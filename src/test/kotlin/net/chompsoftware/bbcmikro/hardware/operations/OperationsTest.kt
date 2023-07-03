package net.chompsoftware.bbcmikro.hardware.operations

import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import net.chompsoftware.bbcmikro.hardware.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@ExperimentalUnsignedTypes
class OperationsTest {

    @Nested
    inner class Brk {
        @Test
        fun `Should handle BReaK instruction and set program counter`() {
            val memory = Memory(setupMemory(InstructionSet.brk.u, 0x01u, 0x02u))
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
            val memory = Memory(setupMemory(InstructionSet.brk.u, 0x01u, 0x02u))
            memory[0x201] = InstructionSet.rti.u
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
            val memory = Memory(setupMemory(InstructionSet.nop.u))
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
            val memory = Memory(setupMemory(InstructionSet.jmp_ab.u, 0x34u, 0x12u))
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
            val memory = Memory(setupMemory(InstructionSet.jmp_ir.u, 0x04u, 0x00u, InstructionSet.brk.u, 0x34u, 0x12u))
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
            val memory = Memory(setupMemory(InstructionSet.brk.u, InstructionSet.jsr_ab.u, 0x34u, 0x12u))
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
            val memory = Memory(setupMemory(InstructionSet.rts.u))
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
}