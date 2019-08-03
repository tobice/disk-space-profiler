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
import kotlin.collections.ArrayList

data class Node(val file: File, val childNodes: List<Node> = emptyList()) {
    val size: Long = if (file.isFile) file.length() else childNodes.map { node -> node.size }.sum()
}
