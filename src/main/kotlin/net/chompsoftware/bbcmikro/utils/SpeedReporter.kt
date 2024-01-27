package net.chompsoftware.bbcmikro.utils

import net.chompsoftware.bbcmikro.hardware.Logging

class SpeedReporter(val timedSystem: String, val getCurrentSecond: () -> Long = ::currentSecond) {
    private var currentSecond = getCurrentSecond()
    private var currentCounter = 0
    private var mostRecentFinished = 0

    fun increment() {
        increment(1)
    }

    fun increment(amount: Int) {
        val now = getCurrentSecond()
        if (now == currentSecond) {
            currentCounter += amount
        } else {
            mostRecentFinished = currentCounter
            currentCounter = amount
            currentSecond = now
            Logging.speedReporter { "$timedSystem performed $mostRecentFinished in the previous second" }
        }
    }
}

private fun currentSecond(): Long = System.currentTimeMillis() / 1000