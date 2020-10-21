package functional

import net.chompsoftware.k6502.hardware.Cpu
import net.chompsoftware.k6502.hardware.CpuState
import net.chompsoftware.k6502.hardware.InstructionSet.*
import net.chompsoftware.k6502.hardware.Memory
import net.chompsoftware.k6502.hardware.toHex
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import org.junit.jupiter.api.fail

@ExperimentalUnsignedTypes
class TimingTest {

    @EnabledIfEnvironmentVariable(named = "TIMING", matches = "true")
    @Test
    fun `Run timing`() {

        val cyclesToRun = 1000000000

        val program = ubyteArrayOf(
                lda_z.u, 0x00u,
                tax.u,
                inx.u,
                txa.u,
                sta_z.u, 0x00u,
                cmp_i.u, 0xffu,
                bne.u, 0xf5u,
                lda_i.u, 0x00u,
                sta_z.u, 0x00u,
                beq.u, 0xefu
        )

        val memory = Memory(setupProgram(program))

        var state = CpuState(
                programCounter = 0x400,
                breakLocation = 0xfffe)
        val cpu = Cpu()
        var operationsDone = 0L
        val start = System.nanoTime()

        do {
            val counter = state.programCounter
            try {
                state = cpu.run(state, memory)
            } catch (error: Error) {
                fail("failed at ${state.programCounter.toHex()} with $error")
            }
            operationsDone++
            if (counter == state.programCounter) {
                fail("hit trap at ${counter.toHex()}")
            }
        } while (!(state.isBreakCommandFlag) && state.cycleCount < cyclesToRun)

        val finish = System.nanoTime()

        val elapsed = (finish - start) / 1000000
        println("Operations done: ${operationsDone} Time taken: ${elapsed}ms. Ops per ms: ${operationsDone / elapsed}. Ops per s: ${operationsDone / elapsed * 1000}")
        println("Cycles done: ${state.cycleCount} Time taken: ${elapsed}ms. Cycles per ms: ${state.cycleCount / elapsed}. Cycles per s: ${state.cycleCount / elapsed * 1000}")
    }

    private fun setupProgram(program: UByteArray): UByteArray {
        val array = UByteArray(0x8000)
        program.forEachIndexed { i, ub ->
            array[0x400 + i] = ub
        }
        return array
    }
}