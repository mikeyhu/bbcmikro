package net.chompsoftware.bbcmikro.hardware

import net.chompsoftware.bbcmikro.utils.SpeedReporter
import net.chompsoftware.k6502.hardware.BREAK_LOCATION
import net.chompsoftware.k6502.hardware.CpuState
import net.chompsoftware.k6502.hardware.EffectPipeline
import net.chompsoftware.k6502.hardware.Memory
import net.chompsoftware.k6502.hardware.OperationState
import kotlin.concurrent.thread

class Processor(
    private val operation: EffectPipeline,
    private val memory: Memory,
    private val timerManager: TimerManager,
    private val cpuState: CpuState = initialCpuState(),
    private val operationState: OperationState = OperationState(0)
) {
    private val DEFAULT_BATCH_SIZE = 1000
    private var nextPipeline: EffectPipeline? = null


    private val speedReporter = SpeedReporter("6502 processor")

    fun start(callback: () -> InterruptState) {
        thread {
            try {
                while (true) {
                    performOperationsThenCallback(interruptCheck = callback)
                }
            } catch (e: Throwable) {
                Logging.error(e)
            }
            Logging.error { "Thread finished due to error" }
        }
    }

    fun performOperationsThenCallback(interruptCheck: () -> InterruptState) {
        val (cyclesRun, interruptStateFromCycles) = runNextCycleBatch()
        speedReporter.increment(cyclesRun)

        val finalInterruptStates = interruptStateFromCycles + interruptCheck()
        // Don't want to unset this if we've already set it as true
        if (finalInterruptStates.irq) {
            cpuState.isIRQInterrupt = true
        }
        if (finalInterruptStates.nmi) {
            cpuState.isNMIInterrupt = true
        }
    }

    private fun runNextCycleBatch(): Pair<Int, InterruptState> {
        val batchSize = timerManager.availableCpuTicks() ?: DEFAULT_BATCH_SIZE
        Logging.debug { "Running batch of $batchSize cycles" }
        for (i in 0..batchSize) {
            singleCycle()
        }
        val interruptState = timerManager.cpuTick(batchSize)
        return Pair(batchSize, interruptState)
    }

    private fun singleCycle() {
        nextPipeline = (nextPipeline ?: operation).run(cpuState, memory, operationState)
    }

    companion object {
        private fun initialCpuState() = CpuState(
            programCounter = 0xd9cd,
            breakLocation = BREAK_LOCATION
        )
    }
}