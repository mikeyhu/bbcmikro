package functional

import net.chompsoftware.k6502.hardware.Cpu
import net.chompsoftware.k6502.hardware.CpuState
import net.chompsoftware.k6502.hardware.Memory
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import org.junit.jupiter.api.fail
import java.io.File

@ExperimentalUnsignedTypes
class SuiteTest {

    val FINISH_ON_BREAK = false


    @EnabledIfEnvironmentVariable(named = "SUITE", matches = "true")
    @Test
    fun `Run external suite`() {
        val suiteFile = readFileToByteArray("externalSuite/6502_functional_test.bin")
        val memory = Memory(suiteFile)

        var state = CpuState(programCounter = 0x400)
        val cpu = Cpu()
        var operationsDone = 0
        val start = System.nanoTime()

        do {
            val counter = state.programCounter
            state = cpu.run(state, memory)
            operationsDone++
            if (counter == state.programCounter) {
                println(state)
                fail("hit trap at ${counter.toString(16)}")
            }
        } while (!(state.isBreakCommandFlag && FINISH_ON_BREAK) && counter != state.programCounter)

        val finish = System.nanoTime()

        val elapsed = (finish - start) / 1000000
        println("Operations done: ${operationsDone} Time taken: ${elapsed}ms. Ops per ms: ${operationsDone / elapsed}")
    }

    fun logState(state: CpuState) {
        println("PC:${state.programCounter.toString(16)} state:${state}")
    }

    fun readFileToByteArray(fileName: String) = File(fileName).inputStream().readBytes().asUByteArray()
}