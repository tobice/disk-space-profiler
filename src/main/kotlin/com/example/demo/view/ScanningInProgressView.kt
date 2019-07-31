package com.example.demo.view

import com.example.demo.app.AppViewModel
import tornadofx.*
import java.util.*

// TODO: move to a better place
fun getDisplayFileSize(sizeInBytes: Long): String {
    var displaySize: Double = sizeInBytes.toDouble()
    val stack = Stack<String>()
    stack.addAll(listOf("B", "kB", "MB", "GB").reversed())
    while (displaySize > 1000 && stack.size > 1) {
        displaySize /= 1000
        stack.pop()
    }
    return "$displaySize ${stack.pop()}"
}

class ScanningInProgressView : View() {
    private val appViewModel: AppViewModel by inject()

    override val root = vbox {
        label(appViewModel.getRunningSize().stringBinding { getDisplayFileSize(it?.toLong()?:0) })
    }
}
