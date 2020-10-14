package net.chompsoftware.k6502.hardware

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

    fun writeUInt16ToStack(stackPosition:Int, value:UInt) {
        val c1 = value.and(0xffu).toUByte()
        val c2 = value.and(0xff00u).shr(8).toUByte()
        writeUByte(0x100 + stackPosition-1,c1)
        writeUByte(0x100 + stackPosition,c2)
    }

    fun writeUByte(position: Int, value: UByte) {
        store[position] = value
    }


    private fun toUInt16(c: UByte, c2: UByte) = c2.toUInt().shl(8).or(c.toUInt())
    private fun toInt16(c: UByte, c2: UByte) = toUInt16(c, c2).toInt()

    fun readUByte(position: Int) = store[position]
    fun readUInt(position: Int) = readUByte(position).toUInt()
    fun readInt(position: Int) = readUByte(position).toInt()

    fun positionUsing(address: Address, state: CpuState): UInt {
        return when (address) {
            Address.i -> readUInt(state.programCounter + 1)
            Address.z -> readUInt(readInt(state.programCounter + 1))
            Address.ab -> readUInt16(state.programCounter + 1)
            Address.ir -> readUInt16(positionUsing(Address.ab, state).toInt())
            else -> throw Error("Address mode ${address.name} not implemented")
        }
    }

    fun readUsing(address: Address, state: CpuState): UInt {
        return when (address) {
            Address.i -> readUInt(state.programCounter + 1)
            Address.z -> readUInt(readInt(state.programCounter + 1))
            Address.ab -> readUInt(readUInt16(state.programCounter + 1).toInt())
            else -> throw Error("Address mode ${address.name} not implemented")
        }
    }
}