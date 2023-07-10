package net.chompsoftware.bbcmikro.investigation

import net.chompsoftware.bbcmikro.hardware.Logging
import net.chompsoftware.k6502.hardware.CpuState
import net.chompsoftware.k6502.hardware.EffectPipeline
import net.chompsoftware.k6502.hardware.Memory
import net.chompsoftware.k6502.hardware.Operation
import net.chompsoftware.k6502.hardware.OperationState
import net.chompsoftware.k6502.hardware.instructions.BRK
import net.chompsoftware.k6502.hardware.instructions.Instruction
import net.chompsoftware.k6502.hardware.instructions.RTI
import net.chompsoftware.k6502.toHex

/**
 * Alternate to Operation that logs various things
 */
object LoggingOperation : EffectPipeline {
    var previousOp: UByte? = null
    var breakAlreadyLogged = false

    var logOperations : Boolean = false

    override fun run(cpuState: CpuState, memory: Memory, operationState: OperationState): EffectPipeline {
        val op = memory.get(cpuState.programCounter)
        if(logOperations) {
            Logging.debug { "pc:${cpuState.programCounter.toHex()} op:${Instruction.name(op)}" }
        }
        if(cpuState.isBreakCommandFlag && !breakAlreadyLogged) {
            Logging.error { "isBreak set to True. Previous Op:${Instruction.name(previousOp!!)}" }
            breakAlreadyLogged = true
        }
        if(!cpuState.isBreakCommandFlag && breakAlreadyLogged) {
            Logging.error { "isBreak is cleared" }
            breakAlreadyLogged = false
        }
        when(op) {
            RTI -> Logging.debug { "RTI!" }
            BRK -> Logging.error { "BRK!" }
        }
        previousOp = op
        return Operation.run(cpuState, memory, operationState)
    }
}