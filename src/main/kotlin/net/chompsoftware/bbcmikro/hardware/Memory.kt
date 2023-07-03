package net.chompsoftware.bbcmikro.hardware

@ExperimentalUnsignedTypes
class Memory(val store: UByteArray) : RamInterface {
    override operator fun get(position: Int) = store[position]
    override operator fun set(position: Int, value: UByte) {
        store[position] = value
    }
}