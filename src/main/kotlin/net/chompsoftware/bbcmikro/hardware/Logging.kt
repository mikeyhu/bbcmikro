package net.chompsoftware.bbcmikro.hardware

import java.io.File
import java.io.PrintWriter


const val LOGLEVEL: Int = 4
const val ERROR = LOGLEVEL > 0
const val WARN = LOGLEVEL > 1
const val VERBOSE = LOGLEVEL > 2
const val DEBUG = LOGLEVEL > 3

const val logfileName = "/tmp/k6502.log"

object Logging {
    lateinit var log: PrintWriter

    private var loggingEnabled = false

    fun enableLogging() {
        if(LOGLEVEL > 0) {
            File(logfileName).delete()
            log = File(logfileName).printWriter()
        }
        loggingEnabled = true
    }

    fun error(message: String) {
        if(ERROR && loggingEnabled) log.println(message)
    }

    fun debug(message: String) {
        if(DEBUG && loggingEnabled) log.println(message)
    }

    fun verbose(message: String) {
        if(VERBOSE && loggingEnabled) log.println(message)
    }
}