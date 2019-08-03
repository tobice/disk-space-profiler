package cz.tobice.projects.diskspaceprofiler.view

import cz.tobice.projects.diskspaceprofiler.app.AppController
import cz.tobice.projects.diskspaceprofiler.app.AppViewModel
import cz.tobice.projects.diskspaceprofiler.app.SelectedNodeModel
import cz.tobice.projects.diskspaceprofiler.app.getDisplayFileSize
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
