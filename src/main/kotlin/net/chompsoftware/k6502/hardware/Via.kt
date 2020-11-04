package net.chompsoftware.k6502.hardware

import net.chompsoftware.k6502.hardware.ViaAddress.InputOutputRegisterANH
import net.chompsoftware.k6502.hardware.ViaAddress.InterruptEnableRegister

const val SYSTEM_VIA_FROM = 0xfe40
const val SYSTEM_VIA_TO = 0xfe50
const val USER_VIA_FROM = 0xfe60
const val USER_VIA_TO = 0xfe70
@ExperimentalUnsignedTypes
const val BIT_7: UByte = 0x80u

// from http://www.8bs.com/mag/32/bbcmemmap2.txt
// and BBC Microcomputer Advanced User Guide page 399
enum class ViaAddress(val p: Int) {
    InputOutputRegisterB(0x0),
    InputOutputRegisterA(0x1),
    DataDirectionRegisterB(0x2),
    DataDirectionRegisterA(0x3),
    Timer1LatchCounterL(0x4),
    Timer1LatchCounterH(0x5),
    Timer1LatchL(0x6),
    Timer1LatchH(0x7),
    Timer2LatchCounterL(0x8),
    Timer2LatchCounterH(0x9),
    ShiftRegister(0xa),
    AuxiliaryControlRegister(0xb),
    PeripheralControlRegister(0xc),
    InterruptFlagRegister(0xd),
    InterruptEnableRegister(0xe),
    InputOutputRegisterANH(0xf);

    companion object {
        private val Addresses = values().associateBy { it.p }

        fun from(a: Int) = Addresses[a]
    }
}

@ExperimentalUnsignedTypes
abstract class Via(val name: String, val start: Int) {

    val store = UByteArray(0x10)

    operator fun get(position: Int): UByte {
        val viaAddress = ViaAddress.from(position)
        val value = when (viaAddress) {
            InterruptEnableRegister -> store[viaAddress.p] or 0x80u
            InputOutputRegisterANH -> store[viaAddress.p]
            else -> {
                0x0u
            }
        }
        Logging.verbose("${name} read ${(viaAddress)} ${(position + start).toHex()} (${value.toHex()})")
        return value.toUByte()
    }

    fun readUInt(position: Int) = get(position).toUInt()

    operator fun set(position: Int, value: UByte) {
        val viaAddress = ViaAddress.from(position)
        when (viaAddress) {
            InterruptEnableRegister -> store[viaAddress.p] =
                    if (value.and(BIT_7) == BIT_7) store[viaAddress.p] or (value.and(0x7fu))
                    else store[viaAddress.p] and (value.and(0x7fu).inv())
            InputOutputRegisterANH -> store[viaAddress.p] = value
            else -> {
            }
        }
        Logging.verbose("${name} write ${(viaAddress)} ${(position + start).toHex()} (${value.toHex()})")
    }
}

@ExperimentalUnsignedTypes
class SystemVia() : Via("SystemVia", SYSTEM_VIA_FROM) {}

@ExperimentalUnsignedTypes
class UserVia() : Via("UserVia", USER_VIA_FROM) {}