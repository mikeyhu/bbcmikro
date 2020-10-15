package net.chompsoftware.k6502.hardware.operations

import io.kotest.matchers.shouldBe
import net.chompsoftware.k6502.hardware.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@ExperimentalUnsignedTypes
class TransferTest {
    @Nested
    inner class TransferXtoStack {
        @Test
        fun `Should set stackPointer from xRegister`() {
            val memory = Memory(setupMemory(InstructionSet.txs.u))
            val state = CpuState(
                    xRegister = 0x11u,
                    stackPointer = 0x00
            )
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    cycleCount = 2,
                    programCounter = 0x01,
                    stackPointer = 0x11
            )
        }
    }

    @Nested
    inner class TransferStackToX {
        @Test
        fun `Should set xRegister from stackPointer`() {
            val memory = Memory(setupMemory(InstructionSet.tsx.u))
            val state = CpuState(
                    xRegister = 0x00u,
                    stackPointer = 0x11
            )
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    cycleCount = 2L,
                    programCounter = 0x01,
                    xRegister = 0x11u
            )
        }
    }

    @Nested
    inner class TransferYtoAccumulator {
        @Test
        fun `Should set Accumulator from yRegister`() {
            val memory = Memory(setupMemory(InstructionSet.tya.u))
            val state = CpuState(
                    yRegister = 0x11u
            )
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    cycleCount = 2L,
                    programCounter = 0x01,
                    aRegister = 0x11u
            )
        }
    }

    @Nested
    inner class TransferXToAccumulator {
        @Test
        fun `Should set Accumulator from xRegister`() {
            val memory = Memory(setupMemory(InstructionSet.txa.u))
            val state = CpuState(
                    xRegister = 0x11u
            )
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    cycleCount = 2L,
                    programCounter = 0x01,
                    aRegister = 0x11u
            )
        }
    }

    @Nested
    inner class TransferAccumulatorToX {
        @Test
        fun `Should set X from accumulator`() {
            val memory = Memory(setupMemory(InstructionSet.tax.u))
            val state = CpuState(
                    aRegister = 0x11u
            )
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    cycleCount = 2L,
                    programCounter = 0x01,
                    xRegister = 0x11u
            )
        }
    }

    @Nested
    inner class TransferAccumulatorToY {
        @Test
        fun `Should set Y from accumulator`() {
            val memory = Memory(setupMemory(InstructionSet.tay.u))
            val state = CpuState(
                    aRegister = 0x11u
            )
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    cycleCount = 2L,
                    programCounter = 0x01,
                    yRegister = 0x11u
            )
        }
    }
}