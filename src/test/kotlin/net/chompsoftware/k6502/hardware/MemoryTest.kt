package net.chompsoftware.k6502.hardware

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test


@ExperimentalUnsignedTypes
class MemoryTest {

    @Test
    fun `Should get position using zero page addressing`() {
        val memory = Memory(setupMemory(InstructionSet.nop.u, 0x05u))
        val state = CpuState()

        memory.positionUsing(Address.ab, state) shouldBe 0x5u
    }

    @Test
    fun `Should get position using zero page addressing with X offset`() {
        val memory = Memory(setupMemory(InstructionSet.nop.u, 0x05u))
        val state = CpuState(xRegister = 5u)

        memory.positionUsing(Address.zx, state) shouldBe 0xau
    }

    @Test
    fun `Should get position using zero page addressing with X offset with wrap around`() {
        val memory = Memory(setupMemory(InstructionSet.nop.u, 0xffu))
        val state = CpuState(xRegister = 5u)

        memory.positionUsing(Address.zx, state) shouldBe 0x04u
    }

    @Test
    fun `Should get position using zero page addressing with Y offset`() {
        val memory = Memory(setupMemory(InstructionSet.nop.u, 0x05u))
        val state = CpuState(yRegister = 5u)

        memory.positionUsing(Address.aby, state) shouldBe 0xau
    }

    @Test
    fun `Should get position using zero page addressing with Y offset with wrap around`() {
        val memory = Memory(setupMemory(InstructionSet.nop.u, 0xffu))
        val state = CpuState(yRegister = 5u)

        memory.positionUsing(Address.zy, state) shouldBe 0x04u
    }

    @Test
    fun `Should get position using absolute addressing`() {
        val memory = Memory(setupMemory(InstructionSet.nop.u, 0x05u, 0x01u))
        val state = CpuState()

        memory.positionUsing(Address.ab, state) shouldBe 0x105u
    }

    @Test
    fun `Should get position using indexed indirect addressing`() {
        val memory = Memory(setupMemory(InstructionSet.nop.u, 0xf0u))
        memory[0xf5u] = 0xffu
        memory[0xf6u] = 0xeeu
        val state = CpuState(xRegister = 0x5u)

        memory.positionUsing(Address.iix, state) shouldBe 0xeeffu
    }

    @Test
    fun `Should get position using indirect indexed addressing`() {
        val memory = Memory(setupMemory(InstructionSet.nop.u, 0xf0u))
        memory[0xf0u] = 0xf0u
        memory[0xf1u] = 0xeeu
        val state = CpuState(yRegister = 0x5u)

        memory.positionUsing(Address.iiy, state) shouldBe 0xeef5u
    }
}