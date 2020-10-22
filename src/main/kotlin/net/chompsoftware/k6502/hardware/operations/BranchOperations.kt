package net.chompsoftware.k6502.hardware.operations

import net.chompsoftware.k6502.hardware.*


@ExperimentalUnsignedTypes
internal object BranchOperations {

    val branchOnCarryClear = { instruction: InstructionSet, state: CpuState, value: UInt ->
        branchIfTrue(!state.isCarryFlag, instruction, state, value)
    }

    val branchOnCarrySet = { instruction: InstructionSet, state: CpuState, value: UInt ->
        branchIfTrue(state.isCarryFlag, instruction, state, value)
    }

    val branchOnMinus = { instruction: InstructionSet, state: CpuState, value: UInt ->
        branchIfTrue(state.isNegativeFlag, instruction, state, value)
    }

    val branchOnNotEqual = { instruction: InstructionSet, state: CpuState, value: UInt ->
        branchIfTrue(!state.isZeroFlag, instruction, state, value)
    }

    val branchOnEqual = { instruction: InstructionSet, state: CpuState, value: UInt ->
        branchIfTrue(state.isZeroFlag, instruction, state, value)
    }

    val branchOnPlus = { instruction: InstructionSet, state: CpuState, value: UInt ->
        branchIfTrue(!state.isNegativeFlag, instruction, state, value)
    }

    val branchOnOverflowClear = { instruction: InstructionSet, state: CpuState, value: UInt ->
        branchIfTrue(!state.isOverflowFlag, instruction, state, value)
    }

    val branchOnOverflowSet = { instruction: InstructionSet, state: CpuState, value: UInt ->
        branchIfTrue(state.isOverflowFlag, instruction, state, value)
    }

    private val branchIfTrue = { check: Boolean, instruction: InstructionSet, state: CpuState, location: UInt ->
        val newState = if (check) {
            val newLocation = if (location >= 0x80u) -0x100 + location.toInt() else location.toInt()
            state.copy(
                    cycleCount = state.cycleCount + instruction.cy + 1,
                    programCounter = state.programCounter + instruction.ad.size + newLocation
            )

        } else state.incrementByInstruction(instruction)
        if (VERBOSE) println("Branch ${check} from ${state.programCounter.toHex()} to ${newState.programCounter.toHex()}")
        newState
    }
}