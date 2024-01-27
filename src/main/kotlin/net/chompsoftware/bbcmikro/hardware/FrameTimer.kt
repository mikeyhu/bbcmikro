package net.chompsoftware.bbcmikro.hardware

import net.chompsoftware.bbcmikro.Configuration
import kotlin.math.max


class FrameTimer(val repaint: () -> Unit) : Timer("FrameTimer") {
    private val instructionsPerFrame = Configuration.systemCycleSpeed / Configuration.maxFramesPerSecond
    private var currentFrameCountdown = instructionsPerFrame
    private val frameDelayCalculator = FrameDelayCalculator()
    private var previousFrameSmoothing = 0L

    override fun systemTick(amountOfTicks: Int): InterruptState {
        currentFrameCountdown -= amountOfTicks
        if (currentFrameCountdown <= 0) {
            val beforeRepaint = System.nanoTime()
            if (currentFrameCountdown < 0) {
                Logging.error { "Frame render delayed by $currentFrameCountdown system cycles" }
            }
            Logging.debug { "Frame render here" }
            repaint()
            val afterRepaint = System.nanoTime()
            currentFrameCountdown += instructionsPerFrame
            val waitMilliseconds =
                frameDelayCalculator.calculateNextDelayNano(Configuration.maxFramesPerSecond)?.div(1000000)
            waitMilliseconds?.also {
                if (it > 0) {
                    var actual = it
                    if (previousFrameSmoothing < it) {
                        //spikes in the delay. Lets only do 50%
                        actual = it / 2
                    }
                    Thread.sleep(actual)
                    Logging.debug { "slept for $actual milliseconds. Asked for ($it)" }
                }
                previousFrameSmoothing = it
            }
            frameDelayCalculator.add(beforeRepaint, afterRepaint, System.nanoTime())
        }
        return InterruptState.none()
    }

    override fun availableSystemTicks() = currentFrameCountdown
}

data class FrameInformation(val timeBeforeRedraw: Long, val timeAfterRedraw: Long, val timeAfterWait: Long)

class FrameDelayCalculator() {
    private val maxCapacity = 6 // first entry is never used in time calculations as we just use it's end time
    val arrayDequeue = ArrayDeque<FrameInformation>(maxCapacity)

    fun add(timeBeforeRedraw: Long, timeAfterRedraw: Long, timeAfterWait: Long) {
        add(FrameInformation(timeBeforeRedraw, timeAfterRedraw, timeAfterWait))
    }

    fun add(frameInformation: FrameInformation) {
        arrayDequeue.addLast(frameInformation)
        if (arrayDequeue.size > maxCapacity) {
            arrayDequeue.removeFirst()
        }
    }

    fun calculateNextDelayNano(targetFramesPerSecond: Int): Long? {
        if (arrayDequeue.size < 2) {
            return null
        }
        val targetNano: Long = 1000000000L / targetFramesPerSecond * (maxCapacity - 1)
        val startOfTimeperiodNano = arrayDequeue.first().timeAfterRedraw
        val afterRenderNano = System.nanoTime()
        val nanoTaken = afterRenderNano - startOfTimeperiodNano
        val delayBy = max(targetNano - nanoTaken, 0L)
        Logging.debug { "Frame delay: $delayBy ($targetNano - $nanoTaken)" }
        return delayBy
    }
}
