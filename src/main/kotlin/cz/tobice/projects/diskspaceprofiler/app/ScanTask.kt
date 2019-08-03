package cz.tobice.projects.diskspaceprofiler.app

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
        return when {
            // We need to check for symbolic links first as they can also be directories.
            Files.isSymbolicLink(file.toPath()) -> Node(file, size = 0)
            file.isFile -> {
                runningSize += file.length()
                onRunningSizeUpdated?.invoke(runningSize)
                Node(file, file.length())
            }
            file.isDirectory -> {
                if (isCancelled) {
                    return Node(file, size = 0)
                }
                val childNodes = file.listFiles()?. mapNotNull (this::scanFile) ?: emptyList()
                Node(file, childNodes.map { node -> node.size }.sum(), childNodes)
            }
            else -> {
                println("Found $file which is neither an ordinary file nor a directory. " +
                        "Not sure what to do.")
                Node(file, size = 0)
            }
        }
    }
}
