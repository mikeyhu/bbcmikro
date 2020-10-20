package net.chompsoftware.k6502.hardware

@ExperimentalUnsignedTypes
internal typealias Operation = (instruction: InstructionSet, state: CpuState, memory: Memory) -> CpuState

@ExperimentalUnsignedTypes
internal object Operations {
    val notImplementedOperation = { instruction: InstructionSet, state: CpuState, _: Memory ->
        throw NotImplementedError("Not Implemented Operation ${instruction.name}:${instruction.u.toString(16)} at ${state.programCounter.toString(16)}")
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