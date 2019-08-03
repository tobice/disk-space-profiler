package cz.tobice.projects.diskspaceprofiler.view

import cz.tobice.projects.diskspaceprofiler.app.AppViewModel.Status
import cz.tobice.projects.diskspaceprofiler.app.AppController
import cz.tobice.projects.diskspaceprofiler.app.AppViewModel
import cz.tobice.projects.diskspaceprofiler.app.getDisplayFileSize
import javafx.scene.Node
import javafx.scene.layout.Priority
import tornadofx.*

class AppView : View("Disk Space Profiler") {
    private val appViewModel: AppViewModel by inject()
    private val appController: AppController by inject()

    private val scanningResultView: ScanningResultView by inject()

    private val status = appViewModel.status

    override val root = borderpane {
        setPrefSize(800.0, 600.0)
        top = hbox {
            label(appViewModel.targetDirectory)
            button("Choose directory to analyze") {
                enableWhen(status.isNotEqualTo(Status.SCANNING_IN_PROGRESS))
                action {
                    chooseDirectory("Choose the directory to analyze")?.let { file ->
                        appController.setTargetDirectory(file)
                    }
                }
            }
            button("Start scanning") {
                enableWhen(
                        status.isNotEqualTo(Status.SCANNING_IN_PROGRESS)
                                .and(appViewModel.targetDirectory.isNotNull))
                action {
                    appController.startScanning()
                }
            }
            button("Cancel scanning") {
                enableWhen(status.isEqualTo(Status.SCANNING_IN_PROGRESS))
                action {
                    appController.cancelScanning()
                }
            }
        }
        center = hbox {
            status.onChange {
                children.clear()
                add(renderMainScreen())
            }
            add(renderMainScreen())
        }
    }

    private fun renderMainScreen(): Node {
        return when (status.value) {
            Status.WELCOME_SCREEN ->
                renderCenteredMessage("Welcome to Disk File Profiler")
            Status.SCANNING_CANCELLED ->
                renderCenteredMessage("Scanning was canceled")
            Status.SCANNING_FAILED ->
                renderCenteredMessage("Scanning failed. Please check the logs for errors")
            Status.SCANNING_IN_PROGRESS -> stackpane {
                hboxConstraints {
                    hGrow = Priority.ALWAYS
                }
                label(appViewModel.runningSize.stringBinding {
                    "So far scanned " + getDisplayFileSize(it?.toLong() ?: 0) + " of data..."
                })
            }
            Status.SCANNING_FINISHED -> scanningResultView.root
            null -> renderCenteredMessage("Unknown status of the app")
        }
    }

    private fun renderCenteredMessage(message: String): Node {
        return stackpane {
            hboxConstraints {
                hGrow = Priority.ALWAYS
            }
            label(message)
        }
    }
}

