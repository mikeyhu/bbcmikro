package net.chompsoftware.k6502.hardware

@ExperimentalUnsignedTypes
internal typealias Operation = (instruction: InstructionSet, state: CpuState, memory: Memory) -> CpuState

@ExperimentalUnsignedTypes
internal object Operations {
    val brk = { _: InstructionSet, state: CpuState, memory: Memory ->
        state.copy(isBreakCommandFlag = true, programCounter = memory.readInt16(state.breakLocation))
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