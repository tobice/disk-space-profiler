package com.example.demo.app

/** TODO(tobik): Add JavaDoc here. */
class Throttler(val delayMillis : Int) {

    private var lastInvocationMillis: Long = 0

    fun invoke(callable: () -> Unit) {
        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis - lastInvocationMillis > delayMillis) {
            callable()
            lastInvocationMillis = currentTimeMillis
        }
    }
}
