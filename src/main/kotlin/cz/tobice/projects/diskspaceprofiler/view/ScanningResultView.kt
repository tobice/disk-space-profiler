package cz.tobice.projects.diskspaceprofiler.view

import cz.tobice.projects.diskspaceprofiler.app.AppController
import cz.tobice.projects.diskspaceprofiler.app.AppViewModel
import cz.tobice.projects.diskspaceprofiler.app.SelectedNodeModel
import cz.tobice.projects.diskspaceprofiler.app.getDisplayFileSize
import javafx.scene.layout.Priority
import tornadofx.*

class ScanningResultView : View() {
    private val appViewModel: AppViewModel by inject()
    private val appController: AppController by inject()
    private val selectedNodeModel: SelectedNodeModel by inject()

    override val root = borderpane {
        id = "scanningResultView"
        gridpaneConstraints {
            fillWidth = true
            hgrow = Priority.ALWAYS
        }
        top = button("Go up") {
            enableWhen(appViewModel.directoryStack.sizeProperty().greaterThan(0))
            action {
                appController.changeToParentDirectory()
            }
        }
        center = scrollpane {
            // TODO(tobik): Make the scroll pane fill the whole width.
            //  ('Cause that's the real challenge around here.)
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
                        // This weird trick aligns the displayed size to the right. Legit.
                        spacer {
                            hboxConstraints {
                                hGrow = Priority.ALWAYS
                            }
                        }
                        label(getDisplayFileSize(it.size))
                    }
                }
            }
        }
    }
}
