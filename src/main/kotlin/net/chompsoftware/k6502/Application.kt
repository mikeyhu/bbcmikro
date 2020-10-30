package net.chompsoftware.k6502

import net.chompsoftware.k6502.hardware.Microsystem
import net.chompsoftware.k6502.hardware.PageableMemory
import net.chompsoftware.k6502.hardware.video.Mode
import java.awt.Color
import java.awt.EventQueue
import java.awt.Graphics
import java.io.File
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.SwingWorker

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
            EventQueue.invokeLater {

                val pageableMemory = PageableMemory(
                        UByteArray(0x8000),
                        readFileToByteArray("./roms/Os12.rom"),
                        mapOf(
                                0xf to readFileToByteArray("./roms/Basic2.rom")
                        )
                )
                val microsystem = Microsystem(pageableMemory)

                val app = Application(microsystem)
                app.isVisible = true
            }
        }

        fun readFileToByteArray(fileName: String) = File(fileName).inputStream().readBytes().asUByteArray()

    }
}

@ExperimentalUnsignedTypes
class InputOutputSurface(val microsystem: Microsystem) : JPanel() {

    val scale = 4

    init {
        background = Color.BLACK
        runMicrosystem()
    }

    fun runMicrosystem() {
        val worker = MicrosystemWorker()
        worker.execute()
    }

    inner class MicrosystemWorker : SwingWorker<Unit, Unit>() {
        override fun doInBackground() {
            while(true) {
                microsystem.run()
                repaint()
            }
        }
    }

    override fun paintComponent(g: Graphics) {

        super.paintComponent(g)
        microsystem.screen.render(Mode.SEVEN, g, this)
    }
}


