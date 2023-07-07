package net.chompsoftware.bbcmikro.hardware

import java.io.File
import java.io.PrintWriter

const val logfileName = "/tmp/k6502.log"

object Logging {
    private val writeToFile = false

    private val log: PrintWriter = if(writeToFile) {
        File(logfileName).delete()
        File(logfileName).printWriter()
    } else {
        PrintWriter(System.out, true)
    }
    private val logLevel = 3

    private val loggingEnabled = true

    fun error(error: Throwable) {
        if (loggingEnabled && logError()) {
            log.println("Logging an exception: $error")
            error.printStackTrace()
        }
    }

    fun error(functionToMessage: () -> String) {
        if (loggingEnabled && logError()) log.println(functionToMessage())
    }

    fun warn(functionToMessage: () -> String) {
        if (loggingEnabled && logWarn()) log.println(functionToMessage())
    }

    fun debug(functionToMessage: () -> String) {
        if (loggingEnabled && logDebug()) log.println(functionToMessage())
    }

    fun info(functionToMessage: () -> String) {
        if (loggingEnabled && logInfo()) log.println(functionToMessage())
    }

    private fun logError() = logLevel > 0
    private fun logWarn() = logLevel > 1
    private fun logInfo() = logLevel > 2
    private fun logDebug() = logLevel > 3
}