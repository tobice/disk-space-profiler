package com.example.demo.app

import tornadofx.*
import java.io.File
import com.example.demo.app.AppViewModel.Status

class AppController : Controller() {
    private val appViewModel : AppViewModel by inject()
    private val selectedNodeModel: SelectedNodeModel by inject()

    private val throttler = Throttler(200)
    private var scanTask : ScanTask? = null

    fun setTargetDirectory(file: File) {
        appViewModel.targetDirectory.value = file
    }

    fun changeToChildDirectory(node: Node) {
        // TODO: check if it's actually a child.

        appViewModel.directoryStack.add(selectedNodeModel.item)
        selectedNodeModel.item = node
    }

    fun changeToParentDirectory() {
        val nodeStack = appViewModel.directoryStack
        if (!nodeStack.isEmpty()) {
            selectedNodeModel.item = nodeStack.last()
            nodeStack.removeAt(nodeStack.lastIndex)
        }
    }

    fun startScanning() {
        // TODO: make sure that target directory is set.
        setStatus(Status.SCANNING_IN_PROGRESS)

        val scanTask = ScanTask(appViewModel.targetDirectory.value)

        scanTask.setOnCancelled {
            println("cancelled")
            setStatus(Status.SCANNING_CANCELLED)
        }

        scanTask.setOnFailed{
            println("failed")
            scanTask.exception.printStackTrace() // This is normally swallowed.
            setStatus(Status.SCANNING_FAILED)
        }

        scanTask.setOnSucceeded{
            println("succeeded")
            setStatus(Status.SCANNING_FINISHED)
            selectedNodeModel.item = scanTask.rootNode
        }

        appViewModel.runningSize.value = 0
        scanTask.runningSizeProperty.addListener(
                ChangeListener { _, _, value ->
                    throttler.invoke {
                        runLater {
                            appViewModel.runningSize.value = value.toLong()
                        }
                    }
                })

        Thread(scanTask).start()
        this.scanTask = scanTask
    }

    fun cancelScanning() {
        scanTask?.cancel()
    }

    private fun setStatus(status: Status) {
        appViewModel.status.value = status
    }
}
