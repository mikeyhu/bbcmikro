package net.chompsoftware.bbcmikro.investigation

import net.chompsoftware.bbcmikro.hardware.Logging
import net.chompsoftware.k6502.hardware.Memory
import net.chompsoftware.k6502.toHex


data class MemoryWatch(val name: String, val low: Int, val high: Int)

/**
 * Wraps Memory and logs watched areas of memory for reads and writes
 */
class WatchableMemory(val backingMemory: Memory, val watches: List<MemoryWatch>) : Memory {
    override fun get(position: Int): UByte {
        return backingMemory.get(position).also { value ->
            watches.forEach {
                if (position >= it.low && position <= it.high) {
                    Logging.debug { "${it.name} watched get at ${position.toHex()} value ${value.toHex()}" }
                }
            }
        }
    }

    override fun set(position: Int, value: UByte) {
        backingMemory.set(position, value)
        watches.forEach {
            if (position >= it.low && position <= it.high) {
                Logging.debug { "${it.name} watched set at ${position.toHex()} value ${value.toHex()}" }
            }
        }
    }
}