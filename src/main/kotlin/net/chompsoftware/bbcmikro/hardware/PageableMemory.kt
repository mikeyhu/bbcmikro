package net.chompsoftware.bbcmikro.hardware

import net.chompsoftware.k6502.hardware.Memory
import net.chompsoftware.k6502.toHex

const val PAGE_OFFSET = 0x8000
const val OS_OFFSET = 0xc000
const val PAGE_SWITCH_LOCATION = 0xfe30
const val OUT_OF_RANGE_OFFSET = 0x10000

// Other Memory Mapped ranges:

// Fred - external devices such as disc drives
const val FRED_FROM = 0xfc00
const val FRED_TO = 0xfcff

// Jim - access to optional extra 64K of RAM
const val JIM_FROM = 0xfd00
const val JIM_TO = 0xfdff

// Sheila - (0xfe00 -> 0xfeff) internal hardware - this range is generally covered by the VIAs etc.
const val ACIA_6850_CONTROL_REGISTER = 0xfe08
const val ACIA_6850_STATUS_REGISTER = 0xfe08 // same as above but when used for reading
const val ACIA_6850_DATA_REGISTER = 0xfe09

const val SERIAL_ULA_CONTROL_REGISTER = 0xfe10
const val VIDE0_ULA_CONTROL_REGISTER = 0xfe20
const val VIDE0_ULA_PALETTE_REGISTER = 0xfe21

// Analog to digital conversion
const val ADC_7002_FROM = 0xfec0
const val ADC_7002_TO = 0xfec2

//The Tube
const val TUBE_ULA_STATUS_REGISTER = 0xfee0
const val TUBE_ULA_DATA_REGISTER3 = 0xfee5


@ExperimentalUnsignedTypes
const val NOT_FOUND_PAGE_RETURN: UByte = 0x0u

class PageableMemory(
    val ram: UByteArray,
    val os: UByteArray,
    val pages: Map<Int, UByteArray>,
    val systemVia: SystemVia,
    val userVia: UserVia,
    val failOnIgnored: Boolean = false
) : Memory {

    private var currentPage: Int = 0xf

    override operator fun get(position: Int): UByte {
        return when {
            position < PAGE_OFFSET -> ram[position]
            position in FRED_FROM until FRED_TO -> ignoredRead("FRED", position)
            position in JIM_FROM until JIM_TO -> ignoredRead("JIM", position)
            position == ACIA_6850_STATUS_REGISTER -> ignoredRead("ACIA_6850_STATUS_REGISTER", position)
            position == ACIA_6850_DATA_REGISTER -> ignoredRead("ACIA_6850_DATA_REGISTER", position)
            position == SERIAL_ULA_CONTROL_REGISTER -> ignoredRead("SERIAL_ULA_CONTROL_REGISTER", position)
            position == VIDE0_ULA_CONTROL_REGISTER -> ignoredRead("VIDE0_ULA_CONTROL_REGISTER", position)
            position == VIDE0_ULA_PALETTE_REGISTER -> ignoredRead("VIDE0_ULA_PALETTE_REGISTER", position)
            position == TUBE_ULA_STATUS_REGISTER -> ignoredRead("TUBE_ULA_STATUS_REGISTER", position)
            position == TUBE_ULA_DATA_REGISTER3 -> ignoredRead("TUBE_ULA_DATA_REGISTER3", position)
            position in ADC_7002_FROM until ADC_7002_TO -> ignoredRead("ADC_7002", position)
            position in SYSTEM_VIA_FROM until SYSTEM_VIA_TO -> systemVia[position - SYSTEM_VIA_FROM]
            position in USER_VIA_FROM until USER_VIA_TO -> userVia[position - USER_VIA_FROM]
            position in OS_OFFSET until OUT_OF_RANGE_OFFSET -> os[position - OS_OFFSET]
            position in PAGE_OFFSET until OS_OFFSET -> pages[currentPage]?.get(position - PAGE_OFFSET)
                ?: NOT_FOUND_PAGE_RETURN

            else -> throw PageableMemoryError("Read out of range: ${position.toHex()}")
        }
    }

    override operator fun set(position: Int, value: UByte) {
        when {
            position < PAGE_OFFSET -> ram[position] = value
            position == PAGE_SWITCH_LOCATION -> currentPage = value.toInt()
            position in FRED_FROM until FRED_TO -> ignoredWrite("FRED", position, value)
            position in JIM_FROM until JIM_TO -> ignoredWrite("JIM", position, value)
            position == ACIA_6850_CONTROL_REGISTER -> ignoredWrite("ACIA_6850_CONTROL_REGISTER", position, value)
            position == ACIA_6850_DATA_REGISTER -> ignoredWrite("ACIA_6850_DATA_REGISTER", position, value)
            position == SERIAL_ULA_CONTROL_REGISTER -> ignoredWrite("SERIAL_ULA_CONTROL_REGISTER", position, value)
            position == VIDE0_ULA_CONTROL_REGISTER -> ignoredWrite("VIDE0_ULA_CONTROL_REGISTER", position, value)
            position == VIDE0_ULA_PALETTE_REGISTER -> ignoredWrite("VIDE0_ULA_PALETTE_REGISTER", position, value)
            position == TUBE_ULA_STATUS_REGISTER -> ignoredWrite("TUBE_ULA_STATUS_REGISTER", position, value)
            position == TUBE_ULA_DATA_REGISTER3 -> ignoredWrite("TUBE_ULA_DATA_REGISTER3", position, value)
            position in ADC_7002_FROM until ADC_7002_TO -> ignoredWrite("ADC_7002", position, value)
            position in SYSTEM_VIA_FROM until SYSTEM_VIA_TO -> systemVia[position - SYSTEM_VIA_FROM] = value
            position in USER_VIA_FROM until USER_VIA_TO -> userVia[position - USER_VIA_FROM] = value
            position in OS_OFFSET until OUT_OF_RANGE_OFFSET -> os[position - OS_OFFSET] = value
            position in PAGE_OFFSET until OS_OFFSET -> throw PageableMemoryError("Cannot write to page ${currentPage.toHex()}")
            else -> throw PageableMemoryError("Write out of range: ${position.toHex()} (${value.toHex()})")
        }
    }

    private fun ignoredWrite(name: String, position: Int, value: UByte) {
        Logging.warn { "$name ignored hardware write ${position.toHex()} (${value.toHex()})" }
        if (failOnIgnored) {
            throw PageableMemoryError("Failing on ignored: $name")
        }
    }

    private fun ignoredRead(name: String, position: Int, value: UByte = 0u): UByte {
        Logging.warn { "$name ignored hardware read ${position.toHex()} (${value.toHex()})" }
        if (failOnIgnored) {
            throw PageableMemoryError("Failing on ignored: $name")
        }
        return value
    }
}

class PageableMemoryError(message: String) : Error(message)
