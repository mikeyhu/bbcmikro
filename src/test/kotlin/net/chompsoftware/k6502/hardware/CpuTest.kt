package net.chompsoftware.k6502.hardware

import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import net.chompsoftware.k6502.hardware.InstructionSet.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

@ExperimentalUnsignedTypes
class CpuTest {

    @Test
    fun `All operations should take at least 1 cycle`() {
        values().forEach {instruction ->
            val memory = Memory(setupMemory(instruction.u, 0x01u, 0x02u))
            val state = CpuState(cycleCount = 100)
            try {
                val result = Cpu().run(state, memory)
                if(result.cycleCount == 100L) {
                    fail("${instruction.name} took 0 cycles")
                }
                if(result.cycleCount < 100L) {
                    fail("${instruction.name} reset cycles to ${result.cycleCount}")
                }
            } catch (error: NotImplementedError) {
                println("$instruction failed due to $error")
            }
        }
    }

    @Test
    fun `All operations should set the program Counter to something higher than it was before`() {
        values().forEach {instruction ->
            val memory = Memory(setupMemory(brk.u, instruction.u, 0x04u, 0x00u, 0xffu, 0x00u))
            memory.writeUInt16ToStack(0xfe, 0x9999u)
            memory.writeUByteToStack(0xfd, 0x99u)
            val state = CpuState(
                    programCounter = 1,
                    breakLocation = 0x04,
                    stackPointer = 0xfc
            )
            try {
                val result = Cpu().run(state, memory)
                if(result.programCounter <= 1) {
                    fail("${instruction.name} did not set program counter to something reasonable ${result.programCounter}")
                }
            } catch (error: NotImplementedError) {
                println("$instruction failed due to $error")
            }
        }
    }

    @Test
    fun `copyRelativeWithFlags should not reset unsent properties`() {
        val cpu = CpuState(cycleCount = 0x100,
                programCounter = 0x200,
                breakLocation = 0x300,
                stackPointer = 0xfe,
                aRegister = 0x1u,
                xRegister = 0x2u,
                yRegister = 0x3u)

        cpu.copyRelativeWithFlags(instruction = clc, interruptDisabledFlag = true) shouldBe cpu.copy(
                cycleCount = 0x102,
                programCounter = 0x201,
                isInterruptDisabledFlag = true
        )
    }

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
                it.isInterruptDisabledFlag shouldBe true
            }
        }
    }

    @Nested
    inner class ReturnFromInterrupt {
        @Test
        fun `Should return back from a break`() {
            val memory = Memory(setupMemory(brk.u, 0x01u, 0x02u))
            memory[0x201] = rti.u
            val state = CpuState(breakLocation = 0x01)
            val cpu = Cpu()
            val stateAfterBreak = cpu.run(state, memory)
            cpu.run(stateAfterBreak, memory) should {
                it.programCounter shouldBe 2
                it.isBreakCommandFlag shouldBe true
            }
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
                    cycleCount = 2,
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
                    cycleCount = 3L,
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
                    cycleCount = 5L,
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
                    cycleCount = 6,
                    programCounter = 0x1234,
                    stackPointer = 0xfd
            )
            memory.readUInt16(0x1fe) shouldBe 0x03u
        }
    }

    @Nested
    inner class ReturnFromSubroutine {
        @Test
        fun `Should set programCounter using the value on the stack`() {
            val memory = Memory(setupMemory(rts.u))
            memory.writeUInt16ToStack(0xff, 0x1234u)
            val state = CpuState(
                    stackPointer = 0xfd
            )
            val cpu = Cpu()
            cpu.run(state, memory) shouldBe state.copy(
                    cycleCount = 6,
                    programCounter = 0x1235,
                    stackPointer = 0xff
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
                    cycleCount = 2,
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
                    cycleCount = 2,
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
                    cycleCount = 2,
                    programCounter = 0x02,
                    isZeroFlag = true,
                    isCarryFlag = true,
                    isNegativeFlag = false
            )
        }
    }
}

@ExperimentalUnsignedTypes
fun setupMemory(vararg bytes: UByte, size: Int = 0x8000): UByteArray {
    val array = UByteArray(size)
    bytes.copyInto(array, 0, 0, bytes.size)
    return array
}
