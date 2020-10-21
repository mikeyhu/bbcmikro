package net.chompsoftware.k6502.hardware.operations

import net.chompsoftware.k6502.hardware.CpuState
import net.chompsoftware.k6502.hardware.InstructionSet
import net.chompsoftware.k6502.hardware.Memory
import net.chompsoftware.k6502.hardware.toHex

@ExperimentalUnsignedTypes
internal typealias Operation = (instruction: InstructionSet, state: CpuState, memory: Memory) -> CpuState

@ExperimentalUnsignedTypes
internal object Operations {
    val notImplementedOperation = { instruction: InstructionSet, state: CpuState, _: Memory ->
        throw NotImplementedError("Not Implemented Operation ${instruction.name}:${instruction.u.toHex()} at ${state.programCounter.toHex()}")
    }

    val brk = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        memory.writeUInt16ToStack(state.stackPointer, state.programCounter.toUInt() + 2u)
        memory.writeUByteToStack(state.stackPointer - 2, state.copy(isBreakCommandFlag = true).readFlagsAsUbyte())

        state.copy(
                cycleCount = state.cycleCount + instruction.cy,
                isBreakCommandFlag = true,
                programCounter = memory.readInt16(state.breakLocation),
                stackPointer = state.stackPointer - 3,
                isInterruptDisabledFlag = true

        )
    }


    val jump = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        state.copy(
                cycleCount = state.cycleCount + instruction.cy,
                programCounter = memory.positionUsing(instruction.ad, state).toInt()
        )
    }

    val jumpToSubroutine = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        memory.writeUInt16ToStack(state.stackPointer, state.programCounter.toUInt() + 2u)
        state.copy(
                cycleCount = state.cycleCount + instruction.cy,
                programCounter = memory.positionUsing(instruction.ad, state).toInt(),
                stackPointer = state.stackPointer - 2
        )
    }

    val returnFromSubroutine = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        state.copy(
                cycleCount = state.cycleCount + instruction.cy,
                programCounter = memory.readUInt16FromStack(state.stackPointer + 1).toInt() + 1,
                stackPointer = state.stackPointer + 2
        )
    }

    val returnFromInterrupt = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        val flagsByte = memory.readUIntFromStack(state.stackPointer + 1)
        val position = memory.readUInt16FromStack(state.stackPointer + 2)
        state.setFlagsUsingUByte(
                flagsByte,
                position.toInt(),
                state.stackPointer + 3,
                instruction.cy
        )
    }

    val noOperation = { instruction: InstructionSet, state: CpuState, _: Memory ->
        state.incrementByInstruction(instruction)
    }
}