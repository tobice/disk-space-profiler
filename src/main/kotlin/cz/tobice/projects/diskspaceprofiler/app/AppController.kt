package cz.tobice.projects.diskspaceprofiler.app

import tornadofx.*
import java.io.File
import cz.tobice.projects.diskspaceprofiler.app.AppViewModel.Status

// TODO(tobik): Consider strict vs forgiving behavior for forbidden operations.
class AppController : Controller() {
    private val appViewModel : AppViewModel by inject()
    private val selectedNodeModel: SelectedNodeModel by inject()

    private val scanner: Scanner by inject()

    private val throttler = Throttler(200)
    private var scanTask : ScanTask? = null

    fun setTargetDirectory(file: File) {
        appViewModel.targetDirectory.value = file
    }

    fun changeToChildDirectory(node: Node) {
        // TODO: check if it's actually a child.
        // TODO: Check the status

        appViewModel.directoryStack.add(selectedNodeModel.item)
        selectedNodeModel.item = node
    }

    fun changeToParentDirectory() {
        val nodeStack = appViewModel.directoryStack
        if (!nodeStack.isEmpty()) {
            selectedNodeModel.item = nodeStack.last()
            nodeStack.removeAt(nodeStack.lastIndex)
        }
    }

    fun startScanning() {
        // TODO: Make sure that target directory is set.

        setStatus(Status.SCANNING_IN_PROGRESS)
        appViewModel.runningSize.value = 0
        appViewModel.directoryStack.clear()

        val scanTask = scanner.createScanTask(appViewModel.targetDirectory.value)

        // TODO: Always check that the current status is SCANNING_IN_PROGRESS.

        scanTask.setOnCancelled {
            println("Scanning cancelled")
            setStatus(Status.SCANNING_CANCELLED)
        }

        scanTask.setOnFailed {
            println("Scanning failed")
            scanTask.exception?.printStackTrace() // This is normally swallowed.
            setStatus(Status.SCANNING_FAILED)
        }

        scanTask.setOnSucceeded {
            println("Scanning succeeded")
            setStatus(Status.SCANNING_FINISHED)
            selectedNodeModel.item = scanTask.get()
        }

        scanTask.onRunningSizeUpdated = { runningSize ->
            throttler.invoke {
                runLater {
                    appViewModel.runningSize.value = runningSize
                }
            }
        }

        this.scanTask = scanTask

        scanner.startScanTask(scanTask)
    }

    fun cancelScanning() {
        scanTask?.cancel()
    }

    private fun setStatus(status: Status) {
        appViewModel.status.value = status
    }
}
