package net.chompsoftware.k6502.hardware

const val LOG_OPERATIONS: Boolean = true

@ExperimentalUnsignedTypes
internal typealias Operation = (instruction: InstructionSet, state: CpuState, memory: Memory) -> CpuState

@ExperimentalUnsignedTypes
internal object Operations {
    fun log(s: String) {
        if (LOG_OPERATIONS) println(s)
    }

    val notImplementedOperation = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        throw NotImplementedError("Not Implemented Operation ${instruction.name}:${instruction.u.toString(16)} at ${state.programCounter.toString(16)}")
    }


    val addWithCarry = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        val amount = memory.readUsing(instruction.ad, state)
        val sum = state.aRegister + amount

        state.copy(
                cycleCount = state.cycleCount + instruction.cy,
                programCounter = state.programCounter + instruction.ad.size,
                aRegister = if (sum > 0xffu) sum - 0x100u else sum,
                isCarryFlag = sum > 0xffu
        )
    }

    private val branchIfTrue = { check: Boolean, instruction: InstructionSet, state: CpuState, memory: Memory ->
        val newState = if (check) {
            val location = memory.readUsing(instruction.ad, state)
            val newLocation = if (location >= 0x80u) -0x100 + location.toInt() else location.toInt()
            state.copy(
                    cycleCount = state.cycleCount + instruction.cy + 1,
                    programCounter = state.programCounter + instruction.ad.size + newLocation
            )

        } else state.incrementCountersBy(instruction.ad.size, instruction.cy)
        log("Branch ${check} from ${state.programCounter.toString(16)} to ${newState.programCounter.toString(16)}")
        newState
    }

    val branchOnCarryClear = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        branchIfTrue(!state.isCarryFlag, instruction, state, memory)
    }

    val branchOnCarrySet = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        branchIfTrue(state.isCarryFlag, instruction, state, memory)
    }

    val branchOnMinus = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        branchIfTrue(state.isNegativeFlag, instruction, state, memory)
    }

    val branchOnNotEqual = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        branchIfTrue(!state.isZeroFlag, instruction, state, memory)
    }

    val branchOnEqual = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        branchIfTrue(state.isZeroFlag, instruction, state, memory)
    }

    val branchOnPlus = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        branchIfTrue(!state.isNegativeFlag, instruction, state, memory)
    }

    val branchOnOverflowClear = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        branchIfTrue(!state.isOverflowFlag, instruction, state, memory)
    }

    val branchOnOverflowSet = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        branchIfTrue(state.isOverflowFlag, instruction, state, memory)
    }

    val brk = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        state.copy(
                cycleCount = state.cycleCount + instruction.cy,
                isBreakCommandFlag = true,
                programCounter = memory.readInt16(state.breakLocation))
    }

    val compareAccumulator = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        val compareTo = memory.readUsing(instruction.ad, state)
        state.copy(
                cycleCount = state.cycleCount + instruction.cy,
                programCounter = state.programCounter + instruction.ad.size,
                isZeroFlag = state.aRegister == compareTo,
                isCarryFlag = state.aRegister >= compareTo,
                isNegativeFlag = state.aRegister < compareTo
        )
    }

    val compareX = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        val compareTo = memory.readUsing(instruction.ad, state)
        state.copy(
                cycleCount = state.cycleCount + instruction.cy,
                programCounter = state.programCounter + instruction.ad.size,
                isZeroFlag = state.xRegister == compareTo,
                isCarryFlag = state.xRegister >= compareTo,
                isNegativeFlag = state.xRegister < compareTo
        )
    }

    val compareY = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        val compareTo = memory.readUsing(instruction.ad, state)
        state.copy(
                cycleCount = state.cycleCount + instruction.cy,
                programCounter = state.programCounter + instruction.ad.size,
                isZeroFlag = state.yRegister == compareTo,
                isCarryFlag = state.yRegister >= compareTo,
                isNegativeFlag = state.yRegister < compareTo
        )
    }

    val decrementx = { instruction: InstructionSet, state: CpuState, _: Memory ->
        state.copyWithX(state.xRegister - 1u, programCounter = state.programCounter + 1, cycles = instruction.cy)
    }

    val decrementy = { instruction: InstructionSet, state: CpuState, _: Memory ->
        state.copyWithY(state.yRegister - 1u, programCounter = state.programCounter + 1, cycles = instruction.cy)
    }

    val incrementx = { instruction: InstructionSet, state: CpuState, _: Memory ->
        state.copyWithX(state.xRegister + 1u, programCounter = state.programCounter + 1, cycles = instruction.cy)
    }

    val incrementy = { instruction: InstructionSet, state: CpuState, _: Memory ->
        state.copyWithY(state.yRegister + 1u, programCounter = state.programCounter + 1, cycles = instruction.cy)
    }

    val exclusiveOr = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        state.copyWithA(
                state.aRegister.xor(memory.readUsing(instruction.ad, state)),
                state.programCounter + instruction.ad.size,
                instruction.cy)
    }

    val storeAccumulator = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        val location = memory.positionUsing(instruction.ad, state)
        memory[location] = state.aRegister.toUByte()
        state.incrementCountersBy(instruction.ad.size, instruction.cy)
    }

    val loadAccumulator = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        val register = memory.readUsing(instruction.ad, state)
        state.copyWithA(register, state.programCounter + instruction.ad.size, instruction.cy)
    }

    val loadx = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        val register = memory.readUsing(instruction.ad, state)
        state.copyWithX(register, state.programCounter + instruction.ad.size, instruction.cy)
    }

    val loady = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        val register = memory.readUsing(instruction.ad, state)
        state.copyWithY(register, state.programCounter + instruction.ad.size, instruction.cy)
    }

    val jump = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        state.copy(
                cycleCount = state.cycleCount + instruction.cy,
                programCounter = memory.positionUsing(instruction.ad, state).toInt()
        )
    }

    val jumpToSubroutine = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        memory.writeUInt16ToStack(state.stackPointer, state.programCounter.toUInt() + 2u)
        state.copy(
                cycleCount = state.cycleCount + instruction.cy,
                programCounter = memory.positionUsing(instruction.ad, state).toInt(),
                stackPointer = state.stackPointer - 2
        )

    }

    val returnFromSubroutine = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        state.copy(
                cycleCount = state.cycleCount + instruction.cy,
                programCounter = memory.readUInt16FromStack(state.stackPointer).toInt() + 1,
                stackPointer = state.stackPointer + 2
        )

    }

    val clearCarry = { instruction: InstructionSet, state: CpuState, _: Memory ->
        state.copy(
                cycleCount = state.cycleCount + instruction.cy,
                programCounter = state.programCounter + instruction.ad.size,
                isCarryFlag = false
        )
    }

    val clearDecimal = { instruction: InstructionSet, state: CpuState, _: Memory ->
        state.copy(
                cycleCount = state.cycleCount + instruction.cy,
                programCounter = state.programCounter + instruction.ad.size,
                isDecimalFlag = false
        )
    }

    val pushAccumulator = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        memory.writeUByteToStack(state.stackPointer, state.aRegister.toUByte())
        state.copy(
                cycleCount = state.cycleCount + instruction.cy,
                programCounter = state.programCounter + instruction.ad.size,
                stackPointer = state.stackPointer-1
        )
    }

    val pullAccumulator = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        state.copyWithA(
                memory.readUIntFromStack(state.stackPointer + 1),
                state.programCounter + instruction.ad.size,
                instruction.cy,
                state.stackPointer + 1
        )
    }

    val pushProcessorStatus = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        memory.writeUByteToStack(state.stackPointer, state.readFlagsAsUbyte())
        state.copy(
                cycleCount = state.cycleCount + instruction.cy,
                programCounter = state.programCounter + instruction.ad.size,
                stackPointer = state.stackPointer-1
        )
    }

    val pullProcessorStatus = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        state.setFlagsUsingUByte(
                memory.readUIntFromStack(state.stackPointer + 1),
                state.programCounter + instruction.ad.size,
                state.stackPointer + 1,
                instruction.cy
        )
    }

    val transferXToStack = { instruction: InstructionSet, state: CpuState, _: Memory ->
        state.copy(
                cycleCount = state.cycleCount + instruction.cy,
                programCounter = state.programCounter + instruction.ad.size,
                stackPointer = state.xRegister.toInt()
        )
    }

    val transferStackToX = { instruction: InstructionSet, state: CpuState, _: Memory ->
        state.copyWithX(state.stackPointer.toUInt(), state.programCounter + instruction.ad.size, instruction.cy)
    }

    val transferAccumulatorToX = { instruction: InstructionSet, state: CpuState, _: Memory ->
        state.copyWithX(state.aRegister, programCounter = state.programCounter + instruction.ad.size, cycles = instruction.cy)
    }

    val transferAccumulatorToY = { instruction: InstructionSet, state: CpuState, _: Memory ->
        state.copyWithY(state.aRegister, programCounter = state.programCounter + instruction.ad.size, cycles = instruction.cy)
    }

    val transferYtoAccumulator = { instruction: InstructionSet, state: CpuState, _: Memory ->
        state.copyWithA(state.yRegister, programCounter = state.programCounter + instruction.ad.size, cycles = instruction.cy)
    }

    val transferXtoAccumulator = { instruction: InstructionSet, state: CpuState, _: Memory ->
        state.copyWithA(state.xRegister, programCounter = state.programCounter + instruction.ad.size, cycles = instruction.cy)
    }

    val noOperation = { instruction: InstructionSet, state: CpuState, _: Memory ->
        state.incrementCountersBy(instruction.ad.size, instruction.cy)
    }
}