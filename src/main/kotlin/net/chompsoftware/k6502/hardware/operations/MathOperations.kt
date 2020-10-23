package net.chompsoftware.k6502.hardware.operations

import net.chompsoftware.k6502.hardware.*


@ExperimentalUnsignedTypes
internal object MathOperations {

    val addWithCarry = { instruction: InstructionSet, state: CpuState, value: UInt ->
        val sum = state.aRegister + value + if(state.isCarryFlag) 1u else 0u

        if (VERBOSE) println("add for ${instruction}: aRegister=${state.aRegister.toHex()} value=${value.toHex()} result=${sum.toHex()}")

        state.copyRelativeWithA(
                instruction,
                sum,
                carryFlag = sum > 0xffu,
                overflowFlag = false
        )
    }

    val subtractWithCarry = { instruction: InstructionSet, state: CpuState, value: UInt ->
        val sum = state.aRegister.toUByte() - value.toUByte() - if(state.isCarryFlag) 0u else 1u

        if (VERBOSE) println("subtract for ${instruction}: aRegister=${state.aRegister.toHex()} value=${value.toHex()} result=${sum.toHex()}")

        state.copyRelativeWithA(
                instruction,
                sum,
                carryFlag = sum <= state.aRegister,
                overflowFlag = false
        )
    }

    val logicalAnd = { instruction: InstructionSet, state: CpuState, value: UInt ->
        val result = state.aRegister.and(value)
        if (VERBOSE) println("and for ${instruction}: aRegister=${state.aRegister.toHex()} value=${value.toHex()} result=${result.toHex()}")

        state.copyRelativeWithA(
                instruction,
                result)
    }

    val decrementx = { instruction: InstructionSet, state: CpuState, _: Memory ->
        state.copyRelativeWithX(instruction, state.xRegister - 1u)
    }

    val decrementy = { instruction: InstructionSet, state: CpuState, _: Memory ->
        state.copyRelativeWithY(instruction, state.yRegister - 1u)
    }

    val decrement = { instruction: InstructionSet, state: CpuState, memory: Memory, position: UInt ->
        val decremented = memory[position] - 1u
        memory[position] = decremented.toUByte()
        state.copyRelativeWithFlags(instruction,
                negativeFlag = (decremented and 0x80u) > 0u,
                zeroFlag = decremented.and(0xffu) == 0u
        )
    }

    val incrementx = { instruction: InstructionSet, state: CpuState, _: Memory ->
        state.copyRelativeWithX(instruction, state.xRegister + 1u)
    }

    val incrementy = { instruction: InstructionSet, state: CpuState, _: Memory ->
        state.copyRelativeWithY(instruction, state.yRegister + 1u)
    }

    val increment = { instruction: InstructionSet, state: CpuState, memory: Memory, position: UInt ->
        val incremented = memory[position] + 1u
        memory[position] = incremented.toUByte()
        state.copyRelativeWithFlags(instruction,
                negativeFlag = (incremented and 0x80u) > 0u,
                zeroFlag = incremented.and(0xffu) == 0u
        )
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

    val rotateRight = { instruction: InstructionSet, state: CpuState, memory: Memory ->
        val position = if (instruction.ad == Address.none) null else memory.positionUsing(instruction.ad, state)
        val valueToShift = position?.let { memory.get(position).toUInt() } ?: state.aRegister
        val shiftedByte = valueToShift.shr(1) + if(state.isCarryFlag) 0x80u else 0u
        val carry = valueToShift.and(0x1u) > 0u

        if (VERBOSE) println("ror for ${instruction}: toShift=${valueToShift.toHex()} result=${shiftedByte.toHex()} carry=${carry}")


        position?.let {
            state.copyRelativeWithFlags(instruction,
                    carryFlag = carry,
                    zeroFlag = shiftedByte == 0u,
                    negativeFlag = (shiftedByte and 0x80u) > 0u
            ).also { memory[position] = shiftedByte.toUByte() }
        } ?: state.copyRelativeWithA(instruction, shiftedByte, carryFlag = carry)
    }
}