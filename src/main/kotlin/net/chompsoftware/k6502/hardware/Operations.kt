package net.chompsoftware.k6502.hardware

@ExperimentalUnsignedTypes
internal typealias Operation = (instruction: InstructionSet, state: CpuState, memory: Memory) -> CpuState

@ExperimentalUnsignedTypes
internal object Operations {

    private val branchIfTrue = { check: Boolean, instruction: InstructionSet, state: CpuState, memory: Memory ->
        if(check) {
            val location = memory.readUsing(instruction.ad, state)
            val newLocation = if (location > 0x80u) -0xff + location.toInt() else location.toInt()
            state.copy(programCounter = state.programCounter + instruction.ad.size + newLocation)

        } else state.incrementCounterBy(instruction.ad.size)
    }

    val branchOnNotEqual = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        branchIfTrue(!state.isZeroFlag, instruction, state, memory)
    }

    val branchOnEqual = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        branchIfTrue(state.isZeroFlag, instruction, state, memory)
    }

    val brk = { _: InstructionSet, state: CpuState, memory: Memory ->
        state.copy(isBreakCommandFlag = true, programCounter = memory.readInt16(state.breakLocation))
    }

    val decrementx = { _: InstructionSet, state: CpuState, memory: Memory ->
        state.copyWithX(state.xRegister-1u, programCounter = state.programCounter + 1)
    }

    val storeAccumulator = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        val location = memory.readUsing(instruction.ad, state)
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
        state.copy(programCounter = memory.readUsing(instruction.ad, state).toInt())
    }

    val clearDecimal = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        state.copy(
                programCounter = state.programCounter + instruction.ad.size,
                isDecimalFlag = false
        )
    }

    val transferXToStack = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        state.copy(
                programCounter = state.programCounter + instruction.ad.size,
                stackPointer = state.xRegister.toInt()
        )
    }
}