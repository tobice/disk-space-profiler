package com.example.demo.app

import com.example.demo.view.AppView
import javafx.application.Platform
import tornadofx.App

class MyApp: App(AppView::class, Styles::class) {
    override fun stop() {
        super.stop()
        // To kill the scan task in the background.
        Platform.exit()
        System.exit(0)
    }
}