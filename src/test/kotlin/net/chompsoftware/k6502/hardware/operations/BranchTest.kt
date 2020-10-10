package net.chompsoftware.k6502.hardware.operations

import io.kotest.matchers.shouldBe
import net.chompsoftware.k6502.hardware.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@ExperimentalUnsignedTypes
class BranchTest {
    @Nested
    inner class BranchOnNotEqual {
        @Test
        fun `Should branch if zeroFlag is false`() {
            val memory = Memory(setupMemory(InstructionSet.bne.u, 0x02u))
            val state = CpuState(
                    isZeroFlag = false
            )
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    programCounter = 0x04
            )
        }

        @Test
        fun `Should branch backwards if zeroFlag is false and location greater than 0x80`() {
            val memory = Memory(setupMemory(0x0u, InstructionSet.bne.u, 0xfdu))
            val state = CpuState(
                    programCounter = 1,
                    isZeroFlag = false
            )
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    programCounter = 0x00
            )
        }

        @Test
        fun `Should not branch if zeroFlag is true`() {
            val memory = Memory(setupMemory(InstructionSet.bne.u, 0x02u))
            val state = CpuState(
                    isZeroFlag = true
            )
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    programCounter = 0x02
            )
        }
    }

    @Nested
    inner class BranchOnEqual {
        @Test
        fun `Should branch if zeroFlag is true`() {
            val memory = Memory(setupMemory(InstructionSet.beq.u, 0x02u))
            val state = CpuState(
                    isZeroFlag = true
            )
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    programCounter = 0x04
            )
        }

        @Test
        fun `Should not branch if zeroFlag is false`() {
            val memory = Memory(setupMemory(InstructionSet.beq.u, 0x02u))
            val state = CpuState(
                    isZeroFlag = false
            )
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    programCounter = 0x02
            )
        }
    }

    @Nested
    inner class BranchOnPlus {
        @Test
        fun `Should branch if negativeFlag is false`() {
            val memory = Memory(setupMemory(InstructionSet.bpl.u, 0x02u))
            val state = CpuState(
                    isNegativeFlag = false
            )
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    programCounter = 0x04
            )
        }

        @Test
        fun `Should not branch if negativeFlag is true`() {
            val memory = Memory(setupMemory(InstructionSet.bpl.u, 0x02u))
            val state = CpuState(
                    isNegativeFlag = true
            )
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    programCounter = 0x02
            )
        }
    }
}