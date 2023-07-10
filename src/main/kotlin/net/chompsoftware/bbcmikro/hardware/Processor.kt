package net.chompsoftware.bbcmikro.hardware

import net.chompsoftware.bbcmikro.utils.Timer
import net.chompsoftware.k6502.hardware.BREAK_LOCATION
import net.chompsoftware.k6502.hardware.CpuState
import net.chompsoftware.k6502.hardware.EffectPipeline
import net.chompsoftware.k6502.hardware.Memory
import net.chompsoftware.k6502.hardware.OperationState
import kotlin.concurrent.thread

class Processor(
    private val operation: EffectPipeline,
    private val memory: Memory,
    private val cpuState: CpuState = initialCpuState(memory),
    private val operationState: OperationState = OperationState(0)
) {
    private var nextPipeline: EffectPipeline? = null


    private val timer = Timer("6502 processor")

    fun start(callback: () -> Boolean) {
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

    fun cycle(onNMICallback: () -> Boolean) {
        val isIRQInterrupt = onNMICallback()
        // Don't want to unset this if we've already set it as true
        if (isIRQInterrupt) {
            cpuState.isIRQInterrupt = true
        }
        timer.increment()

        nextPipeline = (nextPipeline ?: operation).run(cpuState, memory, operationState)
    }

    companion object {
        private fun initialCpuState(memory: Memory) = CpuState(
            programCounter = 0xd9cd,
            breakLocation = BREAK_LOCATION
        )
    }
}