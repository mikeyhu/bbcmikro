package net.chompsoftware.bbcmikro.hardware.operations

import io.kotest.matchers.shouldBe
import net.chompsoftware.bbcmikro.hardware.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@ExperimentalUnsignedTypes
class ComparisonOperationsTest {

    @Nested
    inner class CompareAccumulator {
        @Test
        fun `Should compare Accumulator with immediate when equals`() {
            val memory = Memory(setupMemory(InstructionSet.cmp_i.u, 0x01u))
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
            val memory = Memory(setupMemory(InstructionSet.cpx_i.u, 0x01u))
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
            val memory = Memory(setupMemory(InstructionSet.cpy_i.u, 0x01u))
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