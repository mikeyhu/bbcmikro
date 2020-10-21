package net.chompsoftware.k6502.hardware.operations

import net.chompsoftware.k6502.hardware.*


@ExperimentalUnsignedTypes
internal object BranchOperations {

    private val branchIfTrue = { check: Boolean, instruction: InstructionSet, state: CpuState, memory: Memory ->
        val newState = if (check) {
            val location = memory.readUsing(instruction.ad, state)
            val newLocation = if (location >= 0x80u) -0x100 + location.toInt() else location.toInt()
            state.copy(
                    cycleCount = state.cycleCount + instruction.cy + 1,
                    programCounter = state.programCounter + instruction.ad.size + newLocation
            )

        } else state.incrementCountersBy(instruction.ad.size, instruction.cy)
        if(VERBOSE) println("Branch ${check} from ${state.programCounter.toHex()} to ${newState.programCounter.toHex()}")
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
}