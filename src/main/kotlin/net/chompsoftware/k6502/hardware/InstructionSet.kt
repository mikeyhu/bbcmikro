package net.chompsoftware.k6502.hardware

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
    beq(0xf0u, Address.i, Operations.branchOnEqual),
    bmi(0x30u, Address.i, Operations.branchOnMinus),
    bne(0xd0u, Address.i, Operations.branchOnNotEqual),
    bpl(0x10u, Address.i, Operations.branchOnPlus),
    bvc(0x50u, Address.i, Operations.branchOnOverflowClear),
    bvs(0x70u, Address.i, Operations.branchOnOverflowSet),

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

    cpx_i(0xe0u, Address.i, Operations.compareX),
    cpx_ab(0xecu, Address.ab, Operations.compareX),
    cpx_z(0xe4u, Address.z, Operations.compareX),

    cpy_i(0xc0u, Address.i, Operations.compareY),
    cpy_ab(0xccu, Address.ab, Operations.compareY),
    cpy_z(0xc4u, Address.z, Operations.compareY),

    dex(0xcau, Address.none, Operations.decrementx),
    dey(0x88u, Address.none, Operations.decrementy),

    eor_i(0x49u, Address.i, Operations.exclusiveOr),
    eor_ab(0x4du, Address.ab, Operations.exclusiveOr),
    eor_abx(0x5du, Address.abx, Operations.exclusiveOr),
    eor_aby(0x59u, Address.aby, Operations.exclusiveOr),
    eor_z(0x45u, Address.z, Operations.exclusiveOr),
    eor_zx(0x55u, Address.zx, Operations.exclusiveOr),
    eor_ixir(0x41u, Address.ixir, Operations.exclusiveOr),
    eor_irix(0x51u, Address.irix, Operations.exclusiveOr),

    jmp_ab(0x4cu, Address.ab, Operations.jump),

    lda_i(0xa9u, Address.i, Operations.loadAccumulator),
    lda_ab(0xadu, Address.ab, Operations.loadAccumulator),
    lda_abx(0xbdu, Address.abx, Operations.loadAccumulator),
    lda_aby(0xb9u, Address.aby, Operations.loadAccumulator),
    lda_z(0xa5u, Address.z, Operations.loadAccumulator),
    lda_zx(0xb5u, Address.zx, Operations.loadAccumulator),
    lda_ixir(0xa1u, Address.ixir, Operations.loadAccumulator),
    lda_irix(0xb1u, Address.irix, Operations.loadAccumulator),

    ldx_i(0xa2u, Address.i, Operations.loadx),
    ldx_ab(0xaeu, Address.ab, Operations.loadx),
    ldx_aby(0xbeu, Address.aby, Operations.loadx),
    ldx_z(0xa6u, Address.z, Operations.loadx),
    ldx_zx(0xb6u, Address.zx, Operations.loadx),

    ldy_i(0xa0u, Address.i, Operations.loady),
    ldy_ab(0xacu, Address.ab, Operations.loady),
    ldy_abx(0xbcu, Address.abx, Operations.loady),
    ldy_z(0xa4u, Address.z, Operations.loady),
    ldy_zx(0xb4u, Address.zx, Operations.loady),

    nop(0xeau, Address.none, Operations.noOperation),

    pha(0x48u, Address.none, Operations.pushAccumulator),
    pla(0x68u, Address.none, Operations.pullAccumulator),
    plp(0x28u, Address.none, Operations.pullProcessorStatus),

    sta_ab(0x8du, Address.ab, Operations.storeAccumulator),
    sta_abx(0x9du, Address.abx, Operations.storeAccumulator),
    sta_aby(0x99u, Address.aby, Operations.storeAccumulator),
    sta_z(0x85u, Address.z, Operations.storeAccumulator),
    sta_zx(0x95u, Address.zx, Operations.storeAccumulator),
    sta_ixir(0x81u, Address.ixir, Operations.storeAccumulator),
    sta_irix(0x91u, Address.irix, Operations.storeAccumulator),

    tay(0xa8u, Address.none, Operations.transferAccumulatorToY),
    tax(0xaau, Address.none, Operations.transferAccumulatorToX),
    tya(0x98u, Address.none, Operations.transferYtoAccumulator),
    txa(0x8au, Address.none, Operations.transferXtoAccumulator),
    txs(0x9au, Address.none, Operations.transferXToStack),
    tsx(0xbau, Address.none, Operations.transferStackToX);

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