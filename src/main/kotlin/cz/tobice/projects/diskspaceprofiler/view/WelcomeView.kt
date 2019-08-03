package cz.tobice.projects.diskspaceprofiler.view

import tornadofx.*

class WelcomeView : View() {
    override val root = hbox {
        id = "welcomeView"

        label("Welcome to Disk File Profiler")
    }
}
