package net.chompsoftware.bbcmikro.hardware.operations

import net.chompsoftware.bbcmikro.hardware.CpuState
import net.chompsoftware.bbcmikro.hardware.InstructionSet
import net.chompsoftware.bbcmikro.hardware.RamInterface


@ExperimentalUnsignedTypes
internal object StackOperations {

    val pushAccumulator = { instruction: InstructionSet, state: CpuState, memory: RamInterface ->
        memory.writeUByteToStack(state.stackPointer, state.aRegister.toUByte())
        state.copy(
                cycleCount = state.cycleCount + instruction.cy,
                programCounter = state.programCounter + instruction.ad.size,
                stackPointer = state.removeUnderflow(state.stackPointer - 1)
        )
    }

    val pullAccumulator = { instruction: InstructionSet, state: CpuState, memory: RamInterface ->
        state.copyRelativeWithA(
                instruction,
                memory.readUIntFromStack(state.stackPointer + 1),
                state.removeOverflow(state.stackPointer + 1)
        )
    }

    val pushProcessorStatus = { instruction: InstructionSet, state: CpuState, memory: RamInterface ->
        memory.writeUByteToStack(state.stackPointer, state.readFlagsAsUbyte())
        state.copy(
                cycleCount = state.cycleCount + instruction.cy,
                programCounter = state.programCounter + instruction.ad.size,
                stackPointer = state.removeUnderflow(state.stackPointer - 1)
        )
    }

    val pullProcessorStatus = { instruction: InstructionSet, state: CpuState, memory: RamInterface ->
        val overflowedStack = state.removeOverflow(state.stackPointer + 1)
        state.setFlagsUsingUByte(
                memory.readUIntFromStack(overflowedStack),
                state.programCounter + instruction.ad.size,
                overflowedStack,
                instruction.cy
        )
    }
}