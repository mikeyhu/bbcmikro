package net.chompsoftware.k6502.hardware.operations

import io.kotest.matchers.shouldBe
import net.chompsoftware.k6502.hardware.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@ExperimentalUnsignedTypes
class StackTest {

    @Nested
    inner class PushAccumulator {
        @Test
        fun `Should put the accumulator value onto the stack`() {
            val memory = Memory(setupMemory(InstructionSet.pha.u))
            val state = CpuState(
                    aRegister = 0x11u,
                    stackPointer = 0xff
            )
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    programCounter = 0x01,
                    stackPointer = 0xfe
            )
            memory.readUInt(0x1ff) shouldBe 0x11u
        }
    }
}