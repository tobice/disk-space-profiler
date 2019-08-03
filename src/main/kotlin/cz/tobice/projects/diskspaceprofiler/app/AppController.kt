package cz.tobice.projects.diskspaceprofiler.app

import tornadofx.*
import java.io.File
import cz.tobice.projects.diskspaceprofiler.app.AppViewModel.Status
import java.util.logging.Level

/**
 * Main app controller which encapsulates the core app logic. It receives signals from the views
 * and updates view models (which creates a nice unidirectional flow within the app).
 *
 * <p>The controller is very robust, protecting the app from ending up in an undefined state. If
 * an unsupported operation is attempted, the controller silently stops it without crashing the app.
 */
class AppController : Controller() {
    private val logger by logger()

    private val appViewModel : AppViewModel by inject()
    private val selectedNodeModel: SelectedNodeModel by inject()

    private val scanner: Scanner by inject()
    private val throttler = Throttler(200)

    /** Background task performing the space analysis. */
    private var scanTask : ScanTask? = null

    fun setTargetDirectory(file: File) {
        appViewModel.targetDirectory.value = file
    }

    fun changeToChildDirectory(node: Node) {
        if (!selectedNodeModel.childNodes.contains(node)) {
            log.warning("Can't change to directory. Not a subdirectory")
            return
        }

        appViewModel.directoryStack.add(selectedNodeModel.item)
        selectedNodeModel.item = node
    }

    fun changeToParentDirectory() {
        val nodeStack = appViewModel.directoryStack
        if (nodeStack.isEmpty()) {
            log.warning("Can't change to parent directory. There is no parent directory")
            return
        }

        selectedNodeModel.item = nodeStack.last()
        nodeStack.removeAt(nodeStack.lastIndex)
    }

    fun startScanning() {
        if (appViewModel.targetDirectory.value == null) {
            log.warning("Can't start scanning if the target directory is not set")
            return
        }

        setStatus(Status.SCANNING_IN_PROGRESS)
        appViewModel.runningSize.value = 0
        appViewModel.directoryStack.clear()

        val scanTask = scanner.createScanTask(appViewModel.targetDirectory.value)

        scanTask.setOnCancelled {
            if (getStatus() != Status.SCANNING_IN_PROGRESS) {
                logger.warning("Scan task was cancelled but the status is {${getStatus()}")
            } else {
                logger.info("Scanning cancelled")
                setStatus(Status.SCANNING_CANCELLED)
            }
        }

        scanTask.setOnFailed {
            if (getStatus() != Status.SCANNING_IN_PROGRESS) {
                logger.warning("Scan task failed but the status is {${getStatus()}")
            } else {
                logger.log(Level.SEVERE, "Scanning failed", scanTask.exception)
                setStatus(Status.SCANNING_FAILED)
            }

        }

        scanTask.setOnSucceeded {
            if (getStatus() != Status.SCANNING_IN_PROGRESS) {
                logger.warning("Scan task succeeded but the status is {${getStatus()}")
            } else {
                logger.info("Scanning succeeded")
                setStatus(Status.SCANNING_FINISHED)
                selectedNodeModel.item = scanTask.get()
            }
        }

        scanTask.onRunningSizeUpdated = { runningSize ->
            if (getStatus() != Status.SCANNING_IN_PROGRESS) {
                logger.warning("Running size updated but the status is {${getStatus()}")
            } else {
                throttler.invoke {
                    runLater {
                        appViewModel.runningSize.value = runningSize
                    }
                }
            }
        }

        this.scanTask = scanTask

        scanner.startScanTask(scanTask)
    }

    fun cancelScanning() {
        if (getStatus() != Status.SCANNING_IN_PROGRESS) {
            logger.warning("Can't cancel scanning as scanning is not in progress.")
            return
        }
        scanTask?.cancel()
    }

    private fun getStatus() = appViewModel.status.value

    private fun setStatus(status: Status) {
        appViewModel.status.value = status
    }
}
