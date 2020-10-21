package net.chompsoftware.k6502.hardware

@ExperimentalUnsignedTypes
fun UByte.toHex() = "0x" + this.toString(16)

@ExperimentalUnsignedTypes
fun UInt.toHex() = "0x" + this.toString(16)
fun Int.toHex() = "0x" + this.toString(16)
