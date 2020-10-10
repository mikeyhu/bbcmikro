package net.chompsoftware.k6502.hardware

import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import net.chompsoftware.k6502.hardware.InstructionSet.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@ExperimentalUnsignedTypes
class CpuTest {

    @Nested
    inner class Brk {
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
    inner class LoadAccumulator {
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

        @Test
        fun `Should handle LoaDAcc instruction and set accumulator using Absolute addressing`() {
            val memory = Memory(setupMemory(lda_ab.u, 0x04u, 0x00u, 0x00u, 0x11u))
            val state = CpuState()
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    programCounter = 0x03,
                    aRegister = 0x11u
            )
        }
    }

    @Nested
    inner class LoadX {
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
    inner class LoadY {
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
    inner class StoreAccumulator {
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
    inner class ClearDecimal {
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
    inner class Jump {
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

    @Nested
    inner class BranchOnNotEqual {
        @Test
        fun `Should branch if zeroFlag is false`() {
            val memory = Memory(setupMemory(bne.u, 0x02u))
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
            val memory = Memory(setupMemory(0x0u, bne.u, 0xfdu))
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
            val memory = Memory(setupMemory(bne.u, 0x02u))
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
            val memory = Memory(setupMemory(beq.u, 0x02u))
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
            val memory = Memory(setupMemory(beq.u, 0x02u))
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
    inner class DecrementX {
        @Test
        fun `Should decrement X`() {
            val memory = Memory(setupMemory(dex.u))
            val state = CpuState(
                    xRegister = 0x5u
            )
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    programCounter = 0x01,
                    xRegister = 0x4u
            )
        }
    }

    @Nested
    inner class DecrementY {
        @Test
        fun `Should decrement Y`() {
            val memory = Memory(setupMemory(dey.u))
            val state = CpuState(
                    yRegister = 0x5u
            )
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    programCounter = 0x01,
                    yRegister = 0x4u
            )
        }
    }

    @Nested
    inner class CompareAccummulator {
        @Test
        fun `Should compare Accumulator with immediate when equals`() {
            val memory = Memory(setupMemory(cmp_i.u, 0x01u))
            val state = CpuState(
                    aRegister = 0x01u,
                    isZeroFlag = false,
                    isCarryFlag = false,
                    isNegativeFlag = true
            )
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    programCounter = 0x02,
                    isZeroFlag = true,
                    isCarryFlag = true,
                    isNegativeFlag = false
            )
        }
    }

    fun setupMemory(vararg bytes: UByte): UByteArray {
        val array = UByteArray(0x8000)
        bytes.copyInto(array, 0, 0, bytes.size)
        return array
    }
}


