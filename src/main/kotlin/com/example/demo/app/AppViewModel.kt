package com.example.demo.app

import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.property.SimpleListProperty
import tornadofx.*
import java.io.File

class AppViewModel : ViewModel() {
    private val status = ReadOnlyObjectWrapper<AppStatus>()
    private val targetDirectory = ReadOnlyObjectWrapper<File>()
    private val selectedNode = ReadOnlyObjectWrapper<Node>()

    // val nodes ObservableList<Node>: bind { SimpleListProperty(selectedNode.readOnlyProperty.select { it -> it.childNodesForUi }) }

    private var scanTask : ScanTask? = null

    init {
        status.value = AppStatus.WELCOME_SCREEN
        selectedNode.value = Node(File("/"))
    }

    fun getStatus(): ReadOnlyObjectProperty<AppStatus> = status.readOnlyProperty

    fun getTargetDirectory(): ReadOnlyObjectProperty<File> = targetDirectory.readOnlyProperty

    fun getSelectedNode(): ReadOnlyObjectProperty<Node> = selectedNode.readOnlyProperty

    fun setTargetDirectory(file: File) {
        targetDirectory.value = file
    }

    fun startScanning() {
        status.value = AppStatus.SCANNING_IN_PROGRESS

        val scanTask = ScanTask(targetDirectory.value)

        selectedNode.value = scanTask.rootNode

        scanTask.setOnCancelled {
            println("cancelled")
            status.value = AppStatus.SCANNING_CANCELLED
        }

        scanTask.setOnFailed{
            println("failed")
            scanTask.exception.printStackTrace()
            status.value = AppStatus.SCANNING_FAILED
        }

        scanTask.setOnSucceeded{
            println("succeeded")
            status.value = AppStatus.SCANNING_FINISHED
        }

        Thread(scanTask).start()

        this.scanTask = scanTask
    }

    fun cancelScanning() {
        scanTask?.cancel()
    }
}
