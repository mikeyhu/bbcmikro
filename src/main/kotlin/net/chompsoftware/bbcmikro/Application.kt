package net.chompsoftware.bbcmikro

import net.chompsoftware.bbcmikro.investigation.LoggingOperation
import net.chompsoftware.bbcmikro.hardware.Microsystem
import net.chompsoftware.bbcmikro.hardware.PageableMemory
import net.chompsoftware.bbcmikro.hardware.SystemVia
import net.chompsoftware.bbcmikro.hardware.UserVia
import net.chompsoftware.bbcmikro.hardware.video.Mode
import java.awt.Color
import java.awt.Graphics
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.event.WindowEvent
import java.awt.event.WindowListener
import java.io.File
import javax.swing.*
import kotlin.system.exitProcess

@ExperimentalUnsignedTypes
class Application(microsystem: Microsystem) : JFrame() {
    init {
        title = "K6502"
        setSize(1024, 800)
        defaultCloseOperation = EXIT_ON_CLOSE

        add(InputOutputSurface(microsystem))

        addWindowListener(object : WindowListener {
            override fun windowOpened(e: WindowEvent?) {}

            override fun windowClosing(e: WindowEvent?) {
                exitProcess(0)
            }

            override fun windowClosed(e: WindowEvent?) {}

            override fun windowIconified(e: WindowEvent?) {}

            override fun windowDeiconified(e: WindowEvent?) {}

            override fun windowActivated(e: WindowEvent?) {}

            override fun windowDeactivated(e: WindowEvent?) {}
        })
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

        this.addKeyListener(object : KeyListener {
            override fun keyTyped(e: KeyEvent?) {}

            override fun keyPressed(e: KeyEvent?) {
                e?.also {
                    microsystem.setKey(it, true)
                    LoggingOperation.logOperations = true
                }
            }

            override fun keyReleased(e: KeyEvent?) {
                e?.also {
                    microsystem.setKey(it, false)
                }
            }
        })
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        microsystem.screen.render(Mode.SEVEN, g, this)
    }
}


