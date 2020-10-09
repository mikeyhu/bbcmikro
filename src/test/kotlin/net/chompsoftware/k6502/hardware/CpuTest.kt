package net.chompsoftware.k6502.hardware

import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import net.chompsoftware.k6502.hardware.Instruction.BReaK
import net.chompsoftware.k6502.hardware.Instruction.CLearDecimal
import net.chompsoftware.k6502.hardware.Instruction.JuMP_Ab
import net.chompsoftware.k6502.hardware.Instruction.LoaDAcc_I
import net.chompsoftware.k6502.hardware.Instruction.LoaDX_I
import net.chompsoftware.k6502.hardware.Instruction.LoaDY_I
import net.chompsoftware.k6502.hardware.Instruction.SToreAcc_Ab
import net.chompsoftware.k6502.hardware.Instruction.SToreAcc_Z
import net.chompsoftware.k6502.hardware.Instruction.TransferXtoStack
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@ExperimentalUnsignedTypes
class CpuTest {

    @Nested
    inner class BReaK {
        @Test
        fun `Should handle BReaK instruction and set program counter`() {
            val memory = Memory(setupMemory(BReaK, 0x01u, 0x02u))
            val state = CpuState(breakLocation = 0x01)
            val cpu = Cpu()
            cpu.run(state, memory) should {
                it.programCounter shouldBe 0x201
                it.isBreakCommandFlag shouldBe true
            }
        }
    }

    @Nested
    inner class LoaDAccumulator {
        @Test
        fun `Should handle LoaDAcc instruction and set accumulator`() {
            val memory = Memory(setupMemory(LoaDAcc_I, 0x08u))
            val state = CpuState()
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    programCounter = 0x02,
                    aRegister = 0x08u
            )
        }

        @Test
        fun `Should handle LoaDAcc instruction, set accumulator and set negative flag`() {
            val memory = Memory(setupMemory(LoaDAcc_I, 0x80u))
            val state = CpuState()
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    programCounter = 0x02,
                    aRegister = 0x80u,
                    isNegativeFlag = true
            )
        }

        @Test
        fun `Should handle LoaDAcc instruction, set accumulator and set zero flag`() {
            val memory = Memory(setupMemory(LoaDAcc_I, 0x0u))
            val state = CpuState()
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    programCounter = 0x02,
                    aRegister = 0x0u,
                    isZeroFlag = true
            )
        }
    }

    @Nested
    inner class LoaDX {
        @Test
        fun `Should handle LoaDX instruction and set accumulator`() {
            val memory = Memory(setupMemory(LoaDX_I, 0x08u))
            val state = CpuState()
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    programCounter = 0x02,
                    xRegister = 0x08u
            )
        }

        @Test
        fun `Should handle LoaDX instruction, set accumulator and set negative flag`() {
            val memory = Memory(setupMemory(LoaDX_I, 0x80u))
            val state = CpuState()
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    programCounter = 0x02,
                    xRegister = 0x80u,
                    isNegativeFlag = true
            )
        }

        @Test
        fun `Should handle LoaDX instruction, set accumulator and set zero flag`() {
            val memory = Memory(setupMemory(LoaDX_I, 0x0u))
            val state = CpuState()
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    programCounter = 0x02,
                    xRegister = 0x0u,
                    isZeroFlag = true
            )
        }
    }

    @Nested
    inner class LoaDY {
        @Test
        fun `Should handle LoaDY instruction and set accumulator`() {
            val memory = Memory(setupMemory(LoaDY_I, 0x08u))
            val state = CpuState()
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    programCounter = 0x02,
                    yRegister = 0x08u
            )
        }

        @Test
        fun `Should handle LoaDY instruction, set accumulator and set negative flag`() {
            val memory = Memory(setupMemory(LoaDY_I, 0x80u))
            val state = CpuState()
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    programCounter = 0x02,
                    yRegister = 0x80u,
                    isNegativeFlag = true
            )
        }

        @Test
        fun `Should handle LoaDY instruction, set accumulator and set zero flag`() {
            val memory = Memory(setupMemory(LoaDY_I, 0x0u))
            val state = CpuState()
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    programCounter = 0x02,
                    yRegister = 0x0u,
                    isZeroFlag = true
            )
        }
    }

    @Nested
    inner class SToreAcc {
        @Test
        fun `Should store accumulator in memory using Zero Page addressing`() {
            val memory = Memory(setupMemory(SToreAcc_Z, 0x05u))
            val state = CpuState(aRegister = 0x11u)
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    programCounter = 0x02
            )
            memory.readUInt(0x05) shouldBe 0x11u
        }

        @Test
        fun `Should store accumulator in memory using Absolute addressing`() {
            val memory = Memory(setupMemory(SToreAcc_Ab, 0x05u, 0x01u))
            val state = CpuState(aRegister = 0x11u)
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    programCounter = 0x03
            )
            memory.readUInt(0x105) shouldBe 0x11u
        }
    }

    @Nested
    inner class CLearDecimal {
        @Test
        fun `Should reset decimal flag`() {
            val memory = Memory(setupMemory(CLearDecimal))
            val state = CpuState(isDecimalFlag = true)
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    programCounter = 0x01,
                    isDecimalFlag = false
            )
        }
    }

    @Nested
    inner class TransferXtoStack {
        @Test
        fun `Should set stackPointer from xRegister`() {
            val memory = Memory(setupMemory(TransferXtoStack))
            val state = CpuState(
                    xRegister = 0x11u,
                    stackPointer = 0x00
            )
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    programCounter = 0x01,
                    stackPointer = 0x11
            )
        }
    }

    @Nested
    inner class JuMP {
        @Test
        fun `Should set programCounter using Absolute addressing`() {
            val memory = Memory(setupMemory(JuMP_Ab, 0x34u, 0x12u))
            val state = CpuState(
                    xRegister = 0x11u,
                    stackPointer = 0x00
            )
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    programCounter = 0x1234
            )
        }
    }

    fun setupMemory(vararg bytes: UByte): UByteArray {
        val array = UByteArray(0x8000)
        bytes.copyInto(array, 0, 0, bytes.size)
        return array
    }
}


