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

    @EnabledIfEnvironmentVariable(named = "SUITE", matches="true")
    @Test
    fun `Run external suite`() {
        val suiteFile = readFileToByteArray("externalSuite/6502_functional_test.bin")
        val memory = Memory(suiteFile)

        var state = CpuState(programCounter = 0x400)
        val cpu = Cpu()

        do {
            val counter = state.programCounter
            state = cpu.run(state, memory)
            if(counter == state.programCounter) {
                println(state)
                fail("hit trap at ${counter.toString(16)}")
            }
        } while (!state.isBreakCommandFlag && counter != state.programCounter)

    }

    fun readFileToByteArray(fileName: String) = File(fileName).inputStream().readBytes().asUByteArray()
}