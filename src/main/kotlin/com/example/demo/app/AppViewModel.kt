package com.example.demo.app

import javafx.beans.property.*
import javafx.collections.FXCollections
import tornadofx.*
import java.io.File

class AppViewModel : ViewModel() {
    private val selectedNodeModel: SelectedNodeModel by inject()

    private val throttler = Throttler(200)

    private val status = ReadOnlyObjectWrapper<AppStatus>()
    private val targetDirectory = ReadOnlyObjectWrapper<File>()
    private val runningSize = ReadOnlyLongWrapper()

    val nodeStack = SimpleListProperty<Node>(FXCollections.observableArrayList())

    private var scanTask : ScanTask? = null

    init {
        status.value = AppStatus.WELCOME_SCREEN
    }

    // TODO: change ViewModel to Controller
    // TODO: introduce view models for properties
    // TODO: implement going up

    fun getStatus(): ReadOnlyObjectProperty<AppStatus> = status.readOnlyProperty

    fun getTargetDirectory(): ReadOnlyObjectProperty<File> = targetDirectory.readOnlyProperty

    fun getRunningSize(): ReadOnlyLongProperty = runningSize.readOnlyProperty

    fun setTargetDirectory(file: File) {
        targetDirectory.value = file
    }

    fun setSelectedNode(node: Node) {
        nodeStack.add(selectedNodeModel.item)
        selectedNodeModel.item = node
    }

    fun goUp() {
        if (!nodeStack.isEmpty()) {
            selectedNodeModel.item = nodeStack.last()
            nodeStack.removeAt(nodeStack.lastIndex)
        }
    }

    fun startScanning() {
        status.value = AppStatus.SCANNING_IN_PROGRESS

        val scanTask = ScanTask(targetDirectory.value)

        scanTask.setOnCancelled {
            println("cancelled")
            status.value = AppStatus.SCANNING_CANCELLED
        }

        scanTask.setOnFailed{
            println("failed")
            scanTask.exception.printStackTrace() // This is normally swallowed.
            status.value = AppStatus.SCANNING_FAILED
        }

        scanTask.setOnSucceeded{
            println("succeeded")
            status.value = AppStatus.SCANNING_FINISHED
            selectedNodeModel.item = scanTask.rootNode
        }

        this.scanTask = scanTask

        runningSize.value = 0
        scanTask.runningSizeProperty.addListener(
                ChangeListener { _, _, value ->
                    throttler.invoke {
                        runLater {
                            runningSize.value = value.toLong()
                            println("Listening to change: $value")
                        }
                    }
                })

        Thread(scanTask).start()
    }

    fun cancelScanning() {
        scanTask?.cancel()
    }
}
