package com.example.demo.view

import com.example.demo.app.AppController
import com.example.demo.app.AppViewModel
import com.example.demo.app.SelectedNodeModel
import com.example.demo.app.getDisplayFileSize
import tornadofx.*

class ScanningResultView : View() {
    private val appViewModel: AppViewModel by inject()
    private val appController: AppController by inject()
    private val selectedNodeModel : SelectedNodeModel by inject()

    override val root = vbox {
        id = "scanningResultView"

        button("Go up") {
            enableWhen(appViewModel.directoryStack.sizeProperty().greaterThan(0))
            action {
                appController.changeToParentDirectory()
            }
        }
        vbox {
            bindChildren(selectedNodeModel.childNodes) {
                hbox {
                    if (it.file.isDirectory) {
                        hyperlink(it.file.name) {
                            action {
                                appController.changeToChildDirectory(it)
                            }
                        }
                    } else {
                        label(it.file.name)
                    }
                    label(getDisplayFileSize(it.size))
                }
            }
        }
    }
}

// TODO: if else
