package net.chompsoftware.k6502.hardware.operations

import net.chompsoftware.k6502.hardware.*


@ExperimentalUnsignedTypes
internal object ComparisonOperations {
    val compareAccumulator = { instruction: InstructionSet, state: CpuState, compareTo: UInt ->
        if (VERBOSE) println("compareAccumulator for ${instruction}: aRegister=${state.aRegister.toHex()} compareTo=${compareTo.toHex()}")

        state.copyRelativeWithFlags(
                instruction,
                zeroFlag = state.aRegister == compareTo,
                carryFlag = state.aRegister >= compareTo,
                negativeFlag = state.aRegister < compareTo
        )
    }

    val compareX = { instruction: InstructionSet, state: CpuState, compareTo: UInt ->
        if (VERBOSE) println("compareX for ${instruction}: xRegister=${state.xRegister.toHex()} compareTo=${compareTo.toHex()}")

        state.copyRelativeWithFlags(
                instruction,
                zeroFlag = state.xRegister == compareTo,
                carryFlag = state.xRegister >= compareTo,
                negativeFlag = state.xRegister < compareTo
        )
    }

    val compareY = { instruction: InstructionSet, state: CpuState, compareTo: UInt ->
        if (VERBOSE) println("compareY for ${instruction}: yRegister=${state.yRegister.toHex()} compareTo=${compareTo.toHex()}")

        state.copyRelativeWithFlags(
                instruction,
                zeroFlag = state.yRegister == compareTo,
                carryFlag = state.yRegister >= compareTo,
                negativeFlag = state.yRegister < compareTo
        )
    }
}