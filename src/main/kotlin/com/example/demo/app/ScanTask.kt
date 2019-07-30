package com.example.demo.app

import javafx.concurrent.Task
import java.io.File
import java.nio.file.Files
import java.util.*

/** TODO(tobik): Add JavaDoc here. */
class ScanTask(private val rootDirectory: File) : Task<Unit>() {

    val rootNode = Node(rootDirectory)

    var size: Long = 0

    override fun call() {
        val stack = Stack<Node>()
        stack.add(rootNode)

        while (!stack.empty()) {
            if (isCancelled) {
                return
            }

            val node = stack.pop()
            val file = node.file

            if (file.isFile) {
                size += file.length()
                println(size)
            }

            when {
                Files.isSymbolicLink(file.toPath()) -> {}
                file.isFile -> node.increaseSize(file.length())
                file.isDirectory -> file.listFiles()?.forEach { stack.push(Node(it, node)) }
                else -> println("Found $file which is neither an ordinary file nor a directory. " +
                        "Not sure what to do.")
            }
        }
    }
}
