package net.chompsoftware.k6502.hardware.operations

import net.chompsoftware.k6502.hardware.CpuState
import net.chompsoftware.k6502.hardware.InstructionSet
import net.chompsoftware.k6502.hardware.Memory
import net.chompsoftware.k6502.hardware.VERBOSE


@ExperimentalUnsignedTypes
internal object ComparisonOperations {
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
        if(VERBOSE) println("compareX for ${instruction}: xRegister=${state.xRegister.toString(16)} compareTo=${compareTo.toString(16)}")

        state.copyRelativeWithFlags(
                instruction,
                zeroFlag = state.xRegister == compareTo,
                carryFlag = state.xRegister >= compareTo,
                negativeFlag = state.xRegister < compareTo
        )
    }

    val compareY = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        val compareTo = memory.readUsing(instruction.ad, state)
        if(VERBOSE) println("compareY for ${instruction}: yRegister=${state.yRegister.toString(16)} compareTo=${compareTo.toString(16)}")

        state.copyRelativeWithFlags(
                instruction,
                zeroFlag = state.yRegister == compareTo,
                carryFlag = state.yRegister >= compareTo,
                negativeFlag = state.yRegister < compareTo
        )
    }
}