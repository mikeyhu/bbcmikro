package net.chompsoftware.bbcmikro.hardware

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

@ExperimentalUnsignedTypes
class PageableMemoryTest {

    @Test
    fun `Should be able to read a value in the first 0x8000 of RAM`() {
        val memory = PageableMemory(
                setupMemory(InstructionSet.nop.u, 0x05u),
                UByteArray(0x4000),
                emptyMap(),
                SystemVia(), UserVia()
        )
        val state = CpuState()

        memory.positionUsing(Address.ab, state) shouldBe 0x5u
    }

    @Test
    fun `Should be able to write a value in the first 0x8000 of RAM`() {
        val memory = PageableMemory(
                setupMemory(InstructionSet.nop.u),
                UByteArray(0x4000),
                emptyMap(),
                SystemVia(), UserVia()
        )
        val state = CpuState()

        memory.set(0x01, 0xffu)

        memory.positionUsing(Address.ab, state) shouldBe 0xffu
    }

    @Test
    fun `Should be able to read a value in OS segment of RAM`() {
        val memory = PageableMemory(
                setupMemory(),
                setupMemory(InstructionSet.nop.u, 0x05u, size = 0x4000),
                emptyMap(),
                SystemVia(), UserVia()
        )
        val state = CpuState(programCounter = 0xc000)

        memory.positionUsing(Address.ab, state) shouldBe 0x5u
    }

    @Test
    fun `Should be able to write a value in OS segment of RAM`() {
        val memory = PageableMemory(
                setupMemory(),
                setupMemory(0xaau, size = 0x4000),
                emptyMap(),
                SystemVia(), UserVia()
        )
        memory[0xffff] = 0xbbu

        memory[0xc000].toUInt() shouldBe 0xaau
        memory[0xffff].toUInt() shouldBe 0xbbu
    }

    @Test
    fun `Should be able to read a value in Page segment of RAM`() {
        val memory = PageableMemory(
                setupMemory(),
                setupMemory(InstructionSet.nop.u, 0x05u, size = 0x4000),
                mapOf(
                        0xf to setupMemory(0x11u)
                ),
                SystemVia(), UserVia()
        )
        memory[0x8000].toUInt() shouldBe 0x11u
    }

    @Test
    fun `Should NOT be able to write a value in Page segment of RAM`() {
        val memory = PageableMemory(
                setupMemory(),
                setupMemory(InstructionSet.nop.u, 0x05u, size = 0x4000),
                mapOf(
                        0xf to setupMemory(0x11u)
                ),
                SystemVia(), UserVia()
        )

        val exception = shouldThrow<PageableMemoryError> {
            memory.set(0x8000, 0xbbu)
        }

        exception.message shouldBe "Cannot write to page 0xf"
    }

    @Test
    fun `Should be able to switch to a different Page`() {
        val memory = PageableMemory(
                setupMemory(),
                setupMemory(InstructionSet.nop.u, 0x05u, size = 0x4000),
                mapOf(
                        0xf to setupMemory(0xf1u),
                        0x1 to setupMemory(0xf2u)
                ),
                SystemVia(), UserVia()
        )
        memory[0x8000].toUInt() shouldBe 0xf1u

        memory[0xfe30] = 0x1u

        memory[0x8000].toUInt() shouldBe 0xf2u
    }

    @Test
    fun `Switching to an unavailable page should not fail`() {
        val memory = PageableMemory(
                setupMemory(),
                setupMemory(InstructionSet.nop.u, 0x05u, size = 0x4000),
                mapOf(
                        0xf to setupMemory(0xf1u)
                ),
                SystemVia(), UserVia()
        )
        memory[0x8000].toUInt() shouldBe 0xf1u
        memory[0xfe30] = 0x1u
        memory[0x8000].toUInt() shouldBe 0x00u
    }

    @Test
    fun `Reading out of range should fail`() {
        val memory = PageableMemory(
                setupMemory(),
                setupMemory(InstructionSet.nop.u, 0x05u, size = 0x4000),
                mapOf(),
                SystemVia(), UserVia()
        )
        val exception = shouldThrow<PageableMemoryError> {
            memory.get(0x10000)
        }

        exception.message shouldBe "Read out of range: 0x10000"
    }

    @Test
    fun `Write out of range should fail`() {
        val memory = PageableMemory(
                setupMemory(),
                setupMemory(InstructionSet.nop.u, 0x05u, size = 0x4000),
                mapOf(),
                SystemVia(), UserVia()
        )
        val exception = shouldThrow<PageableMemoryError> {
            memory.set(0x10000, 0x1u)
        }

        exception.message shouldBe "Write out of range: 0x10000 (0x1)"
    }

    @Test
    fun `Reading in range should succeed`() {
        val memory = PageableMemory(
                setupMemory(),
                setupMemory(size = 0x4000),
                mapOf(),
                SystemVia(), UserVia()
        )
        (0 until 0x10000).forEach { index ->
            memory[index].toUInt() shouldBeGreaterThanOrEqualTo 0u
        }
    }

}