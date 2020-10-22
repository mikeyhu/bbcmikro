package net.chompsoftware.k6502.hardware.operations

import net.chompsoftware.k6502.hardware.*


@ExperimentalUnsignedTypes
internal object MathOperations {

    val addWithCarry = { instruction: InstructionSet, state: CpuState, value: UInt ->
        val sum = state.aRegister + value

        state.copy(
                cycleCount = state.cycleCount + instruction.cy,
                programCounter = state.programCounter + instruction.ad.size,
                aRegister = if (sum > 0xffu) sum - 0x100u else sum,
                isCarryFlag = sum > 0xffu
        )
    }

    val decrementx = { instruction: InstructionSet, state: CpuState, _: Memory ->
        state.copyRelativeWithX(instruction, state.xRegister - 1u)
    }

    val decrementy = { instruction: InstructionSet, state: CpuState, _: Memory ->
        state.copyRelativeWithY(instruction, state.yRegister - 1u)
    }

    val incrementx = { instruction: InstructionSet, state: CpuState, _: Memory ->
        state.copyRelativeWithX(instruction, state.xRegister + 1u)
    }

    val incrementy = { instruction: InstructionSet, state: CpuState, _: Memory ->
        state.copyRelativeWithY(instruction, state.yRegister + 1u)
    }

    val exclusiveOr = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        state.copyRelativeWithA(
                instruction,
                state.aRegister.xor(memory.readUsing(instruction.ad, state)))
    }

    val orWithAccumulator = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        state.copyRelativeWithA(
                instruction,
                state.aRegister.or(memory.readUsing(instruction.ad, state)))
    }

    val bitWithAccumulator = { instruction: InstructionSet, state: CpuState, value: UInt ->
        val andByte = state.aRegister and value

        val zeroFlag = andByte == 0u
        val negativeFlag = value and CpuSettings.NEGATIVE_BYTE_POSITION > 0u
        val overflowFlag = value and CpuSettings.OVERFLOW_BYTE_POSITION > 0u

        if (VERBOSE) println("bit for ${instruction}: aRegister=${state.aRegister.toHex()} result=${andByte.toHex()} zro=${zeroFlag} neg=${negativeFlag} ovr=${overflowFlag}")

        state.copyRelativeWithFlags(
                instruction,
                zeroFlag = zeroFlag,
                negativeFlag = negativeFlag,
                overflowFlag = overflowFlag
        )
    }

    val arithmeticShiftLeft = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        val position = if (instruction.ad == Address.none) null else memory.positionUsing(instruction.ad, state)
        val shiftedValue = (position?.let { memory.get(position).toUInt() } ?: state.aRegister).shl(1)
        val carry = shiftedValue.shr(8) > 0u
        val shiftedByte = shiftedValue.and(0xffu)

        position?.let {
            state.copyRelativeWithFlags(instruction,
                    carryFlag = carry,
                    zeroFlag = shiftedByte == 0u,
                    negativeFlag = (shiftedByte and 0x80u) > 0u
            ).also { memory[position] = shiftedByte.toUByte() }
        } ?: state.copyRelativeWithA(instruction, shiftedByte, carryFlag = carry)
    }

    val logicalShiftRight = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        val position = if (instruction.ad == Address.none) null else memory.positionUsing(instruction.ad, state)
        val valueToShift = position?.let { memory.get(position).toUInt() } ?: state.aRegister
        val shiftedByte = valueToShift.shr(1)
        val carry = valueToShift.and(0x1u) > 0u

        position?.let {
            state.copyRelativeWithFlags(instruction,
                    carryFlag = carry,
                    zeroFlag = shiftedByte == 0u,
                    negativeFlag = (shiftedByte and 0x80u) > 0u
            ).also { memory[position] = shiftedByte.toUByte() }
        } ?: state.copyRelativeWithA(instruction, shiftedByte, carryFlag = carry)
    }

    val rotateLeft = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        val position = if (instruction.ad == Address.none) null else memory.positionUsing(instruction.ad, state)
        val shiftedValue = (position?.let { memory.get(position).toUInt() } ?: state.aRegister).shl(1) + if(state.isCarryFlag) 1u else 0u

        val shiftedByte = shiftedValue.and(0xffu)
        val carry = shiftedValue.shr(8) > 0u

        position?.let {
            state.copyRelativeWithFlags(instruction,
                    carryFlag = carry,
                    zeroFlag = shiftedByte == 0u,
                    negativeFlag = (shiftedByte and 0x80u) > 0u
            ).also { memory[position] = shiftedByte.toUByte() }
        } ?: state.copyRelativeWithA(instruction, shiftedByte, carryFlag = carry)
    }
}