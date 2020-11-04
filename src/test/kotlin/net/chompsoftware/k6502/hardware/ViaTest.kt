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
    fun `should be able to write to ORBna`() {
        val systemVia = SystemVia()
        systemVia[0xf] = 1u

        systemVia.readUInt(0xf) shouldBe 1u
    }
}
