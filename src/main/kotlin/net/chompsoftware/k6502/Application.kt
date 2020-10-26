package net.chompsoftware.k6502

import net.chompsoftware.k6502.hardware.video.TELETEXT_HEIGHT
import net.chompsoftware.k6502.hardware.video.TELETEXT_WIDTH
import net.chompsoftware.k6502.hardware.video.characterAsBufferedImage
import net.chompsoftware.k6502.hardware.video.teletextCharacters
import java.awt.Color
import java.awt.EventQueue
import java.awt.Graphics
import javax.swing.JFrame
import javax.swing.JPanel

class Application() : JFrame() {
    init {
        title = "K6502"
        setSize(1024, 1024)
        defaultCloseOperation = EXIT_ON_CLOSE

        add(InputOutputSurface())
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            EventQueue.invokeLater {
                val app = Application()
                app.isVisible = true
            }
        }
    }
}

class InputOutputSurface() : JPanel() {

    val scale = 4

    init {
        background = Color.BLACK
        repaint()
    }


    override fun paintComponent(g: Graphics){

        super.paintComponent(g)

        val charWidth = TELETEXT_WIDTH * scale
        val charHeight = TELETEXT_HEIGHT * scale

        teletextCharacters.keys.forEachIndexed { index, key ->
            val img = teletextCharacters[key]?.let {
                characterAsBufferedImage(it)
            }

            if(img!=null) {
                g.drawImage(img, index % 10 * charWidth, index / 10 * charHeight, charWidth, charHeight, this)
            }
        }
    }


}

