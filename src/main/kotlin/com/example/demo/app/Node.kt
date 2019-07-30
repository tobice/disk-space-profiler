package com.example.demo.app

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import tornadofx.*
import java.io.File
import java.util.*

class Node(val file: File, val parent: Node? = null) {
    private var sizeInBytes : Long =  0
    private val childNodes = ArrayList<Node>()

    val childNodesForUi : ObservableList<Node> = FXCollections.observableArrayList<Node>()
    val sizeInBytesForUi = SimpleIntegerProperty()

    init {
        childNodesForUi.addListener(ListChangeListener { println("Added node") })
    }

    fun increaseSize(bytes : Long) {
        sizeInBytes += bytes
        parent?.increaseSize(bytes)

        runLater {
            sizeInBytesForUi += bytes
        }
    }

    fun addFile(file : File) : Node {
        val node = Node(file, this)
        childNodes.add(node)

        runLater {
            childNodesForUi.add(node)
        }

        return node
    }
}
