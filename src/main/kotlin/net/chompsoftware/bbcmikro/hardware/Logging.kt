package net.chompsoftware.bbcmikro.hardware

import java.io.File
import java.io.PrintWriter

const val logfileName = "/tmp/bbcmikro.log"

object Logging {
    private val logSpeedReporter = true
    private val writeToFile = false

    private val log: PrintWriter = if (writeToFile) {
        File(logfileName).delete()
        File(logfileName).printWriter()
    } else {
        PrintWriter(System.out, true)
    }
    private val logLevel = 2

    private val loggingEnabled = true

    fun error(error: Throwable) {
        if (loggingEnabled && logError()) {
            log.println("Logging an exception: $error")
            error.printStackTrace()
        }
    }

    fun error(functionToMessage: () -> String) {
        if (loggingEnabled && logError()) log.println("ERROR " + functionToMessage())
    }

    fun warn(functionToMessage: () -> String) {
        if (loggingEnabled && logWarn()) log.println("WARN  " + functionToMessage())
    }

    fun debug(functionToMessage: () -> String) {
        if (loggingEnabled && logDebug()) log.println("DEBUG " + functionToMessage())
    }

    fun info(functionToMessage: () -> String) {
        if (loggingEnabled && logInfo()) log.println("INFO  " + functionToMessage())
    }

    fun speedReporter(functionToMessage: () -> String) {
        if (loggingEnabled && logSpeedReporter) log.println("SPEED " + functionToMessage())
    }

    private fun logError() = logLevel > 0
    private fun logWarn() = logLevel > 1
    private fun logInfo() = logLevel > 2
    private fun logDebug() = logLevel > 3
}