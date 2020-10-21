package net.chompsoftware.k6502.hardware

private const val STACK_START = 0x100

@ExperimentalUnsignedTypes
class Memory(val store: UByteArray) {
    operator fun get(position: Int) = store[position]
    operator fun set(position: Int, value: UByte) {
        store[position] = value
    }

    operator fun get(position: UInt) = store[position.toInt()]
    operator fun set(position: UInt, value: UByte) {
        store[position.toInt()] = value
    }

    fun readUInt16(position: Int): UInt {
        return toUInt16(store[position], store[position + 1])
    }

    fun readInt16(position: Int): Int {
        return toInt16(store[position], store[position + 1])
    }

    fun readUIntFromStack(stackPosition: Int) = readUInt(STACK_START + stackPosition)

    fun writeUByteToStack(stackPosition: Int, value: UByte) {
        writeUByte(STACK_START + stackPosition, value)
    }

    fun readUInt16FromStack(stackPosition:Int):UInt {
        return toUInt16(
                readUByte(STACK_START + stackPosition + 0x1),
                readUByte(STACK_START + stackPosition + 0x2)
        )
    }

    fun writeUInt16ToStack(stackPosition:Int, value:UInt) {
        val c1 = value.and(0xffu).toUByte()
        val c2 = value.and(0xff00u).shr(8).toUByte()
        writeUByte(STACK_START + stackPosition-1,c1)
        writeUByte(STACK_START + stackPosition,c2)
    }

    private fun toUInt16(c: UByte, c2: UByte) = c2.toUInt().shl(8).or(c.toUInt())
    private fun toInt16(c: UByte, c2: UByte) = toUInt16(c, c2).toInt()

    fun readUByte(position: Int): UByte {
        val value = store[position]
        if(VERBOSE) println("read Ubyte ${value.toString(16)} from ${position.toString(16)}")
        return value
    }

    private fun writeUByte(position: Int, value: UByte) {
        if(VERBOSE) println("write Ubyte ${value.toString(16)} to ${position.toString(16)}")
        store[position] = value
    }

    fun readUInt(position: Int) = readUByte(position).toUInt()
    private fun readInt(position: Int) = readUByte(position).toInt()

    fun positionUsing(address: Address, state: CpuState): UInt {
        return when (address) {
//            Address.i -> readUInt(state.programCounter + 1)
            Address.z -> readUInt(state.programCounter + 1)
            Address.zx -> (readUInt(state.programCounter + 1) + state.xRegister) % 0x100u
            Address.zy -> (readUInt(state.programCounter + 1) + state.yRegister) % 0x100u
            Address.ab -> readUInt16(state.programCounter + 1)
            Address.abx -> readUInt16(state.programCounter + 1) + state.xRegister
            Address.aby -> readUInt16(state.programCounter + 1) + state.yRegister
            Address.ir -> readUInt16(positionUsing(Address.ab, state).toInt())
            else -> throw NotImplementedError("Address mode ${address.name} not implemented for positionUsing")
        }.also {
            if(VERBOSE) println("position using ${address} for ${state.programCounter} is ${it.toString(16)}")
        }
    }

    fun readUsing(address: Address, state: CpuState): UInt {
        return when (address) {
            Address.i -> readUInt(state.programCounter + 1)
            Address.z,
            Address.zx,
            Address.zy,
            Address.ab,
            Address.abx,
            Address.aby -> readUInt(positionUsing(address, state).toInt())
            else -> throw NotImplementedError("Address mode ${address.name} not implemented for readUsing")
        }
    }


}