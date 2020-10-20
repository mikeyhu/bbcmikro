package net.chompsoftware.k6502.hardware.operations

import net.chompsoftware.k6502.hardware.CpuState
import net.chompsoftware.k6502.hardware.InstructionSet
import net.chompsoftware.k6502.hardware.Memory


@ExperimentalUnsignedTypes
internal object FlagOperations {
    val clearInterrupt = { instruction: InstructionSet, state: CpuState, _: Memory ->
        state.copyRelativeWithFlags(instruction, interruptDisabledFlag = false)
    }

    val setInterrupt = { instruction: InstructionSet, state: CpuState, _: Memory ->
        state.copyRelativeWithFlags(instruction, interruptDisabledFlag = true)
    }

    val clearCarry = { instruction: InstructionSet, state: CpuState, _: Memory ->
        state.copyRelativeWithFlags(instruction, carryFlag = false)
    }

    val setCarry = { instruction: InstructionSet, state: CpuState, _: Memory ->
        state.copyRelativeWithFlags(instruction, carryFlag = true)
    }

    val clearDecimal = { instruction: InstructionSet, state: CpuState, _: Memory ->
        state.copyRelativeWithFlags(instruction, decimalFlag = false)
    }

    val setDecimal = { instruction: InstructionSet, state: CpuState, _: Memory ->
        state.copyRelativeWithFlags(instruction, decimalFlag = true)
    }

    val clearOverflow = { instruction: InstructionSet, state: CpuState, _: Memory ->
        state.copyRelativeWithFlags(instruction, overflowFlag = false)
    }
}