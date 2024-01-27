package net.chompsoftware.bbcmikro.hardware

import net.chompsoftware.bbcmikro.Configuration.cpuSystemMultiple

/**
 * In the BBC Model B the CPU runs at 2Mhz and the system runs at 1MHz.
 * All the timers will run at the system speed rather than the CPU speed
 */
abstract class Timer(val name: String) {
    abstract fun systemTick(amountOfTicks: Int): InterruptState

    abstract fun availableSystemTicks(): Int?
}

data class InterruptState(val irq: Boolean, val nmi: Boolean) {
    companion object {
        fun irq() = InterruptState(irq = true, nmi = false)
        fun nmi() = InterruptState(irq = false, nmi = true)
        fun none() = InterruptState(irq = false, nmi = false)
        fun both() = InterruptState(irq = true, nmi = true)
    }

    operator fun plus(other: InterruptState) = InterruptState(
        irq = this.irq || other.irq,
        nmi = this.nmi || other.nmi
    )
}


class TimerManager {
    /**
     * Handles all timing events by interacting with the timers to see how many operations can be done before checking
     * with the timers again.
     *
     * Current timers:
     * SystemVia (2 timers)
     * UserVia (2 timers)
     * FrameTimer
     */
    private val timers: MutableMap<String, Timer> = mutableMapOf()

    private var remainder = 0
    fun writeTimer(timer: Timer) {
        timers[timer.name] = timer
    }

    fun readTimer(name: String): Timer? {
        return timers.get(name)
    }

    fun cpuTick(amountOfTicks: Int): InterruptState {
        val totalTicks = amountOfTicks + remainder
        val ticksToSend = totalTicks / cpuSystemMultiple
        remainder = totalTicks % cpuSystemMultiple
        if (ticksToSend > 0 && timers.isNotEmpty()) {
            return timers.values
                .map { timer -> timer.systemTick(ticksToSend) }
                .reduce(InterruptState::plus)
        }
        return InterruptState.none()
    }

    fun availableCpuTicks(): Int? {
        return timers.values
            .mapNotNull { timer -> timer.availableSystemTicks() }
            .minOrNull()
            ?.times(cpuSystemMultiple)
    }
}