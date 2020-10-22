package net.chompsoftware.k6502.hardware

@ExperimentalUnsignedTypes
object CpuSettings {
    const val CARRY_BYTE_POSITION = 0x1u
    const val ZERO_BYTE_POSITION = 0x2u
    const val INTERRUPT_BYTE_POSITION = 0x4u
    const val DECIMAL_BYTE_POSITION = 0x8u
    const val BREAK_BYTE_POSITION = 0x10u
    const val OVERFLOW_BYTE_POSITION = 0x40u
    const val NEGATIVE_BYTE_POSITION = 0x80u
}

@ExperimentalUnsignedTypes
data class CpuState(
        val cycleCount: Long = 0,
        val programCounter: Int = 0x00,
        val breakLocation: Int = 0x00,
        val aRegister: UInt = 0x0u,
        val xRegister: UInt = 0x0u,
        val yRegister: UInt = 0x0u,
        val stackPointer: Int = 0xff,

        val isBreakCommandFlag: Boolean = false,
        val isNegativeFlag: Boolean = false,
        val isZeroFlag: Boolean = false,
        val isDecimalFlag: Boolean = false,
        val isCarryFlag: Boolean = false,
        val isOverflowFlag: Boolean = false,
        val isInterruptDisabledFlag: Boolean = false
) {
    fun copyRelativeWithA(instruction: InstructionSet,
                          value: UInt,
                          stackPoint: Int? = null,
                          carryFlag: Boolean? = null): CpuState {
        val ubyteValue = removeOverflow(value)
        return this.copy(
                cycleCount = cycleCount + instruction.cy,
                programCounter = programCounter + instruction.ad.size,
                aRegister = ubyteValue,
                isNegativeFlag = tweakNegative(ubyteValue),
                isZeroFlag = tweakZero(ubyteValue),
                stackPointer = stackPoint ?: stackPointer,
                isCarryFlag = carryFlag ?: isCarryFlag
        )
    }

    fun copyRelativeWithX(instruction: InstructionSet, value: UInt): CpuState {
        val ubyteValue = removeOverflow(value)
        return this.copy(
                cycleCount = cycleCount + instruction.cy,
                programCounter = programCounter + instruction.ad.size,
                xRegister = ubyteValue,
                isNegativeFlag = tweakNegative(ubyteValue),
                isZeroFlag = tweakZero(ubyteValue)
        )
    }

    fun copyRelativeWithY(instruction: InstructionSet, value: UInt): CpuState {
        val ubyteValue = removeOverflow(value)
        return this.copy(
                cycleCount = cycleCount + instruction.cy,
                programCounter = programCounter + instruction.ad.size,
                yRegister = ubyteValue,
                isNegativeFlag = tweakNegative(ubyteValue),
                isZeroFlag = tweakZero(ubyteValue)
        )
    }

    fun removeOverflow(value: UInt) = value.and(0xffu)
    fun removeOverflow(value: Int) = value.and(0xff)
    fun removeUnderflow(value: Int) = if (value < 0) 0x100 + value else value

    fun copyRelativeWithFlags(
            instruction: InstructionSet,
            carryFlag: Boolean? = null,
            overflowFlag: Boolean? = null,
            interruptDisabledFlag: Boolean? = null,
            decimalFlag: Boolean? = null,
            negativeFlag: Boolean? = null,
            breakCommandFlag: Boolean? = null,
            zeroFlag: Boolean? = null
    ) = CpuState(
            cycleCount = cycleCount + instruction.cy,
            programCounter = programCounter + instruction.ad.size,
            breakLocation = breakLocation,
            aRegister = aRegister,
            xRegister = xRegister,
            yRegister = yRegister,
            stackPointer = stackPointer,
            isCarryFlag = carryFlag ?: isCarryFlag,
            isZeroFlag = zeroFlag ?: isZeroFlag,
            isInterruptDisabledFlag = interruptDisabledFlag ?: isInterruptDisabledFlag,
            isDecimalFlag = decimalFlag ?: isDecimalFlag,
            isOverflowFlag = overflowFlag ?: isOverflowFlag,
            isNegativeFlag = negativeFlag ?: isNegativeFlag,
            isBreakCommandFlag = breakCommandFlag ?: isBreakCommandFlag
    )

    fun setFlagsUsingUByte(byte: UInt, programCounter: Int, stackPointer: Int, cycles: Long): CpuState {
        val withFlags = this.copy(
                cycleCount = cycleCount + cycles,
                programCounter = programCounter,
                stackPointer = stackPointer,
                isCarryFlag = byte.and(CpuSettings.CARRY_BYTE_POSITION) == CpuSettings.CARRY_BYTE_POSITION,
                isZeroFlag = byte.and(CpuSettings.ZERO_BYTE_POSITION) == CpuSettings.ZERO_BYTE_POSITION,
                isInterruptDisabledFlag = byte.and(CpuSettings.INTERRUPT_BYTE_POSITION) == CpuSettings.INTERRUPT_BYTE_POSITION,
                isDecimalFlag = byte.and(CpuSettings.DECIMAL_BYTE_POSITION) == CpuSettings.DECIMAL_BYTE_POSITION,
                isBreakCommandFlag = byte.and(CpuSettings.BREAK_BYTE_POSITION) == CpuSettings.BREAK_BYTE_POSITION,
                isOverflowFlag = byte.and(CpuSettings.OVERFLOW_BYTE_POSITION) == CpuSettings.OVERFLOW_BYTE_POSITION,
                isNegativeFlag = byte.and(CpuSettings.NEGATIVE_BYTE_POSITION) == CpuSettings.NEGATIVE_BYTE_POSITION
        )
        if (VERBOSE) println("loaded flags $withFlags from ${byte.toHex()}")
        return withFlags
    }

    fun readFlagsAsUbyte(): UByte {
        val ub = (0x20u +
                (if (isCarryFlag) CpuSettings.CARRY_BYTE_POSITION else 0u) +
                (if (isZeroFlag) CpuSettings.ZERO_BYTE_POSITION else 0u) +
                (if (isInterruptDisabledFlag) CpuSettings.INTERRUPT_BYTE_POSITION else 0u) +
                (if (isDecimalFlag) CpuSettings.DECIMAL_BYTE_POSITION else 0u) +
                CpuSettings.BREAK_BYTE_POSITION +
                (if (isOverflowFlag) CpuSettings.OVERFLOW_BYTE_POSITION else 0u) +
                (if (isNegativeFlag) CpuSettings.NEGATIVE_BYTE_POSITION else 0u)).toUByte()
        if (VERBOSE) println("saving flags ${ub.toHex()} from $this")
        return ub
    }

    fun incrementByInstruction(instruction: InstructionSet) = this.copy(
            cycleCount = cycleCount + instruction.cy,
            programCounter = programCounter + instruction.ad.size
    )

    private fun tweakNegative(value: UInt) = value.shr(7) != 0u
    private fun tweakZero(value: UInt) = value == 0u

    override fun toString(): String {
        return "CpuState(pc=${programCounter.toHex()}, cc=${cycleCount} bl=${breakLocation.toHex()}, a=${aRegister.toHex()}, x=${xRegister.toHex()}, y=${yRegister.toHex()}" +
                ", sp=${stackPointer.toHex()}, brk=$isBreakCommandFlag, neg=$isNegativeFlag, zro=$isZeroFlag, dec=$isDecimalFlag, car=$isCarryFlag, ovr=$isOverflowFlag, int=$isInterruptDisabledFlag)"
    }

    fun addressParameters() = programCounter + 1
}

@ExperimentalUnsignedTypes
class Cpu {
    fun run(state: CpuState, memory: Memory): CpuState {

        val instructionByte = memory[state.programCounter]
        val instruction = InstructionSet.from(instructionByte)

        return when (instruction) {
            null -> throw Error("Undefined instruction ${instructionByte.toHex()} at PC ${state.programCounter.toHex()}")
            else -> instruction.run(state, memory)
        }
    }
}

