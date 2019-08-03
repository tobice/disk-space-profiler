package cz.tobice.projects.diskspaceprofiler.view

import cz.tobice.projects.diskspaceprofiler.app.AppViewModel.Status
import cz.tobice.projects.diskspaceprofiler.app.AppController
import cz.tobice.projects.diskspaceprofiler.app.AppViewModel
import tornadofx.*

class AppView : View("Hello World!") {
    private val appViewModel: AppViewModel by inject()
    private val appController: AppController by inject()

    private val welcomeView: WelcomeView by inject()
    private val scanningResultView: ScanningResultView by inject()
    private val scanningInProgressView: ScanningInProgressView by inject()

    private val status = appViewModel.status

    override val root = vbox {
        hbox {
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
        label(appViewModel.targetDirectory)

        hbox {
            status.onChange {
                children.clear()
                when (status.value) {
                    Status.WELCOME_SCREEN -> add(welcomeView)
                    Status.SCANNING_FINISHED -> add(scanningResultView)
                    Status.SCANNING_IN_PROGRESS -> add(scanningInProgressView)
                    else -> label("Unsupported status: " + status.value)
                }
            }
            add(welcomeView)
        }
    }
}
