package net.chompsoftware.k6502.hardware

@ExperimentalUnsignedTypes
data class CpuState(
        val programCounter: Int = 0x00,
        val breakLocation: Int = 0x00,
        val aRegister: UInt = 0x0u,
        val xRegister: UInt = 0x0u,
        val yRegister: UInt = 0x0u,
        val stackPointer: Int = 0xff,
        val isBreakCommandFlag: Boolean = false,
        val isNegativeFlag: Boolean = false,
        val isZeroFlag: Boolean = false,
        val isDecimalFlag: Boolean = false,
        val isCarryFlag: Boolean = false
) {
    fun copyWithA(value: UInt, programCounter: Int) = this.copy(
            programCounter = programCounter,
            aRegister = value,
            isNegativeFlag = tweakNegative(value),
            isZeroFlag = tweakZero(value)
    )

    fun copyWithX(value: UInt, programCounter: Int) = this.copy(
            programCounter = programCounter,
            xRegister = value,
            isNegativeFlag = tweakNegative(value),
            isZeroFlag = tweakZero(value)

    )

    fun copyWithY(value: UInt, programCounter: Int) = this.copy(
            programCounter = programCounter,
            yRegister = value,
            isNegativeFlag = tweakNegative(value),
            isZeroFlag = tweakZero(value)
    )

    fun incrementCounterBy(value: Int) = this.copy(programCounter = programCounter + value)

    private fun tweakNegative(value: UInt) = value.shr(7) != 0u
    private fun tweakZero(value: UInt) = value == 0u
}

@ExperimentalUnsignedTypes
class Cpu {
    fun run(state: CpuState, memory: Memory): CpuState {

        val instructionByte = memory[state.programCounter]
        val instruction = InstructionSet.from(instructionByte)

        return when (instruction) {
            null -> throw Error("Undefined instruction ${instructionByte.toString(16)} at PC ${state.programCounter.toString(16)}")
            else -> instruction.run(state, memory)
        }
    }
}

enum class Address(val size: Int) {
    none(1),
    i(2),
    irix(10001),
    ixir(10002),
    ab(3),
    abx(10003),
    aby(10004),
    z(2),
    zx(10005)
}

@ExperimentalUnsignedTypes
enum class InstructionSet(val u: UByte, val ad: Address, val op: Operation) {
    adc_i(0x69u, Address.i, Operations.addWithCarry),
    adc_ab(0x6du, Address.ab, Operations.addWithCarry),
    adc_abx(0x7du, Address.abx, Operations.addWithCarry),
    adc_aby(0x79u, Address.aby, Operations.addWithCarry),
    adc_z(0x65u, Address.z, Operations.addWithCarry),
    adc_zx(0x75u, Address.zx, Operations.addWithCarry),
    adc_ixir(0x61u, Address.ixir, Operations.addWithCarry),
    adc_irix(0x71u, Address.irix, Operations.addWithCarry),

    bcc(0x90u, Address.i, Operations.branchOnCarryClear),
    bcs(0xb0u, Address.i, Operations.branchOnCarrySet),
    bmi(0x30u, Address.i, Operations.branchOnMinus),
    bne(0xd0u, Address.i, Operations.branchOnNotEqual),
    beq(0xf0u, Address.i, Operations.branchOnEqual),
    bpl(0x10u, Address.i, Operations.branchOnPlus),
    brk(0x00u, Address.none, Operations.brk),
    clc(0x18u, Address.none, Operations.clearCarry),
    cld(0xd8u, Address.none, Operations.clearDecimal),

    cmp_i(0xc9u, Address.i, Operations.compareAccumulator),
    cmp_ab(0xcdu, Address.ab, Operations.compareAccumulator),
    cmp_abx(0xddu, Address.abx, Operations.compareAccumulator),
    cmp_aby(0xd9u, Address.aby, Operations.compareAccumulator),
    cmp_z(0xc5u, Address.z, Operations.compareAccumulator),
    cmp_zx(0xd5u, Address.zx, Operations.compareAccumulator),
    cmp_ixir(0xc1u, Address.ixir, Operations.compareAccumulator),
    cmp_irix(0xd1u, Address.irix, Operations.compareAccumulator),

    cpx_i(0xe0u, Address.i, Operations.notImplementedOperation),

    cpy_i(0xc0u, Address.i, Operations.compareY),
    cpy_ab(0xccu, Address.ab, Operations.compareY),
    cpy_z(0xc4u, Address.z, Operations.compareY),

    dex(0xcau, Address.none, Operations.decrementx),
    dey(0x88u, Address.none, Operations.decrementy),
    eor_i(0x49u, Address.i, Operations.exclusiveOr),


    jmp_ab(0x4cu, Address.ab, Operations.jump),

    lda_i(0xa9u, Address.i, Operations.loadAccumulator),
    lda_ab(0xadu, Address.ab, Operations.loadAccumulator),

    ldx_i(0xa2u, Address.i, Operations.loadx),
    ldx_ab(0xaeu, Address.ab, Operations.loadx),
    ldx_aby(0xbeu, Address.aby, Operations.loadx),
    ldx_z(0xa6u, Address.z, Operations.loadx),

    ldy_i(0xa0u, Address.i, Operations.loady),

    nop(0xeau, Address.none, Operations.noOperation),

    sta_z(0x85u, Address.z, Operations.storeAccumulator),
    sta_ab(0x8du, Address.ab, Operations.storeAccumulator),

    tax(0xaau, Address.none, Operations.transferAccumulatorToX),
    tya(0x98u, Address.none, Operations.transferYtoAccumulator),
    txs(0x9au, Address.none, Operations.transferXToStack);

    fun run(state: CpuState, memory: Memory) = op(this, state, memory)

    companion object {
        private val instructions = values().associateBy { it.u }

        init {
            if(instructions.size != values().size) {
                throw Error("instructions size is not equal to values size")
            }
        }


        fun from(u: UByte) = instructions[u]
    }
}
