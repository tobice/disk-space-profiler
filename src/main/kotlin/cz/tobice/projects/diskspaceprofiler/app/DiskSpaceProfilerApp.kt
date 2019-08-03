package cz.tobice.projects.diskspaceprofiler.app

import cz.tobice.projects.diskspaceprofiler.view.AppView
import javafx.application.Platform
import tornadofx.App

class DiskSpaceProfilerApp: App(AppView::class, Styles::class) {
    override fun stop() {
        super.stop()
        // To kill the scan task in the background if needed.
        Platform.exit()
        System.exit(0)
    }
}