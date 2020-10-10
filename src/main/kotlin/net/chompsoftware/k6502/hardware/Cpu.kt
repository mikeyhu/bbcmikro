package net.chompsoftware.k6502.hardware

@ExperimentalUnsignedTypes
data class CpuState(
        val programCounter: Int = 0x00,
        val breakLocation: Int = 0x00,
        val aRegister: UInt = 0x0u,
        val xRegister: UInt = 0x0u,
        val yRegister: UInt = 0x0u,
        val stackPointer: Int = 0xff,
        val isBreakCommandFlag: Boolean = false,
        val isNegativeFlag: Boolean = false,
        val isZeroFlag: Boolean = false,
        val isDecimalFlag: Boolean = false
) {
    fun copyWithA(value: UInt, programCounter: Int) = this.copy(
            programCounter = programCounter,
            aRegister = value,
            isNegativeFlag = tweakNegative(value),
            isZeroFlag = tweakZero(value)
    )

    fun copyWithX(value: UInt, programCounter: Int) = this.copy(
            programCounter = programCounter,
            xRegister = value,
            isNegativeFlag = tweakNegative(value),
            isZeroFlag = tweakZero(value)

    )

    fun copyWithY(value: UInt, programCounter: Int) = this.copy(
            programCounter = programCounter,
            yRegister = value,
            isNegativeFlag = tweakNegative(value),
            isZeroFlag = tweakZero(value)
    )

    fun incrementCounterBy(value: Int) = this.copy(programCounter = programCounter + value)

    private fun tweakNegative(value: UInt) = value.shr(7) != 0u
    private fun tweakZero(value: UInt) = value == 0u
}

@ExperimentalUnsignedTypes
class Cpu {
    fun run(state: CpuState, memory: Memory): CpuState {

        val instructionByte = memory[state.programCounter]
        val instruction = InstructionSet.from(instructionByte)

        return when (instruction) {
            null -> throw Error("Undefined instruction ${instructionByte.toString(16)} at PC ${state.programCounter.toString(16)}")
            else -> instruction.run(state, memory)
        }
    }
}

enum class Address(val size: Int) {
    none(1),
    i(2),
    ab(3),
    aby(10000),
    z(2)
}

@ExperimentalUnsignedTypes
enum class InstructionSet(val u: UByte, val ad: Address, val op: Operation) {
    bne(0xd0u, Address.i, Operations.branchOnNotEqual),
    beq(0xf0u, Address.i, Operations.branchOnEqual),
    brk(0x00u, Address.none, Operations.brk),
    cld(0xd8u, Address.none, Operations.clearDecimal),
    dex(0xcau, Address.none, Operations.decrementx),
    txs(0x9au, Address.none, Operations.transferXToStack),
    jmp_ab(0x4cu, Address.ab, Operations.jump),
    lda_i(0xa9u, Address.i, Operations.loadAccumulator),
    ldx_i(0xa2u, Address.i, Operations.loadx),
    ldx_ab(0xaeu, Address.ab, Operations.loadx),
    ldx_aby(0xbeu, Address.aby, Operations.loadx),
    ldx_z(0xa6u, Address.z, Operations.loadx),
    ldy_i(0xa0u, Address.i, Operations.loady),
    sta_z(0x85u, Address.z, Operations.storeAccumulator),
    sta_ab(0x8du, Address.ab, Operations.storeAccumulator);

    fun run(state: CpuState, memory: Memory) = op(this, state, memory)

    companion object {
        private val instructions = values().associateBy { it.u }

        fun from(u: UByte) = instructions.get(u)
    }
}
