package net.chompsoftware.bbcmikro.utils

import net.chompsoftware.bbcmikro.hardware.Logging

class Timer(val timedSystem: String, val getCurrentSecond: () -> Long = ::currentSecond) {
    private var currentSecond = getCurrentSecond()
    private var currentCounter = 0
    private var mostRecentFinished = 0

    fun increment() {
        val now = getCurrentSecond()
        if (now == currentSecond) {
            currentCounter++
        } else {
            mostRecentFinished = currentCounter
            currentCounter = 0
            currentSecond = now
            Logging.info { "$timedSystem performed $mostRecentFinished in the previous second" }
        }
    }
}

private fun currentSecond(): Long = System.currentTimeMillis() / 1000