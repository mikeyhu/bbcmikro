package net.chompsoftware.k6502.hardware.operations

import net.chompsoftware.k6502.hardware.*

@ExperimentalUnsignedTypes
internal typealias Operation = (instruction: InstructionSet, state: CpuState, memory: RamInterface) -> CpuState

@ExperimentalUnsignedTypes
internal typealias ReadOperation = (instruction: InstructionSet, state: CpuState, value: UInt) -> CpuState

@ExperimentalUnsignedTypes
internal typealias PositionOperation = (instruction: InstructionSet, state: CpuState, memory: RamInterface, position: UInt) -> CpuState

@ExperimentalUnsignedTypes
internal object Operations {

    inline fun withRead(crossinline op:ReadOperation):Operation {
        return {instruction: InstructionSet, state: CpuState, memory: RamInterface ->
            op(instruction, state, memory.readUsing(instruction.ad, state))
        }
    }

    inline fun withPosition(crossinline op:PositionOperation):Operation {
        return {instruction: InstructionSet, state: CpuState, memory: RamInterface ->
            op(instruction, state, memory, memory.positionUsing(instruction.ad, state))
        }
    }

    val notImplementedOperation = { instruction: InstructionSet, state: CpuState, _: RamInterface ->
        throw NotImplementedError("Not Implemented Operation ${instruction.name}:${instruction.u.toHex()} at ${state.programCounter.toHex()}")
    }

    val brk = { instruction: InstructionSet, state: CpuState, memory: RamInterface ->
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


    val jump = { instruction: InstructionSet, state: CpuState, memory: RamInterface ->
        state.copy(
                cycleCount = state.cycleCount + instruction.cy,
                programCounter = memory.positionUsing(instruction.ad, state).toInt()
        )
    }

    val jumpToSubroutine = { instruction: InstructionSet, state: CpuState, memory: RamInterface ->
        memory.writeUInt16ToStack(state.stackPointer, state.programCounter.toUInt() + 2u)
        state.copy(
                cycleCount = state.cycleCount + instruction.cy,
                programCounter = memory.positionUsing(instruction.ad, state).toInt(),
                stackPointer = state.stackPointer - 2
        )
    }

    val returnFromSubroutine = { instruction: InstructionSet, state: CpuState, memory: RamInterface ->
        state.copy(
                cycleCount = state.cycleCount + instruction.cy,
                programCounter = memory.readUInt16FromStack(state.stackPointer + 1).toInt() + 1,
                stackPointer = state.stackPointer + 2
        )
    }

    val returnFromInterrupt = { instruction: InstructionSet, state: CpuState, memory: RamInterface ->
        val flagsByte = memory.readUIntFromStack(state.stackPointer + 1)
        val position = memory.readUInt16FromStack(state.stackPointer + 2)
        state.setFlagsUsingUByte(
                flagsByte,
                position.toInt(),
                state.stackPointer + 3,
                instruction.cy
        )
    }

    val noOperation = { instruction: InstructionSet, state: CpuState, _: RamInterface ->
        state.incrementByInstruction(instruction)
    }
}