package cz.tobice.projects.diskspaceprofiler.app

import java.io.File

// TODO(tobik): Add a JavaDoc
data class Node(val file: File, val size: Long = 0, val childNodes: List<Node> = emptyList())
