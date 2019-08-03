package com.example.demo.app

import tornadofx.*
import java.io.File

/** TODO(tobik): Add JavaDoc here. */
class Scanner : Component(), ScopedInstance {
    fun createScanTask(rootDirectory: File): ScanTask {
        return ScanTask(rootDirectory)
    }

    fun startScanTask(scanTask: ScanTask) {
        Thread(scanTask).start()
    }
}