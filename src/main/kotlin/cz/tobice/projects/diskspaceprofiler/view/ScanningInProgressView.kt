package cz.tobice.projects.diskspaceprofiler.view

import cz.tobice.projects.diskspaceprofiler.app.AppViewModel
import cz.tobice.projects.diskspaceprofiler.app.getDisplayFileSize
import tornadofx.*

class ScanningInProgressView : View() {
    private val appViewModel: AppViewModel by inject()

    override val root = vbox {
        id = "scanningInProgressView"
        label(appViewModel.runningSize.stringBinding { getDisplayFileSize(it?.toLong()?:0) })
    }
}
