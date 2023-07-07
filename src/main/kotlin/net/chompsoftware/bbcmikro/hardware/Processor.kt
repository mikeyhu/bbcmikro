package net.chompsoftware.bbcmikro.hardware

import net.chompsoftware.bbcmikro.Configuration
import net.chompsoftware.bbcmikro.utils.Timer
import net.chompsoftware.k6502.hardware.BREAK_LOCATION
import net.chompsoftware.k6502.hardware.CpuState
import net.chompsoftware.k6502.hardware.EffectPipeline
import net.chompsoftware.k6502.hardware.Memory
import net.chompsoftware.k6502.hardware.OperationState
import kotlin.concurrent.thread
import kotlin.math.max

class Processor(
    private val operation: EffectPipeline,
    private val memory: Memory,
    private val cpuState: CpuState = initialCpuState(memory),
    private val operationState: OperationState = OperationState(0)
) {
    private var nextPipeline: EffectPipeline? = null
    private var limitSpeed = true
    private var limitToFPS = 60


    private var previousCallbackMillis: Long = System.currentTimeMillis()
    private val millisecondsPerFrame = 1000 / limitToFPS
    private val instructionsPerFrame = Configuration.cycleSpeed / limitToFPS
    private val timer = Timer("6502 processor")

    private var tick: Long = 0
    private var frameStartMillis = System.currentTimeMillis()

    fun start(callback: () -> Unit) {
        thread {
            try {
                while (true) {
                    cycle(onNMICallback = callback)
                }
            } catch (e: Throwable) {
                Logging.error(e)
            }
            Logging.error { "Thread finished due to error" }
        }
    }


    fun cycle(onNMICallback: () -> Unit) {
        val isNMIInterrupt = callbackInterceptor(onNMICallback)()
        if (isNMIInterrupt) {
            cpuState.isNMIInterrupt = true
        }
        timer.increment()
        nextPipeline = (nextPipeline ?: operation).run(cpuState, memory, operationState)
//        Logging.debug { "tick" }
    }

    private fun callbackInterceptor(onNMICallback: () -> Unit): () -> Boolean = {
        var interrupt = false
        if (limitSpeed) {
            tick++
//            Logging.debug { "interceptor elapsed = $elapsed, currentMillis = $currentMillis" }
            if (tick > instructionsPerFrame) {
                onNMICallback()
                val currentMillis = System.currentTimeMillis()
                val sleepFor = max(millisecondsPerFrame - (currentMillis - frameStartMillis), 0)
                tick = 0

                Logging.debug { "sleeping for ${sleepFor}ms" }
                interrupt = true
                Thread.sleep(sleepFor)
//                Logging.debug { "cpu calling for repaint" }

                previousCallbackMillis = System.currentTimeMillis()
            }
        }

        interrupt
    }


    companion object {
        private fun initialCpuState(memory: Memory) = CpuState(
            programCounter = 0xd9cd,
            breakLocation = BREAK_LOCATION
        )
    }
}