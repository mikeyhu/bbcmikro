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
                    cycleCount = 3,
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
                    cycleCount = 3,
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
                    cycleCount = 2,
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
                    cycleCount = 3,
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
                    cycleCount = 2,
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
                    cycleCount = 3,
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
                    cycleCount = 2,
                    programCounter = 0x02
            )
        }
    }

    @Nested
    inner class BranchOnCarryClear {
        @Test
        fun `Should branch if carryFlag is false`() {
            val memory = Memory(setupMemory(InstructionSet.bcc.u, 0x02u))
            val state = CpuState(
                    isCarryFlag = false
            )
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    cycleCount = 3,
                    programCounter = 0x04
            )
        }

        @Test
        fun `Should not branch if carryFlag is true`() {
            val memory = Memory(setupMemory(InstructionSet.bcc.u, 0x02u))
            val state = CpuState(
                    isCarryFlag = true
            )
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    cycleCount = 2,
                    programCounter = 0x02
            )
        }
    }

    @Nested
    inner class BranchOnCarrySet {
        @Test
        fun `Should branch if carryFlag is true`() {
            val memory = Memory(setupMemory(InstructionSet.bcs.u, 0x02u))
            val state = CpuState(
                    isCarryFlag = true
            )
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    cycleCount = 3,
                    programCounter = 0x04
            )
        }

        @Test
        fun `Should not branch if carryFlag is false`() {
            val memory = Memory(setupMemory(InstructionSet.bcs.u, 0x02u))
            val state = CpuState(
                    isCarryFlag = false
            )
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    cycleCount = 2,
                    programCounter = 0x02
            )
        }
    }

    @Nested
    inner class BranchOnMinus {
        @Test
        fun `Should branch if negativeFlag is true`() {
            val memory = Memory(setupMemory(InstructionSet.bmi.u, 0x02u))
            val state = CpuState(
                    isNegativeFlag = true
            )
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    cycleCount = 3,
                    programCounter = 0x04
            )
        }

        @Test
        fun `Should not branch if negativeFlag is false`() {
            val memory = Memory(setupMemory(InstructionSet.bmi.u, 0x02u))
            val state = CpuState(
                    isNegativeFlag = false
            )
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    cycleCount = 2,
                    programCounter = 0x02
            )
        }
    }

    @Nested
    inner class BranchOnOverflowClear {
        @Test
        fun `Should branch if overflowFlag is false`() {
            val memory = Memory(setupMemory(InstructionSet.bvc.u, 0x02u))
            val state = CpuState(
                    isOverflowFlag = false
            )
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    cycleCount = 3,
                    programCounter = 0x04
            )
        }

        @Test
        fun `Should not branch if overflowFlag is true`() {
            val memory = Memory(setupMemory(InstructionSet.bvc.u, 0x02u))
            val state = CpuState(
                    isOverflowFlag = true
            )
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    cycleCount = 2,
                    programCounter = 0x02
            )
        }
    }

    @Nested
    inner class BranchOnOverflowSet {
        @Test
        fun `Should branch if overflowFlag is true`() {
            val memory = Memory(setupMemory(InstructionSet.bvs.u, 0x02u))
            val state = CpuState(
                    isOverflowFlag = true
            )
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    cycleCount = 3,
                    programCounter = 0x04
            )
        }

        @Test
        fun `Should not branch if overflowFlag is false`() {
            val memory = Memory(setupMemory(InstructionSet.bvs.u, 0x02u))
            val state = CpuState(
                    isOverflowFlag = false
            )
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    cycleCount = 2,
                    programCounter = 0x02
            )
        }
    }
}