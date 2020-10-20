package net.chompsoftware.k6502.hardware.operations

import net.chompsoftware.k6502.hardware.CpuState
import net.chompsoftware.k6502.hardware.InstructionSet
import net.chompsoftware.k6502.hardware.Memory


@ExperimentalUnsignedTypes
internal object StackOperations {

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
}