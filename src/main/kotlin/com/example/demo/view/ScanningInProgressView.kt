package com.example.demo.view

import com.example.demo.app.AppViewModel
import com.example.demo.app.getDisplayFileSize
import tornadofx.*

// TODO: move to a better place

class ScanningInProgressView : View() {
    private val appViewModel: AppViewModel by inject()

    override val root = vbox {
        id = "scanningInProgressView"
        label(appViewModel.runningSize.stringBinding { getDisplayFileSize(it?.toLong()?:0) })
    }
}
