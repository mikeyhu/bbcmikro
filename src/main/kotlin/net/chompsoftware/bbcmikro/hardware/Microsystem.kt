package net.chompsoftware.bbcmikro.hardware

import net.chompsoftware.bbcmikro.Configuration
import net.chompsoftware.bbcmikro.hardware.video.Screen
import net.chompsoftware.bbcmikro.investigation.LoggingOperation
import net.chompsoftware.bbcmikro.investigation.MemoryWatch
import net.chompsoftware.bbcmikro.investigation.WatchableMemory
import net.chompsoftware.k6502.hardware.Memory
import java.awt.event.KeyEvent
import kotlin.math.max

class Microsystem(val memory: Memory) {

    private var limitSpeed = true
    private var limitToFPS = 60
    private val millisecondsPerFrame = 1000 / limitToFPS
    private val nanosecondsPerFrame = millisecondsPerFrame * 1000000
    private val instructionsPerFrame = Configuration.cycleSpeed / limitToFPS
    private var tick: Long = 0
    private var vsync: Long = 0
    private var frameStartNano = System.currentTimeMillis()

    private var keyPressedInterrupt: Boolean = false

    init {
        (0 until 0xf).forEach { index ->
            memory[0xFE40 + index] = 0x00u
            memory[0xFE60 + index] = 0x00u
        }

        memory[0xFE40] = 0xFFu
        memory[0xFE43] = 0xFFu
        memory[0xFE60] = 0xFFu
        memory[0xFE63] = 0xFFu
    }

    val watchedMemory = WatchableMemory(memory, listOf(
        MemoryWatch("IRQ", 0xFFFE, 0xFFFF),
        MemoryWatch("NMI", 0xFFFA, 0xFFFB),
        MemoryWatch("DC1C", 0xDC1C, 0xDC1D),
        MemoryWatch("OSVECTOR", 0x200, 0x235),
        MemoryWatch("IRQ1V", 0x204, 0x205),
        MemoryWatch("IRQ2V", 0x206, 0x207),
        MemoryWatch("SHEILA", 0xFE00, 0xFEFF)
    ))

    val screen = Screen(watchedMemory)

    var processor = Processor(LoggingOperation, watchedMemory)

    fun run(repaint: () -> Unit) {
        processor.start(callbackInterceptor(repaint))
    }

    fun setKey(key: KeyEvent, downPress:Boolean) {
        keyPressedInterrupt = true
    }

    private fun callbackInterceptor(onNMICallback: () -> Unit): () -> Boolean = {
        var interrupt = false
        if (limitSpeed) {
            tick++
            if (tick > instructionsPerFrame) {
                tick = 0
                vsync++

                onNMICallback()
                val currentNano = System.nanoTime()
                val sleepFor = max(nanosecondsPerFrame - (currentNano - frameStartNano), 0)
//                Logging.warn { "sleepFor ${sleepFor}ns. millisecondsPerFrame: $millisecondsPerFrame" }
                
                Thread.sleep(sleepFor / 1000000, (sleepFor % 1000000).toInt())
                if(vsync > limitToFPS) {
                    vsync = 0
                    interrupt = true
                }
                if(keyPressedInterrupt) {
                    Logging.debug { "setting interrupt for keyevent" }
                    interrupt = true
                    keyPressedInterrupt = false
                }
                frameStartNano = System.nanoTime()
            }
        }

        interrupt
    }
}