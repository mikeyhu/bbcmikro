package net.chompsoftware.k6502.hardware

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

@ExperimentalUnsignedTypes
class ViaTest {
    @Test
    fun `should be able to set bits on the IER`() {
        val systemVia = SystemVia()

        systemVia[0xe] = 0x83u

        systemVia.readUInt(0xe) shouldBe 0x83u
    }

    @Test
    fun `should be able to unset bits on the IER`() {
        val systemVia = SystemVia()

        systemVia[0xe] = 0x83u

        systemVia[0xe] = 0x01u

        systemVia.readUInt(0xe) shouldBe 0x82u
    }

    @Test
    fun `should set DDRA and DDRB and be able to reread them`() {
        val systemVia = SystemVia()

        systemVia[0x2] = 0xffu
        systemVia.readUInt(0x2) shouldBe 0xffu

        systemVia[0x3] = 0xffu
        systemVia.readUInt(0x3) shouldBe 0xffu
    }

    @Test
    fun `setting DDRA bits to input means that IORA writes will not affect them`() {
        val systemVia = SystemVia()

        systemVia[0x3] = 0xf0u
        //0,1,2,4 are input
        systemVia.readUInt(0x3) shouldBe 0xf0u

        systemVia[0x1] = 0xffu
        systemVia.readUInt(0x1) shouldBe 0x0fu
    }

    @Test
    fun `should be able to write to ORBna`() {
        val systemVia = SystemVia()
        systemVia[0xf] = 1u

        systemVia.readUInt(0xf) shouldBe 1u
    }
}
