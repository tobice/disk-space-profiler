package cz.tobice.projects.diskspaceprofiler.app

import javafx.concurrent.Task
import java.io.File
import java.lang.AssertionError
import java.nio.file.Files

/**
 * Analyzes the size of {@param rootDirectory}.
 *
 * <p>It produces a tree structure of nodes that corresponds to the tree structure of files and
 * directories in {@param rootDirectory}. Each node has a size property of how much space the
 * corresponding file or directory take up on the disk.
 *
 * <p>The returned root {@link Node} corresponds to {@param rootDirectory}.
 *
 * <p>It is implemented as {@link Task} which can be run on a separate thread and can be cancelled.
 *
 * <p>It reports the total running size of all files that have been analyzed so far. When the task
 * finishes, the reported running size corresponds to the actual total size.
 */
class ScanTask(private val rootDirectory: File) : Task<Node>() {

    private var runningSize: Long = 0

    /**
     * The onRunningSizeUpdate handler is invoked whenever a new file is scanned. It receives as a
     * param the updated running size of all files that have been scanned so far.
     */
    var onRunningSizeUpdated: ((runningSize: Long) -> Unit)? = null

    override fun call(): Node {
        return scanFile(rootDirectory)
    }

    /** A simple recursive function that searches through all files in {@code rootDirectory}. */
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
