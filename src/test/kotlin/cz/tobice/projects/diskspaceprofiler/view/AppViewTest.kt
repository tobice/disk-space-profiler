package cz.tobice.projects.diskspaceprofiler.view

import cz.tobice.projects.diskspaceprofiler.app.AppController
import cz.tobice.projects.diskspaceprofiler.app.AppViewModel.Status
import cz.tobice.projects.diskspaceprofiler.app.AppViewModel
import javafx.event.ActionEvent
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Labeled
import javafx.stage.Stage
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.testfx.api.FxAssert.verifyThat
import org.testfx.framework.junit5.ApplicationTest
import org.testfx.matcher.base.NodeMatchers
import org.testfx.matcher.control.LabeledMatchers
import tornadofx.*
import java.io.File
import kotlin.test.assertTrue


class AppViewTest : ApplicationTest() {
    private lateinit var appViewModel: AppViewModel
    private lateinit var appView: AppView
    private lateinit var appController: AppController

    override fun start(stage: Stage) {
        appViewModel = AppViewModel()
        setInScope(appViewModel)

        appController = Mockito.mock(AppController::class.java)
        setInScope(appController)

        appView = AppView()

        interact {
            stage.scene = Scene(appView.root)
            stage.show()
            stage.toFront()
        }
    }


    @Test fun welcomeScreen_welcomeMessageShown() {
        verifyThat(labelWithText("Welcome to Disk File Profiler"), NodeMatchers.isVisible())
    }

    @Test fun welcomeScreen_chooseDirectoryToAnalyzeButton_enabled() {
        verifyThat(buttonByLabel("Choose directory to analyze")) { b -> !b.isDisabled }
    }

    @Test fun welcomeScreen_startScanningButton_disabled() {
        verifyThat(buttonByLabel("Start scanning")) { b -> b.isDisabled }
    }

    @Test fun scanningInProgress_chooseDirectoryToAnalyzeButton_disabled() {
        interact {
            appViewModel.status.value = Status.SCANNING_IN_PROGRESS
        }
        verifyThat(buttonByLabel("Choose directory to analyze")) { b -> b.isDisabled }
    }

    @Test fun clickStartScanningButton_withSelectedTargetDirectory_startsScanning() {
        interact {
            appViewModel.targetDirectory.value = File("random path")
            clickOn(buttonByLabel("Start scanning"))
        }

        verify(appController).startScanning()
    }

    // TODO(tobik): Cover the rest of the behaviors


    private fun buttonByLabel(label: String): Button {
        return lookup(LabeledMatchers.hasText(label)).queryButton()
    }

    private fun labelWithText(text: String): Labeled {
        return lookup(LabeledMatchers.hasText(text)).queryLabeled()
    }

    /**
     * Custom clickOn helper which directly invokes the action handler on the button. Unfortunately,
     * the TestFx's clickOn on does work reliably.
     */
    private fun clickOn(button: Button) {
        button.onAction.handle(ActionEvent(null, null))
    }
}
