package net.chompsoftware.bbcmikro

import net.chompsoftware.bbcmikro.hardware.Microsystem
import net.chompsoftware.bbcmikro.hardware.PageableMemory
import net.chompsoftware.bbcmikro.hardware.SystemVia
import net.chompsoftware.bbcmikro.hardware.UserVia
import net.chompsoftware.bbcmikro.hardware.video.Mode
import java.awt.Color
import java.awt.Graphics
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import java.io.File
import javax.swing.*

@ExperimentalUnsignedTypes
class Application(microsystem: Microsystem) : JFrame() {
    init {
        title = "K6502"
        setSize(1024, 800)
        defaultCloseOperation = EXIT_ON_CLOSE

        add(InputOutputSurface(microsystem))
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val pageableMemory = PageableMemory(
                UByteArray(0x8000),
                readFileToByteArray("./roms/Os12.rom"),
                mapOf(
                    0xf to readFileToByteArray("./roms/Basic2.rom")
                ),
                systemVia = SystemVia(),
                userVia = UserVia()
            )
            val microsystem = Microsystem(pageableMemory)

            val app = Application(microsystem)
            app.isVisible = true
            microsystem.run(app::repaint)
        }


        fun readFileToByteArray(fileName: String) = File(fileName).inputStream().readBytes().asUByteArray()
    }
}


const val PAUSE = "PAUSE"

@ExperimentalUnsignedTypes
class InputOutputSurface(val microsystem: Microsystem) : JPanel() {
    var paused = false

    init {
        background = Color.BLACK

        val pause = object : AbstractAction(PAUSE) {
            override fun actionPerformed(e: ActionEvent) {
                paused = true

                println("paused")
            }
        }

        this.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0), PAUSE)
        this.getActionMap().put(PAUSE, pause)
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        microsystem.screen.render(Mode.SEVEN, g, this)
    }
}


