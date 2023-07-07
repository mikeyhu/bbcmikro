package net.chompsoftware.bbcmikro.hardware

import net.chompsoftware.bbcmikro.hardware.video.Screen
import net.chompsoftware.k6502.hardware.Memory
import net.chompsoftware.k6502.hardware.Operation

class Microsystem(val memory: Memory) {

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
        processor.start(repaint)
    }
}