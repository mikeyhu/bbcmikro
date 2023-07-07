package net.chompsoftware.bbcmikro.hardware.video

import net.chompsoftware.bbcmikro.utils.Timer
import net.chompsoftware.k6502.hardware.Memory
import java.awt.Graphics
import javax.swing.JPanel

enum class Mode {
    SEVEN
}

const val SCREEN_WIDTH = 1280
const val SCREEN_HEIGHT = 800
const val MODE7_CHAR_HEIGHT = SCREEN_HEIGHT / 25
const val MODE7_CHAR_WIDTH = SCREEN_WIDTH / 40

@ExperimentalUnsignedTypes
class Screen(memory: Memory) {

    private val screenMode7 = ScreenMode7(memory)

    fun render(mode: Mode, graphics: Graphics, panel: JPanel) {
        when (mode) {
            Mode.SEVEN -> screenMode7.render(graphics, panel)
        }
    }
}

@ExperimentalUnsignedTypes
class ScreenMode7(val memory: Memory) {
    private val timer = Timer("ScreenMode7 Repaints")

    private val characters = teletextCharacters.map { (k, v) ->
        k.toUByte() to characterAsBufferedImage(v)
    }.toMap()

    fun render(graphics: Graphics, panel: JPanel) {
        timer.increment()
        (0x7c00 until 0x8000).forEach { index ->
            graphics.drawImage(
                    characters[memory[index]],
                    (index - 0x7bff) % 0x28 * MODE7_CHAR_WIDTH, (index - 0x7bff) / 0x28 * MODE7_CHAR_HEIGHT,
                    MODE7_CHAR_WIDTH, MODE7_CHAR_HEIGHT,
                    panel
            )
        }
    }
}