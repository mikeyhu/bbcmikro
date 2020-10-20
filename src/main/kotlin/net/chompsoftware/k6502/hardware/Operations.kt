package net.chompsoftware.k6502.hardware

@ExperimentalUnsignedTypes
internal typealias Operation = (instruction: InstructionSet, state: CpuState, memory: Memory) -> CpuState

@ExperimentalUnsignedTypes
internal object Operations {
    val notImplementedOperation = { instruction: InstructionSet, state: CpuState, _: Memory ->
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
        if(VERBOSE) println("Branch ${check} from ${state.programCounter.toString(16)} to ${newState.programCounter.toString(16)}")
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
        memory.writeUInt16ToStack(state.stackPointer, state.programCounter.toUInt() + 2u)
        memory.writeUByteToStack(state.stackPointer - 2, state.copy(isBreakCommandFlag = true).readFlagsAsUbyte())

        state.copy(
                cycleCount = state.cycleCount + instruction.cy,
                isBreakCommandFlag = true,
                programCounter = memory.readInt16(state.breakLocation),
                stackPointer = state.stackPointer - 3,
                isInterruptDisabledFlag = true

        )
    }

    val compareAccumulator = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        val compareTo = memory.readUsing(instruction.ad, state)
        if(VERBOSE) println("compareAccumulator for ${instruction}: aRegister=${state.aRegister.toString(16)} compareTo=${compareTo.toString(16)}")

        state.copyRelativeWithFlags(
                instruction,
                zeroFlag = state.aRegister == compareTo,
                carryFlag = state.aRegister >= compareTo,
                negativeFlag = state.aRegister < compareTo
        )
    }

    val compareX = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        val compareTo = memory.readUsing(instruction.ad, state)
        state.copyRelativeWithFlags(
                instruction,
                zeroFlag = state.xRegister == compareTo,
                carryFlag = state.xRegister >= compareTo,
                negativeFlag = state.xRegister < compareTo
        )
    }

    val compareY = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        val compareTo = memory.readUsing(instruction.ad, state)
        state.copyRelativeWithFlags(
                instruction,
                zeroFlag = state.yRegister == compareTo,
                carryFlag = state.yRegister >= compareTo,
                negativeFlag = state.yRegister < compareTo
        )
    }

    val decrementx = { instruction: InstructionSet, state: CpuState, _: Memory ->
        state.copyRelativeWithX(instruction, state.xRegister - 1u)
    }

    val decrementy = { instruction: InstructionSet, state: CpuState, _: Memory ->
        state.copyRelativeWithY(instruction,state.yRegister - 1u)
    }

    val incrementx = { instruction: InstructionSet, state: CpuState, _: Memory ->
        state.copyRelativeWithX(instruction, state.xRegister + 1u)
    }

    val incrementy = { instruction: InstructionSet, state: CpuState, _: Memory ->
        state.copyRelativeWithY(instruction,state.yRegister + 1u)
    }

    val exclusiveOr = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        state.copyRelativeWithA(
                instruction,
                state.aRegister.xor(memory.readUsing(instruction.ad, state)))
    }

    val orWithAccumulator = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        state.copyRelativeWithA(
                instruction,
                state.aRegister.or(memory.readUsing(instruction.ad, state)))
    }

    val storeAccumulator = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        val location = memory.positionUsing(instruction.ad, state)
        memory[location] = state.aRegister.toUByte()
        state.incrementCountersBy(instruction.ad.size, instruction.cy)
    }

    val storeX = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        val location = memory.positionUsing(instruction.ad, state)
        memory[location] = state.xRegister.toUByte()
        state.incrementCountersBy(instruction.ad.size, instruction.cy)
    }

    val loadAccumulator = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        val register = memory.readUsing(instruction.ad, state)
        state.copyRelativeWithA(
                instruction,
                register)
    }

    val loadx = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        val register = memory.readUsing(instruction.ad, state)
        state.copyRelativeWithX(instruction, register)
    }

    val loady = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        val register = memory.readUsing(instruction.ad, state)
        state.copyRelativeWithY(instruction, register)
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

    val returnFromInterrupt = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        val flagsByte = memory.readUIntFromStack(state.stackPointer + 1)
        val position = memory.readUInt16FromStack(state.stackPointer + 1)
        state.setFlagsUsingUByte(
                flagsByte,
                position.toInt(),
                state.stackPointer + 3,
                instruction.cy
        )
    }

    val pushAccumulator = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        memory.writeUByteToStack(state.stackPointer, state.aRegister.toUByte())
        state.copy(
                cycleCount = state.cycleCount + instruction.cy,
                programCounter = state.programCounter + instruction.ad.size,
                stackPointer = state.stackPointer - 1
        )
    }

    val pullAccumulator = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        state.copyRelativeWithA(
                instruction,
                memory.readUIntFromStack(state.stackPointer + 1),
                state.stackPointer + 1
        )
    }

    val pushProcessorStatus = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        memory.writeUByteToStack(state.stackPointer, state.readFlagsAsUbyte())
        state.copy(
                cycleCount = state.cycleCount + instruction.cy,
                programCounter = state.programCounter + instruction.ad.size,
                stackPointer = state.stackPointer - 1
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
        state.copyRelativeWithX(instruction, state.stackPointer.toUInt())
    }

    val transferAccumulatorToX = { instruction: InstructionSet, state: CpuState, _: Memory ->
        state.copyRelativeWithX(instruction, state.aRegister)
    }

    val transferAccumulatorToY = { instruction: InstructionSet, state: CpuState, _: Memory ->
        state.copyRelativeWithY(instruction, state.aRegister)
    }

    val transferYtoAccumulator = { instruction: InstructionSet, state: CpuState, _: Memory ->
        state.copyRelativeWithA(instruction, state.yRegister)
    }

    val transferXtoAccumulator = { instruction: InstructionSet, state: CpuState, _: Memory ->
        state.copyRelativeWithA(instruction, state.xRegister)
    }

    val noOperation = { instruction: InstructionSet, state: CpuState, _: Memory ->
        state.incrementCountersBy(instruction.ad.size, instruction.cy)
    }
}