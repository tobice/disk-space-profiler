package cz.tobice.projects.diskspaceprofiler.app

import java.util.*
import java.util.logging.Logger
import kotlin.reflect.full.companionObject

/** Converts size in bytes into a more human readable version with proper units. */
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

/** A simple throttler utility that invokes the callable at most every {@code delayMillis}. */
class Throttler(val delayMillis: Int) {

    private var lastInvocationMillis: Long = 0

    fun invoke(callable: () -> Unit) {
        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis - lastInvocationMillis > delayMillis) {
            callable()
            lastInvocationMillis = currentTimeMillis
        }
    }
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
