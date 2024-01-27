package net.chompsoftware.bbcmikro

const val BBC_6502_CYCLE_SPEED = 2000000
const val BBC_SYSTEM_CYCLE_SPEED = 1000000

object Configuration {
    val systemCycleSpeed = BBC_SYSTEM_CYCLE_SPEED
    val maxFramesPerSecond = 60
    val cpuSystemMultiple = BBC_6502_CYCLE_SPEED / BBC_SYSTEM_CYCLE_SPEED
}

