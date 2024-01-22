package net.chompsoftware.bbcmikro.hardware

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test


class TimerTest {
    @Test
    fun willNotReturnATimerIfItDoesNotExist() {
        val tm = TimerManager()

        val timer = Timer("SystemVIATimer1", true, false, {}, {})

        tm.writeTimer(timer)

        val foundTimer = tm.readTimer("ADifferentTimer")

        foundTimer shouldBe null
    }

    @Test
    fun canAcceptAndReadATimerBack() {
        val tm = TimerManager()

        val timer = Timer("SystemVIATimer1", true, false, {}, {})

        tm.writeTimer(timer)

        val foundTimer = tm.readTimer("SystemVIATimer1")

        foundTimer shouldBe timer
    }
}