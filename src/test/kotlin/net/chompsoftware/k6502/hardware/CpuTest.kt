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
    inner class ExclusiveOr {
        @Test
        fun `Should OR the accumulator with the immediate value`() {
            val memory = Memory(setupMemory(eor_i.u, 0xffu))
            val state = CpuState(aRegister = 0x0fu)
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    programCounter = 0x02,
                    aRegister = 0xf0u,
                    isNegativeFlag = true
            )
        }
    }

    @Nested
    inner class AddWithCarry {
        @Test
        fun `Should add value to accumulator`() {
            val memory = Memory(setupMemory(adc_i.u, 0x1u))
            val state = CpuState(aRegister = 0x0fu)
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    programCounter = 0x02,
                    aRegister = 0x10u,
                    isCarryFlag = false
            )
        }

        @Test
        fun `Should add value to accumulator with carry`() {
            val memory = Memory(setupMemory(adc_i.u, 0xc4u))
            val state = CpuState(aRegister = 0xc0u)
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    programCounter = 0x02,
                    aRegister = 0x84u,
                    isCarryFlag = true
            )
        }
    }

    @Nested
    inner class ClearCarry {
        @Test
        fun `Should reset carry flag`() {
            val memory = Memory(setupMemory(clc.u))
            val state = CpuState(isCarryFlag = true)
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    programCounter = 0x01,
                    isCarryFlag = false
            )
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
    inner class NoOperation {
        @Test
        fun `Should just update programCounter`() {
            val memory = Memory(setupMemory(nop.u))
            val state = CpuState()
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    programCounter = 0x01
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

        @Test
        fun `Should set programCounter using indirect addressing`() {
            val memory = Memory(setupMemory(jmp_ir.u, 0x04u, 0x00u, brk.u, 0x34u, 0x12u))
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
    inner class JumpToSubroutine {
        @Test
        fun `Should set programCounter using Absolute addressing and save previous programCounter on stack`() {
            val memory = Memory(setupMemory(brk.u, jsr_ab.u, 0x34u, 0x12u))
            val state = CpuState(
                    programCounter = 0x01,
                    stackPointer = 0xff
            )
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    programCounter = 0x1234,
                    stackPointer = 0xfd
            )
            memory.readUInt16(0x1fe) shouldBe 0x03u
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
    inner class IncrementX {
        @Test
        fun `Should increment X`() {
            val memory = Memory(setupMemory(inx.u))
            val state = CpuState(
                    xRegister = 0x5u
            )
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    programCounter = 0x01,
                    xRegister = 0x6u
            )
        }
    }

    @Nested
    inner class IncrementY {
        @Test
        fun `Should increment Y`() {
            val memory = Memory(setupMemory(iny.u))
            val state = CpuState(
                    yRegister = 0x5u
            )
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    programCounter = 0x01,
                    yRegister = 0x6u
            )
        }
    }

    @Nested
    inner class CompareAccumulator {
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

    @Nested
    inner class CompareX {
        @Test
        fun `Should compare XRegister with immediate when equals`() {
            val memory = Memory(setupMemory(cpx_i.u, 0x01u))
            val state = CpuState(
                    xRegister = 0x01u,
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

    @Nested
    inner class CompareY {
        @Test
        fun `Should compare YRegister with immediate when equals`() {
            val memory = Memory(setupMemory(cpy_i.u, 0x01u))
            val state = CpuState(
                    yRegister = 0x01u,
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
}

@ExperimentalUnsignedTypes
fun setupMemory(vararg bytes: UByte): UByteArray {
    val array = UByteArray(0x8000)
    bytes.copyInto(array, 0, 0, bytes.size)
    return array
}
