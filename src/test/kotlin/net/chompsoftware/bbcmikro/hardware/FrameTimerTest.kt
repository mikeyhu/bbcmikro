package net.chompsoftware.bbcmikro.hardware

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test


class FrameTimerTest {
    @Nested
    inner class FrameDelayCalculatorTest {
        private val ONE_MILLION = 1000000L

        @Test
        fun canRecordInformationButWillNotCalculateInformationUntilTwoTimesAreRecorded() {
            val frameDelayCalculator = FrameDelayCalculator()

            frameDelayCalculator.add(1000 * ONE_MILLION, 2000 * ONE_MILLION, 6000 * ONE_MILLION)

            frameDelayCalculator.calculateNextDelayNano(10) shouldBe null
        }

        @Test
        fun canCalculateNextDelayWhenSpeedIsTooSlow() {
            val frameDelayCalculator = FrameDelayCalculator()

            frameDelayCalculator.add(1000 * ONE_MILLION, 2000 * ONE_MILLION, 2000 * ONE_MILLION)
            frameDelayCalculator.add(3000 * ONE_MILLION, 4000 * ONE_MILLION, 4000 * ONE_MILLION)

            // A frame takes 2seconds (4000millionNano - 2000millionNano)
            // No wait should happen as we're already behind

            frameDelayCalculator.calculateNextDelayNano(10) shouldBe 0
        }

        @Test
        @Disabled
        fun canCalculateNextDelayWhenSpeedIsFastEnough() {
            val frameDelayCalculator = FrameDelayCalculator()

            frameDelayCalculator.add(1 * ONE_MILLION, 2 * ONE_MILLION, 60 * ONE_MILLION)
            frameDelayCalculator.add(61 * ONE_MILLION, 62 * ONE_MILLION, 120 * ONE_MILLION)

            // A frame takes 2milliseconds but including the wait for a previous frame we've taken

            frameDelayCalculator.calculateNextDelayNano(10) shouldBe 0
        }

    }
}