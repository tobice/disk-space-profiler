package com.example.demo.app

import javafx.beans.property.SimpleLongProperty
import javafx.concurrent.Task
import tornadofx.*
import java.io.File
import java.nio.file.Files

/** TODO(tobik): Add JavaDoc here. */
class ScanTask(private val rootDirectory: File) : Task<Unit>() {

    var runningSizeProperty = SimpleLongProperty()
    var rootNode : Node? = null

    override fun call() {
        rootNode = scanFile(rootDirectory)
    }

    private fun scanFile(file : File) : Node? {
        if (file.isFile) {
            runningSizeProperty.value += file.length()
        }

        return when {
            isCancelled -> null
            Files.isSymbolicLink(file.toPath()) || file.isFile -> Node(file)
            file.isDirectory ->
                Node(file, file.listFiles()?.mapNotNull(this::scanFile) ?: emptyList())
            else -> {
                println("Found $file which is neither an ordinary file nor a directory. " +
                        "Not sure what to do.")
                return null
            }
        }
    }
}
