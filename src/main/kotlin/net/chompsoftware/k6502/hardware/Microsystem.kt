package net.chompsoftware.k6502.hardware

const val BBC_6502_CYCLE_SPEED = 2000000L

const val INTERRUPT = 10 //ms
const val INTERRUPT_CYCLES= BBC_6502_CYCLE_SPEED / 1000 * INTERRUPT

const val NANO_SECOND = 1000 * 1000000

@ExperimentalUnsignedTypes
class Microsystem(val memory: Memory) {

    val cpu = Cpu()

    var cpuState = CpuState(
            programCounter = 0x400,
            breakLocation = 0xfffe
    )

    var interrupted = false



    fun run() {

        var nextInterrupt = INTERRUPT_CYCLES
        var nextSecond = System.nanoTime() + NANO_SECOND

        var interruptCount = 0
        var secondCount = 0


        while(!interrupted) {
            cpuState = cpu.run(cpuState, memory)

            if(nextInterrupt > cpuState.cycleCount) {
                interruptCount+=1
                nextInterrupt+= INTERRUPT_CYCLES
                if(System.nanoTime() > nextSecond) {
                    secondCount+=1
                    nextSecond+=NANO_SECOND
                    println("Second passed. Interrupts: ${interruptCount / secondCount} ${cpuState.cycleCount}")
                }
            }


        }
    }
}