package com.example.demo.app

import javafx.concurrent.Task
import java.io.File
import java.nio.file.Files

/** TODO(tobik): Add JavaDoc here. */
// TODO(tobik): The actual Task should be an internal private property.
// Now it's possible to start the task manually outside of the Scanner.
class ScanTask(private val rootDirectory: File) : Task<Node>() {

    private var runningSize: Long = 0

    var onRunningSizeUpdated: ((runningSize: Long) -> Unit)? = null

    override fun call(): Node {
        return scanFile(rootDirectory)
    }

    private fun scanFile(file : File) : Node {
        if (file.isFile) {
            runningSize += file.length()
            onRunningSizeUpdated?.invoke(runningSize)
        }

        return when {
            // We need to check for symbolic links first as they can also be directories.
            Files.isSymbolicLink(file.toPath()) || file.isFile -> Node(file)
            file.isDirectory ->
                if (isCancelled) Node(file) else
                    Node(file, file.listFiles()?.mapNotNull(this::scanFile) ?: emptyList())
            else -> {
                println("Found $file which is neither an ordinary file nor a directory. " +
                        "Not sure what to do.")
                Node(file)
            }
        }
    }
}
