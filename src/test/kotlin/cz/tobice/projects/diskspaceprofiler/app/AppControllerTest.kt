package cz.tobice.projects.diskspaceprofiler.app

import cz.tobice.projects.diskspaceprofiler.app.AppViewModel.Status
import javafx.stage.Stage
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.hasItems
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.testfx.framework.junit5.ApplicationTest
import tornadofx.*
import java.io.File
import kotlin.test.assertTrue

class AppControllerTest : ApplicationTest() {
    companion object {
        val SOME_ROOT_DIRECTORY = File("/")
    }

    private lateinit var appViewModel: AppViewModel
    private lateinit var selectedNodeModel: SelectedNodeModel
    private lateinit var scanner: Scanner
    private lateinit var scanTask: ScanTask

    private lateinit var appController: AppController

    override fun start(stage: Stage) {
        appViewModel = AppViewModel()
        setInScope(appViewModel)

        selectedNodeModel = SelectedNodeModel()
        setInScope(selectedNodeModel)

        // Set up a mocked Scanner which returns a real ScanTask which we spy on. Having a real
        // ScanTask is nice because it holds all the handlers and still allows us to invoke them
        // manually so that we can verify controller's reactions.
        scanner = Mockito.mock(Scanner::class.java)
        scanTask = Mockito.spy(ScanTask(SOME_ROOT_DIRECTORY))
        doReturn(scanTask).`when`(scanner).createScanTask(SOME_ROOT_DIRECTORY)
        doReturn(Node(SOME_ROOT_DIRECTORY)).`when`(scanTask).get()
        setInScope(scanner)

        appController = AppController()
    }

    @Test fun setTargetDirectory_updatesViewModel() {
        appController.setTargetDirectory(SOME_ROOT_DIRECTORY)
        assertThat(appViewModel.targetDirectory.value, equalTo(SOME_ROOT_DIRECTORY))
    }

    @Test fun startScanning_startsScanning() {
        appController.setTargetDirectory(SOME_ROOT_DIRECTORY)
        appController.startScanning()
        verify(scanner).startScanTask(scanTask)
    }

    @Test fun startScanning_changesStateToScanningInProgress() {
        appController.setTargetDirectory(SOME_ROOT_DIRECTORY)
        appController.startScanning()
        assertThat(appViewModel.status.value, equalTo(Status.SCANNING_IN_PROGRESS))
    }

    @Test fun startScanning_emptiesDirectoryStack() {
        appViewModel.directoryStack.add(Node(SOME_ROOT_DIRECTORY))
        appController.setTargetDirectory(SOME_ROOT_DIRECTORY)
        appController.startScanning()
        assertTrue { appViewModel.directoryStack.isEmpty() }
    }

    @Test fun startScanning_whenNoTargetDirectoryIsProvided_doesNothing() {
        appController.startScanning()
        assertThat(appViewModel.status.value, equalTo(Status.WELCOME_SCREEN))
    }

    @Test fun cancelScanning_cancelsTask() {
        appController.setTargetDirectory(SOME_ROOT_DIRECTORY)
        appController.startScanning()
        appController.cancelScanning()
        verify(scanTask).cancel()
    }

    @Test fun cancelScanning_whenNotScanning_doesNothing() {
        appController.cancelScanning()
        verify(scanTask, never()).cancel()
    }

    @Test fun scanTask_succeeds_controllerChangesStateToScanningFinished() {
        appController.setTargetDirectory(SOME_ROOT_DIRECTORY)
        appController.startScanning()
        scanTask.onSucceeded.handle(/* event = */ null)
        assertThat(appViewModel.status.value, equalTo(Status.SCANNING_FINISHED))
    }

    @Test fun scanTask_succeeds_controllerUpdatesSelectedNodeModel() {
        appController.setTargetDirectory(SOME_ROOT_DIRECTORY)
        appController.startScanning()
        scanTask.onSucceeded.handle(/* event = */ null)
        assertThat(selectedNodeModel.item, equalTo(Node(SOME_ROOT_DIRECTORY)))
    }

    @Test fun scanTask_fails_controllerChangesStateToScanningFailed() {
        appController.setTargetDirectory(SOME_ROOT_DIRECTORY)
        appController.startScanning()
        scanTask.onFailed.handle(/* event = */ null)
        assertThat(appViewModel.status.value, equalTo(Status.SCANNING_FAILED))
    }

    @Test fun scanTask_isCancelled_controllerChangesStateToScanningCancelled() {
        appController.setTargetDirectory(SOME_ROOT_DIRECTORY)
        appController.startScanning()
        scanTask.onCancelled.handle(/* event = */ null)
        assertThat(appViewModel.status.value, equalTo(Status.SCANNING_CANCELLED))
    }

    @Test fun scanTask_updatesRunningSize_controllerUpdatesViewModel() {
        appController.setTargetDirectory(SOME_ROOT_DIRECTORY)
        appController.startScanning()
        scanTask.onRunningSizeUpdated!!.invoke(12345L)
        interact {} // The running size update happens on UI thread. We need to wait.
        assertThat(appViewModel.runningSize.value, equalTo(12345L))
    }

    @Test fun changeToChildDirectory_updatesSelectedNodeModel() {
        val child = Node(File("some_subdirectory"))
        val selected = Node(SOME_ROOT_DIRECTORY, childNodes = listOf(child))
        selectedNodeModel.item = selected

        appController.changeToChildDirectory(child)

        assertThat(selectedNodeModel.item, equalTo(child))
    }

    @Test fun changeToChildDirectory_addSelectedToStack() {
        val child = Node(File("some_subdirectory"))
        val selected = Node(SOME_ROOT_DIRECTORY, childNodes = listOf(child))
        selectedNodeModel.item = selected

        appController.changeToChildDirectory(child)

        assertThat(appViewModel.directoryStack, hasItems(selected))
    }

    @Test fun changeToDirectory_whichIsNotAChild_doesNothing() {
        val someDirectory = Node(File("some_directory"))
        val selected = Node(SOME_ROOT_DIRECTORY)
        selectedNodeModel.item = selected

        appController.changeToChildDirectory(someDirectory)

        assertThat(selectedNodeModel.item, equalTo(selected))
    }

    @Test fun changeToParentDirectory_updatesSelectedNodeModel() {
        val selected = Node(File("some_subdirectory"))
        val parent = Node(SOME_ROOT_DIRECTORY, childNodes = listOf(selected))
        selectedNodeModel.item = selected
        appViewModel.directoryStack.add(parent)

        appController.changeToParentDirectory()

        assertThat(selectedNodeModel.item, equalTo(parent))
    }

    @Test fun changeToParentDirectory_noParentDirectory_doesNothing() {
        val selected = Node(File("some_subdirectory"))
        selectedNodeModel.item = selected

        appController.changeToParentDirectory()

        assertThat(selectedNodeModel.item, equalTo(selected))
    }
}
