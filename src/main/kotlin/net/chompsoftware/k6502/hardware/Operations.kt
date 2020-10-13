package net.chompsoftware.k6502.hardware

const val LOG_OPERATIONS: Boolean = true
const val STACK_START = 0x100

@ExperimentalUnsignedTypes
internal typealias Operation = (instruction: InstructionSet, state: CpuState, memory: Memory) -> CpuState

@ExperimentalUnsignedTypes
internal object Operations {
    fun log(s: String) {
        if (LOG_OPERATIONS) println(s)
    }

    val notImplementedOperation = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        throw Error("Not Implemented Operation ${instruction.name}:${instruction.u.toString(16)} at ${state.programCounter.toString(16)}")
    }


    val addWithCarry = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        val amount = memory.readUsing(instruction.ad, state)
        val sum = state.aRegister + amount

        state.copy(
                programCounter = state.programCounter + instruction.ad.size,
                aRegister = if (sum > 0xffu) sum - 0x100u else sum,
                isCarryFlag = sum > 0xffu
        )
    }

    private val branchIfTrue = { check: Boolean, instruction: InstructionSet, state: CpuState, memory: Memory ->
        val newState = if (check) {
            val location = memory.readUsing(instruction.ad, state)
            val newLocation = if (location >= 0x80u) -0x100 + location.toInt() else location.toInt()
            state.copy(programCounter = state.programCounter + instruction.ad.size + newLocation)

        } else state.incrementCounterBy(instruction.ad.size)
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

    val brk = { _: InstructionSet, state: CpuState, memory: Memory ->
        state.copy(isBreakCommandFlag = true, programCounter = memory.readInt16(state.breakLocation))
    }

    val compareAccumulator = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        val compareTo = memory.readUsing(instruction.ad, state)
        state.copy(
                programCounter = state.programCounter + instruction.ad.size,
                isZeroFlag = state.aRegister == compareTo,
                isCarryFlag = state.aRegister >= compareTo,
                isNegativeFlag = state.aRegister < compareTo
        )
    }

    val compareX = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        val compareTo = memory.readUsing(instruction.ad, state)
        state.copy(
                programCounter = state.programCounter + instruction.ad.size,
                isZeroFlag = state.xRegister == compareTo,
                isCarryFlag = state.xRegister >= compareTo,
                isNegativeFlag = state.xRegister < compareTo
        )
    }

    val compareY = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        val compareTo = memory.readUsing(instruction.ad, state)
        state.copy(
                programCounter = state.programCounter + instruction.ad.size,
                isZeroFlag = state.yRegister == compareTo,
                isCarryFlag = state.yRegister >= compareTo,
                isNegativeFlag = state.yRegister < compareTo
        )
    }

    val decrementx = { _: InstructionSet, state: CpuState, _: Memory ->
        state.copyWithX(state.xRegister - 1u, programCounter = state.programCounter + 1)
    }

    val decrementy = { _: InstructionSet, state: CpuState, _: Memory ->
        state.copyWithY(state.yRegister - 1u, programCounter = state.programCounter + 1)
    }

    val exclusiveOr = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        state.copyWithA(state.aRegister.xor(memory.readUsing(instruction.ad, state)), state.programCounter + instruction.ad.size)
    }

    val storeAccumulator = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        val location = memory.positionUsing(instruction.ad, state)
        memory[location] = state.aRegister.toUByte()
        state.incrementCounterBy(instruction.ad.size)
    }

    val loadAccumulator = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        val register = memory.readUsing(instruction.ad, state)
        state.copyWithA(register, state.programCounter + instruction.ad.size)
    }

    val loadx = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        val register = memory.readUsing(instruction.ad, state)
        state.copyWithX(register, state.programCounter + instruction.ad.size)
    }

    val loady = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        val register = memory.readUsing(instruction.ad, state)
        state.copyWithY(register, state.programCounter + instruction.ad.size)
    }

    val jump = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        state.copy(programCounter = memory.positionUsing(instruction.ad, state).toInt())
    }

    val clearCarry = { instruction: InstructionSet, state: CpuState, _: Memory ->
        state.copy(
                programCounter = state.programCounter + instruction.ad.size,
                isCarryFlag = false
        )
    }

    val clearDecimal = { instruction: InstructionSet, state: CpuState, _: Memory ->
        state.copy(
                programCounter = state.programCounter + instruction.ad.size,
                isDecimalFlag = false
        )
    }

    val pushAccumulator = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        memory[STACK_START + state.stackPointer] = state.aRegister.toUByte()
        state.copy(
                programCounter = state.programCounter + instruction.ad.size,
                stackPointer = state.stackPointer-1
        )
    }

    val pullAccumulator = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        state.copy(
                programCounter = state.programCounter + instruction.ad.size,
                stackPointer = state.stackPointer + 1,
                aRegister = memory.readUInt(STACK_START + state.stackPointer + 1)
        )
    }

    val pullProcessorStatus = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        state.setFlagsUsingUByte(
                memory.readUInt(STACK_START + state.stackPointer + 1),
                state.programCounter + instruction.ad.size,
                state.stackPointer + 1
        )
    }

    val transferXToStack = { instruction: InstructionSet, state: CpuState, _: Memory ->
        state.copy(
                programCounter = state.programCounter + instruction.ad.size,
                stackPointer = state.xRegister.toInt()
        )
    }

    val transferStackToX = { instruction: InstructionSet, state: CpuState, _: Memory ->
        state.copyWithX(state.stackPointer.toUInt(), state.programCounter + instruction.ad.size)
    }

    val transferAccumulatorToX = { instruction: InstructionSet, state: CpuState, _: Memory ->
        state.copyWithX(state.aRegister, programCounter = state.programCounter + instruction.ad.size)
    }

    val transferAccumulatorToY = { instruction: InstructionSet, state: CpuState, _: Memory ->
        state.copyWithY(state.aRegister, programCounter = state.programCounter + instruction.ad.size)
    }

    val transferYtoAccumulator = { instruction: InstructionSet, state: CpuState, _: Memory ->
        state.copyWithA(state.yRegister, programCounter = state.programCounter + instruction.ad.size)
    }

    val transferXtoAccumulator = { instruction: InstructionSet, state: CpuState, _: Memory ->
        state.copyWithA(state.xRegister, programCounter = state.programCounter + instruction.ad.size)
    }

    val noOperation = { _: InstructionSet, state: CpuState, _: Memory ->
        state.incrementCounterBy(1)
    }
}