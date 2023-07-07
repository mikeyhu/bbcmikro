package net.chompsoftware.bbcmikro.hardware

import net.chompsoftware.bbcmikro.Configuration
import net.chompsoftware.bbcmikro.hardware.video.Screen
import net.chompsoftware.k6502.hardware.Memory
import net.chompsoftware.k6502.hardware.Operation
import kotlin.math.max

class Microsystem(val memory: Memory) {

    private var limitSpeed = true
    private var limitToFPS = 60
    private val millisecondsPerFrame = 1000 / limitToFPS
    private val instructionsPerFrame = Configuration.cycleSpeed / limitToFPS
    private var tick: Long = 0
    private var frameStartMillis = System.currentTimeMillis()

    init {
        (0 until 0xf).forEach { index ->
            memory[0xFE40 + index] = 0x00u
            memory[0xFE60 + index] = 0x00u
        }

        memory[0xFE40] = 0xFFu
        memory[0xFE43] = 0xFFu
        memory[0xFE60] = 0xFFu
        memory[0xFE63] = 0xFFu
    }

    val screen = Screen(memory)

    var processor = Processor(Operation, memory)

    fun run(repaint: () -> Unit) {
        processor.start(callbackInterceptor(repaint))
    }

    private fun callbackInterceptor(onNMICallback: () -> Unit): () -> Boolean = {
        var interrupt = false
        if (limitSpeed) {
            tick++
            if (tick > instructionsPerFrame) {
                onNMICallback()
                val currentMillis = System.currentTimeMillis()
                val sleepFor = max(millisecondsPerFrame - (currentMillis - frameStartMillis), 0)
                Logging.debug { "sleepFor ${sleepFor}ms. millisecondsPerFrame: $millisecondsPerFrame, currentMillis: $currentMillis, frameStartMillis: $frameStartMillis" }
                tick = 0
                interrupt = true
                Thread.sleep(sleepFor)

                frameStartMillis = System.currentTimeMillis()
            }
        }

        interrupt
    }
}