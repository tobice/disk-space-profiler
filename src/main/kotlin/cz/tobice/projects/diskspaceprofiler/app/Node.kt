package cz.tobice.projects.diskspaceprofiler.app

import java.io.File

/**
 * Data object that represents either a scanned file or a directory.
 *
 * <p>If the node is a file, it's {@param size} is the file size and {@param sortedChildNodes} are empty.
 * If it's a directory, it's size is a sum of sizes of all files in the directory and its
 * subdirectories and {@param sortedChildNodes} contains immediate files and directories of this
 * directory.
 *
 * <p>{@param file} is a reference to the actual file on the file system.
 *
 * <p>This is a plain data holder which means that whoever creates it is responsible for
 * instantiating it properly.
 */
data class Node(val file: File, val size: Long = 0, val childNodes: List<Node> = emptyList())
