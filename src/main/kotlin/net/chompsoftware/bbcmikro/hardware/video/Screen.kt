package net.chompsoftware.bbcmikro.hardware.video

import net.chompsoftware.bbcmikro.utils.SpeedReporter
import net.chompsoftware.k6502.hardware.Memory
import java.awt.Graphics
import java.awt.image.BufferedImage
import javax.swing.JPanel

enum class Mode {
    SEVEN
}

const val SCREEN_WIDTH = 1280
const val SCREEN_HEIGHT = 800
const val MODE7_CHARS_PER_LINE = 40
const val MODE7_LINES_PER_SCREEN = 25

class Screen(memory: Memory) {

    private val screenMode7 = ScreenMode7(memory)

    fun render(mode: Mode, graphics: Graphics, panel: JPanel) {
        when (mode) {
            Mode.SEVEN -> screenMode7.render(graphics, panel)
        }
    }
}

class ScreenMode7(val memory: Memory) {
    private val speedReporter = SpeedReporter("ScreenMode7 Repaints")

    private val bufferedImage =
        BufferedImage(TELETEXT_WIDTH * MODE7_CHARS_PER_LINE, TELETEXT_HEIGHT * MODE7_LINES_PER_SCREEN, BufferedImage.TYPE_INT_RGB)

    private val characters = teletextCharacters.map { (k, v) ->
        k.toUByte() to characterAsBufferedImage(v)
    }.toMap()

    fun render(graphics: Graphics, panel: JPanel) {
        speedReporter.increment()
        val bufferedGraphics = bufferedImage.graphics
        renderToBufferedGraphics(bufferedGraphics)
        graphics.drawImage(bufferedImage, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, panel)
    }

    private fun renderToBufferedGraphics(bufferedGraphics: Graphics) {
        (0x7c00 until 0x8000).forEach { index ->
            bufferedGraphics.drawImage(
                characters[memory[index]],
                (index - 0x7bff) % 0x28 * TELETEXT_WIDTH, (index - 0x7bff) / 0x28 * TELETEXT_HEIGHT,
                TELETEXT_WIDTH, TELETEXT_HEIGHT,
                null
            )
        }
    }
}