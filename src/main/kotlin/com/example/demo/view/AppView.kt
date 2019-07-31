package com.example.demo.view

import com.example.demo.app.AppStatus
import com.example.demo.app.AppViewModel
import tornadofx.*

class AppView : View("Hello World!") {
    private val appViewModel: AppViewModel by inject()

    private val welcomeView: WelcomeView by inject()
    private val scanningResultView: ScanningResultView by inject()
    private val scanningInProgressView: ScanningInProgressView by inject()

    private val status = appViewModel.getStatus()

    override val root = vbox {
        hbox {
            button("Choose directory to analyze") {
                enableWhen(status.isNotEqualTo(AppStatus.SCANNING_IN_PROGRESS))
                action {
                    chooseDirectory("Choose the directory to analyze")?.let { file ->
                        appViewModel.setTargetDirectory(file)
                    }
                }
            }
            button("Start scanning") {
                enableWhen(
                        status.isNotEqualTo(AppStatus.SCANNING_IN_PROGRESS)
                                .and(appViewModel.getTargetDirectory().isNotNull))
                action {
                    appViewModel.startScanning()
                }
            }
            button("Cancel scanning") {
                enableWhen(status.isEqualTo(AppStatus.SCANNING_IN_PROGRESS))
                action {
                    appViewModel.cancelScanning()
                }
            }
        }
        label(appViewModel.getTargetDirectory())

        hbox {
            status.onChange {
                children.clear()
                when (status.value) {
                    AppStatus.WELCOME_SCREEN -> add(welcomeView)
                    AppStatus.SCANNING_FINISHED -> add(scanningResultView)
                    AppStatus.SCANNING_IN_PROGRESS -> add(scanningInProgressView)
                    else -> label("don't know what to do")
                }
            }
            add(welcomeView)
        }
    }
}
