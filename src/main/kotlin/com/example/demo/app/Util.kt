package com.example.demo.app

import java.util.*

fun getDisplayFileSize(sizeInBytes: Long): String {
    var displaySize: Double = sizeInBytes.toDouble()
    val stack = Stack<String>()
    stack.addAll(listOf("B", "kB", "MB", "GB").reversed())
    while (displaySize > 1000 && stack.size > 1) {
        displaySize /= 1000
        stack.pop()
    }
    return "$displaySize ${stack.pop()}"
}
