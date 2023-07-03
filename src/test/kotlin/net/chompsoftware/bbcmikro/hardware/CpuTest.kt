package net.chompsoftware.bbcmikro.hardware

import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import net.chompsoftware.bbcmikro.hardware.InstructionSet.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

@ExperimentalUnsignedTypes
class CpuTest {

    @Test
    fun `All operations should take at least 1 cycle`() {
        values().forEach { instruction ->
            val memory = Memory(setupMemory(instruction.u, 0x01u, 0x02u))
            val state = CpuState(cycleCount = 100)
            try {
                val result = Cpu().run(state, memory)
                if (result.cycleCount == 100L) {
                    fail("${instruction.name} took 0 cycles")
                }
                if (result.cycleCount < 100L) {
                    fail("${instruction.name} reset cycles to ${result.cycleCount}")
                }
            } catch (error: NotImplementedError) {
                println("$instruction failed due to $error")
            }
        }
    }

    @Test
    fun `All operations should set the program Counter to something higher than it was before`() {
        values().forEach { instruction ->
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
                if (result.programCounter <= 1) {
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

    @Test
    fun `interrupt should do the reverse of return from interrupt`() {
        val state = CpuState(cycleCount = 0x100,
                programCounter = 0x200,
                breakLocation = 0xfffe,
                stackPointer = 0xfe,
                isOverflowFlag = true)

        val memory = Memory(UByteArray(size = 0x10000))

        memory[0xfffe] = 0x34u
        memory[0xffff] = 0x12u

        val interrupted = Cpu().interrupt(state, memory)
        interrupted should {
            it.programCounter shouldBe 0x1234
            it.stackPointer shouldBe 0xfb
        }
    }
}

@ExperimentalUnsignedTypes
fun setupMemory(vararg bytes: UByte, size: Int = 0x8000): UByteArray {
    val array = UByteArray(size)
    bytes.copyInto(array, 0, 0, bytes.size)
    return array
}
