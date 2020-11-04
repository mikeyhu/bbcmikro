package net.chompsoftware.k6502.hardware

import net.chompsoftware.k6502.hardware.video.Screen
import java.io.File

const val BBC_6502_CYCLE_SPEED = 2000000L

const val INTERRUPT = 10 //ms
const val INTERRUPT_CYCLES = BBC_6502_CYCLE_SPEED / 1000 * INTERRUPT

const val NANO_INTERRUPT = 10 * 1000000

@ExperimentalUnsignedTypes
class Microsystem(val memory: RamInterface) {

    init {
        (0 until 0xf).forEach { index ->
            memory[0xFE40 + index] = 0x00u
            memory[0xFE60 + index] = 0x00u
        }

        memory[0xFE40] = 0xFFu
        memory[0xFE43] = 0xFFu
        memory[0xFE60] = 0xFFu
        memory[0xFE63] = 0xFFu

        Logging.enableLogging()
    }

    val screen = Screen(memory)

    val cpu = Cpu()

    var cpuState = CpuState(
            programCounter = 0xd9cd,
            breakLocation = 0xfffe
    )

    var nextInterrupt = INTERRUPT_CYCLES
    var nextPause = System.nanoTime() + NANO_INTERRUPT

    var interruptCount = 0

    var startLogging = true

    fun run() {
        var interrupted = false
        while (!interrupted) {
            try {
                cpuState = cpu.run(cpuState, memory)
                if(startLogging) Logging.verbose("n: ${InstructionSet.from(memory[cpuState.programCounter])} p:${memory[0xf4].toHex()} - $cpuState")

                if (nextInterrupt < cpuState.cycleCount) {
                    interruptCount += 1
                    nextInterrupt += INTERRUPT_CYCLES
                    if (!cpuState.isInterruptDisabledFlag) {
                        cpuState = cpu.interrupt(cpuState, memory)
                        startLogging = true
                        Logging.verbose("int to: ${InstructionSet.from(memory[cpuState.programCounter])} p:${memory[0xf4].toHex()} - $cpuState")
                    }
                }

            } catch (error: Error) {
                println("Error occurred : ${error}")
                Logging.error("Error occurred : ${error}")
            }

            if (System.nanoTime() > nextPause) {
                nextPause += NANO_INTERRUPT
                interrupted = true
            }
        }
    }
}