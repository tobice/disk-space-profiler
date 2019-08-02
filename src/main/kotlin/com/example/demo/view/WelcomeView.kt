package com.example.demo.view

import tornadofx.*

class WelcomeView : View() {
    override val root = hbox {
        id = "welcomeView"

        label("This is welcome screen")
    }
}
