package cz.tobice.projects.diskspaceprofiler.app

import java.util.*
import java.util.logging.Logger
import kotlin.reflect.full.companionObject

fun getDisplayFileSize(sizeInBytes: Long): String {
    var displaySize: Double = sizeInBytes.toDouble()
    val stack = Stack<String>()
    stack.addAll(listOf("B", "kB", "MB", "GB").reversed())
    while (displaySize > 1000 && stack.size > 1) {
        displaySize /= 1000
        stack.pop()
    }
    return "${Math.round(displaySize)} ${stack.pop()}"
}

/**
 * Property delegate for easy instantiation of Logger instance.
 *
 * <p>Source: https://stackoverflow.com/a/34462577/576997
 */
fun <R : Any> R.logger(): Lazy<Logger> {
    return lazy { Logger.getLogger(unwrapCompanionClass(this.javaClass).name) }
}

fun <T : Any> unwrapCompanionClass(ofClass: Class<T>): Class<*> {
    return ofClass.enclosingClass?.takeIf {
        ofClass.enclosingClass.kotlin.companionObject?.java == ofClass
    } ?: ofClass
}
