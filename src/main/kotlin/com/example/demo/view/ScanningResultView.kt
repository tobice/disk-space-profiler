package com.example.demo.view

import com.example.demo.app.AppViewModel
import com.example.demo.app.SelectedNodeModel
import tornadofx.*

class ScanningResultView : View() {
    private val appViewModel: AppViewModel by inject()
    private val selectedNodeModel : SelectedNodeModel by inject()

    override val root = vbox {
        label("Display files below")
        button("Go up") {
            enableWhen(appViewModel.nodeStack.sizeProperty().greaterThan(0))
            action {
                appViewModel.goUp()
            }
        }
        vbox {
            bindChildren(selectedNodeModel.childNodes) {
                hbox {
                    if (it.file.isDirectory) {
                        hyperlink(it.file.name) {
                            action {
                                appViewModel.setSelectedNode(it)
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
