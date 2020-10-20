package net.chompsoftware.k6502.hardware.operations

import net.chompsoftware.k6502.hardware.CpuState
import net.chompsoftware.k6502.hardware.InstructionSet
import net.chompsoftware.k6502.hardware.Memory


@ExperimentalUnsignedTypes
internal object TransferOperations {
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
}