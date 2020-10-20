package net.chompsoftware.k6502.hardware.operations

import net.chompsoftware.k6502.hardware.CpuState
import net.chompsoftware.k6502.hardware.InstructionSet
import net.chompsoftware.k6502.hardware.Memory


@ExperimentalUnsignedTypes
internal object MemoryOperations {
    val loadAccumulator = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        val register = memory.readUsing(instruction.ad, state)
        state.copyRelativeWithA(
                instruction,
                register)
    }

    val loadx = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        val register = memory.readUsing(instruction.ad, state)
        state.copyRelativeWithX(instruction, register)
    }

    val loady = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        val register = memory.readUsing(instruction.ad, state)
        state.copyRelativeWithY(instruction, register)
    }

    val storeAccumulator = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        val location = memory.positionUsing(instruction.ad, state)
        memory[location] = state.aRegister.toUByte()
        state.incrementCountersBy(instruction.ad.size, instruction.cy)
    }

    val storeX = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        val location = memory.positionUsing(instruction.ad, state)
        memory[location] = state.xRegister.toUByte()
        state.incrementCountersBy(instruction.ad.size, instruction.cy)
    }
}