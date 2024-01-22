package net.chompsoftware.bbcmikro.hardware


class Timer(val name: String, isIRQ: Boolean, isNMI: Boolean, startedAction: () -> Unit, elapsedAction: () -> Unit)

class TimerManager {

    private val timers: MutableMap<String, Timer> = mutableMapOf()
    fun writeTimer(timer: Timer) {
        timers[timer.name] = timer
    }

    fun readTimer(name: String): Timer? {
        return timers.get(name)
    }

    fun tick() {

    }
}