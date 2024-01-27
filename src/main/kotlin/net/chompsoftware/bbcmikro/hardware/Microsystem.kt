package net.chompsoftware.bbcmikro.hardware

import net.chompsoftware.bbcmikro.hardware.video.Screen
import net.chompsoftware.bbcmikro.investigation.LoggingOperation
import net.chompsoftware.bbcmikro.investigation.MemoryWatch
import net.chompsoftware.bbcmikro.investigation.WatchableMemory
import net.chompsoftware.k6502.hardware.Memory
import java.awt.event.KeyEvent

class Microsystem(val memory: Memory, val systemVia: SystemVia, val userVia: UserVia, val timerManager: TimerManager) {

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

    val watchedMemory = WatchableMemory(
        memory, listOf(
            MemoryWatch("IRQ", 0xFFFE, 0xFFFF),
            MemoryWatch("NMI", 0xFFFA, 0xFFFB),
            MemoryWatch("DC1C", 0xDC1C, 0xDC1D),
            MemoryWatch("OSVECTOR", 0x200, 0x235),
            MemoryWatch("IRQ1V", 0x204, 0x205),
            MemoryWatch("IRQ2V", 0x206, 0x207),
            MemoryWatch("SHEILA", 0xFE00, 0xFEFF)
        )
    )

    val screen = Screen(watchedMemory)

    fun run(repaint: () -> Unit) {
        val frameTimer = FrameTimer(repaint)
        timerManager.writeTimer(frameTimer)
        val processor = Processor(LoggingOperation, watchedMemory, timerManager)
        processor.start(this::callbackInterceptor)
    }

    fun setKey(key: KeyEvent, downPress: Boolean) {
        // TODO: record the key event and direction somewhere so it can be queried later
        keyPressedInterrupt = true
    }

    private fun callbackInterceptor(): InterruptState {
        var state = InterruptState.none()
        if (keyPressedInterrupt) {
            Logging.info { "setting interrupt for keyevent" }
            systemVia.enableInterruptExternally()
            keyPressedInterrupt = false
            state += InterruptState.irq()
        }

        return state
    }
}