package net.chompsoftware.k6502.hardware

@ExperimentalUnsignedTypes
data class CpuState(
        val programCounter: Int = 0x00,
        val breakLocation: Int = 0x00,
        val aRegister: UInt = 0x0u,
        val xRegister: UInt = 0x0u,
        val yRegister: UInt = 0x0u,
        val isBreakCommandFlag: Boolean = false,
        val isNegativeFlag: Boolean = false,
        val isZeroFlag: Boolean = false
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
class Cpu() {
    fun run(state: CpuState, memory: Memory): CpuState {

        val instruction = memory[state.programCounter]

        return when (instruction) {
            Instruction.BReaK -> {
                state.copy(isBreakCommandFlag = true, programCounter = memory.readInt16(state.breakLocation))
            }
            Instruction.LoaDAcc_I -> {
                val register = memory.readUInt(state.programCounter + 1)
                state.copyWithA(register, state.programCounter + 2)
            }
            Instruction.LoaDX_I -> {
                val register = memory.readUInt(state.programCounter + 1)
                state.copyWithX(register, state.programCounter + 2)

            }
            Instruction.LoaDY_I -> {
                val register = memory.readUInt(state.programCounter + 1)
                state.copyWithY(register, state.programCounter + 2)
            }
            Instruction.SToreAcc_Z -> {
                val location = memory.readUInt(state.programCounter + 1)
                memory[location] = state.aRegister.toUByte()
                state.incrementCounterBy(2)
            }
            else -> throw Error("Undefined instruction $instruction")
        }
    }
}

@ExperimentalUnsignedTypes
object Instruction {
    const val BReaK: UByte = 0x00u
    const val LoaDAcc_I: UByte = 0xa9u

    const val LoaDX_I: UByte = 0xa2u
    const val LoaDX_Ab: UByte = 0xaeu
    const val LoaDX_AbY: UByte = 0xbeu
    const val LoaDX_Z: UByte = 0xa6u
    const val LoaDX_ZY: UByte = 0xb6u

    const val LoaDY_I: UByte = 0xa0u

    const val SToreAcc_Z: UByte = 0x85u


}
