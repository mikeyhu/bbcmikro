package net.chompsoftware.bbcmikro.hardware

import net.chompsoftware.k6502.hardware.Memory
import net.chompsoftware.k6502.toHex

const val PAGE_OFFSET = 0x8000
const val OS_OFFSET = 0xc000
const val PAGE_SWITCH_LOCATION = 0xfe30
const val OUT_OF_RANGE_OFFSET = 0x10000
const val SHIELA_BLOCK = 0xfe00

@ExperimentalUnsignedTypes
const val NOT_FOUND_PAGE_RETURN: UByte = 0x0u

@ExperimentalUnsignedTypes
class PageableMemory(val ram: UByteArray, val os: UByteArray, val pages: Map<Int, UByteArray>, val systemVia: SystemVia, val userVia: UserVia) : Memory {
    private var currentPage: Int = 0xf

    override operator fun get(position: Int): UByte {
        return when {
            position < PAGE_OFFSET -> ram[position]
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
            position in SYSTEM_VIA_FROM until SYSTEM_VIA_TO -> systemVia[position - SYSTEM_VIA_FROM] = value
            position in USER_VIA_FROM until USER_VIA_TO -> userVia[position - USER_VIA_FROM] = value
            position in OS_OFFSET until OUT_OF_RANGE_OFFSET -> os[position - OS_OFFSET] = value
            position in PAGE_OFFSET until OS_OFFSET -> throw PageableMemoryError("Cannot write to page ${currentPage.toHex()}")
            else -> throw PageableMemoryError("Write out of range: ${position.toHex()} (${value.toHex()})")
        }
    }
}

class PageableMemoryError(message: String) : Error(message)
