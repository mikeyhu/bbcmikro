package net.chompsoftware.bbcmikro.hardware

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource


class TimerManagerTest {
    @Test
    fun willNotReturnATimerIfItDoesNotExist() {
        val tm = TimerManager()

        val timer = MockTimer("MockTimer")

        tm.writeTimer(timer)

        val foundTimer = tm.readTimer("ADifferentTimer")

        foundTimer shouldBe null
    }

    @Test
    fun canAcceptAndReadATimerBack() {
        val tm = TimerManager()

        val timer = MockTimer("MockTimer")

        tm.writeTimer(timer)

        val foundTimer = tm.readTimer("MockTimer")

        foundTimer shouldBe timer
    }

    @ParameterizedTest
    @CsvSource(
        "0, null",
        "1, null",
        "2, 1",
        "3, 1",
        "4, 2",
        "100, 50",
        nullValues = ["null"]
    )
    fun willSendAppropriateSystemTicksToAllTimers(cpuTicks: Int, ticksReceived: Int?) {
        val tm = TimerManager()

        val timer1 = MockTimer("1")
        val timer2 = MockTimer("2")

        tm.writeTimer(timer1)
        tm.writeTimer(timer2)

        tm.cpuTick(cpuTicks)

        timer1.ticksReceived shouldBe ticksReceived
        timer2.ticksReceived shouldBe ticksReceived
    }

    @Test
    fun willSendSystemTicksWhenRemainderAndAmountIsEnough() {
        val tm = TimerManager()

        val timer1 = MockTimer("1")
        tm.writeTimer(timer1)

        tm.cpuTick(1)
        timer1.ticksReceived shouldBe null

        tm.cpuTick(1)
        timer1.ticksReceived shouldBe 1
    }

    @Test
    fun willDetermineAvailableSystemTicksAsNullIfNoTimersAreAvailable() {
        val tm = TimerManager()

        tm.availableCpuTicks() shouldBe null
    }

    @Test
    fun willDetermineAvailableSystemTicksAsNullIfNoTimersAreRunning() {
        val tm = TimerManager()

        tm.writeTimer(MockTimer("MockTimer"))

        tm.availableCpuTicks() shouldBe null
    }

    @Test
    fun willDetermineAvailableSystemTicksBasedOnTimerTicks() {
        val tm = TimerManager()

        tm.writeTimer(MockTimer("1", 50))

        tm.availableCpuTicks() shouldBe 100
    }

    @Test
    fun willDetermineAvailableSystemTicksFromTimerThatIsRunning() {
        val tm = TimerManager()

        tm.writeTimer(MockTimer("1", 100))
        tm.writeTimer(MockTimer("2", null))

        tm.availableCpuTicks() shouldBe 200
    }

    @Test
    fun willDetermineAvailableSystemTicksFromDoubleLowestTimer() {
        val tm = TimerManager()

        tm.writeTimer(MockTimer("1", 100))
        tm.writeTimer(MockTimer("2", 101))

        tm.availableCpuTicks() shouldBe 200
    }

    @Test
    fun willReturnADefaultInterruptStateIfNoTimersAreAvailable() {
        val tm = TimerManager()

        tm.cpuTick(2) shouldBe InterruptState.none()
    }

    @Test
    fun willReturnAnInteruptStateFromATimer() {
        val tm = TimerManager()
        tm.writeTimer(MockTimer("1", 100, InterruptState.irq()))
        tm.cpuTick(2) shouldBe InterruptState.irq()
    }

    @Test
    fun willMergeInterruptStateFromAllTimers() {
        val tm = TimerManager()
        tm.writeTimer(MockTimer("1", 100, InterruptState.irq()))
        tm.writeTimer(MockTimer("2", 100, InterruptState.nmi()))
        tm.cpuTick(2) shouldBe InterruptState.both()
    }

    class MockTimer(
        name: String,
        private val ticks: Int? = null,
        private val interruptState: InterruptState = InterruptState(false, false)
    ) : Timer(name) {
        var ticksReceived: Int? = null

        override fun systemTick(amountOfTicks: Int): InterruptState {
            ticksReceived = amountOfTicks
            return interruptState
        }

        override fun availableSystemTicks(): Int? {
            return ticks
        }
    }
}