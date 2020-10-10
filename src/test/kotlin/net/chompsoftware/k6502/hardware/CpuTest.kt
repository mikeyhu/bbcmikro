package net.chompsoftware.k6502.hardware

import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import net.chompsoftware.k6502.hardware.InstructionSet.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@ExperimentalUnsignedTypes
class CpuTest {

    @Nested
    inner class BReaK {
        @Test
        fun `Should handle BReaK instruction and set program counter`() {
            val memory = Memory(setupMemory(brk.u, 0x01u, 0x02u))
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
            val memory = Memory(setupMemory(lda_i.u, 0x08u))
            val state = CpuState()
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    programCounter = 0x02,
                    aRegister = 0x08u
            )
        }

        @Test
        fun `Should handle LoaDAcc instruction, set accumulator and set negative flag`() {
            val memory = Memory(setupMemory(lda_i.u, 0x80u))
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
            val memory = Memory(setupMemory(lda_i.u, 0x0u))
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
            val memory = Memory(setupMemory(ldx_i.u, 0x08u))
            val state = CpuState()
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    programCounter = 0x02,
                    xRegister = 0x08u
            )
        }

        @Test
        fun `Should handle LoaDX instruction, set accumulator and set negative flag`() {
            val memory = Memory(setupMemory(ldx_i.u, 0x80u))
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
            val memory = Memory(setupMemory(ldx_i.u, 0x0u))
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
            val memory = Memory(setupMemory(ldy_i.u, 0x08u))
            val state = CpuState()
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    programCounter = 0x02,
                    yRegister = 0x08u
            )
        }

        @Test
        fun `Should handle LoaDY instruction, set accumulator and set negative flag`() {
            val memory = Memory(setupMemory(ldy_i.u, 0x80u))
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
            val memory = Memory(setupMemory(ldy_i.u, 0x0u))
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
            val memory = Memory(setupMemory(sta_z.u, 0x02u, 0x05u))
            val state = CpuState(aRegister = 0x11u)
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    programCounter = 0x02
            )
            memory.readUInt(0x05) shouldBe 0x11u
        }

        @Test
        fun `Should store accumulator in memory using Absolute addressing`() {
            val memory = Memory(setupMemory(sta_ab.u, 0x05u, 0x01u))
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
            val memory = Memory(setupMemory(cld.u))
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
            val memory = Memory(setupMemory(txs.u))
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
            val memory = Memory(setupMemory(jmp_ab.u, 0x34u, 0x12u))
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


