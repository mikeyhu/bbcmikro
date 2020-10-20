package net.chompsoftware.k6502.hardware

@ExperimentalUnsignedTypes
enum class InstructionSet(val u: UByte, val ad: Address, val op: Operation, val cy: Long) {
    adc_i(0x69u, Address.i, Operations.addWithCarry, 2),
    adc_ab(0x6du, Address.ab, Operations.addWithCarry, 4),
//    adc_abx(0x7du, Address.abx, Operations.addWithCarry, 4),
    adc_aby(0x79u, Address.aby, Operations.addWithCarry, 4),
    adc_z(0x65u, Address.z, Operations.addWithCarry, 3),
    adc_zx(0x75u, Address.zx, Operations.addWithCarry, 4),
    adc_ixir(0x61u, Address.ixir, Operations.addWithCarry, 6),
    adc_irix(0x71u, Address.irix, Operations.addWithCarry, 5),

    bcc(0x90u, Address.i, Operations.branchOnCarryClear, 2),
    bcs(0xb0u, Address.i, Operations.branchOnCarrySet, 2),
    beq(0xf0u, Address.i, Operations.branchOnEqual, 2),
    bmi(0x30u, Address.i, Operations.branchOnMinus, 2),
    bne(0xd0u, Address.i, Operations.branchOnNotEqual, 2),
    bpl(0x10u, Address.i, Operations.branchOnPlus, 2),
    bvc(0x50u, Address.i, Operations.branchOnOverflowClear, 2),
    bvs(0x70u, Address.i, Operations.branchOnOverflowSet, 2),

    brk(0x00u, Address.none, Operations.brk, 7),

    clc(0x18u, Address.none, Operations.clearCarry, 2),
    cld(0xd8u, Address.none, Operations.clearDecimal, 2),

    cmp_i(0xc9u, Address.i, Operations.compareAccumulator, 2),
    cmp_ab(0xcdu, Address.ab, Operations.compareAccumulator, 4),
//    cmp_abx(0xddu, Address.abx, Operations.compareAccumulator, 4),
    cmp_aby(0xd9u, Address.aby, Operations.compareAccumulator, 4),
    cmp_z(0xc5u, Address.z, Operations.compareAccumulator, 3),
    cmp_zx(0xd5u, Address.zx, Operations.compareAccumulator, 4),
    cmp_ixir(0xc1u, Address.ixir, Operations.compareAccumulator, 6),
    cmp_irix(0xd1u, Address.irix, Operations.compareAccumulator, 5),

    cpx_i(0xe0u, Address.i, Operations.compareX, 2),
    cpx_ab(0xecu, Address.ab, Operations.compareX, 4),
    cpx_z(0xe4u, Address.z, Operations.compareX, 3),

    cpy_i(0xc0u, Address.i, Operations.compareY, 2),
    cpy_ab(0xccu, Address.ab, Operations.compareY, 4),
    cpy_z(0xc4u, Address.z, Operations.compareY, 3),

    dex(0xcau, Address.none, Operations.decrementx, 2),
    dey(0x88u, Address.none, Operations.decrementy, 2),

    eor_i(0x49u, Address.i, Operations.exclusiveOr, 2),
    eor_ab(0x4du, Address.ab, Operations.exclusiveOr, 4),
//    eor_abx(0x5du, Address.abx, Operations.exclusiveOr, 4),
    eor_aby(0x59u, Address.aby, Operations.exclusiveOr, 4),
    eor_z(0x45u, Address.z, Operations.exclusiveOr, 3),
    eor_zx(0x55u, Address.zx, Operations.exclusiveOr, 4),
    eor_ixir(0x41u, Address.ixir, Operations.exclusiveOr, 6),
    eor_irix(0x51u, Address.irix, Operations.exclusiveOr, 5),

    inx(0xe8u, Address.none, Operations.incrementx, 2),
    iny(0xc8u, Address.none, Operations.incrementy, 2),

    jmp_ab(0x4cu, Address.ab, Operations.jump, 3),
    jmp_ir(0x6cu, Address.ir, Operations.jump, 5),
    jsr_ab(0x20u, Address.ab, Operations.jumpToSubroutine, 6),


    lda_i(0xa9u, Address.i, Operations.loadAccumulator, 2),
    lda_ab(0xadu, Address.ab, Operations.loadAccumulator, 4),
    lda_abx(0xbdu, Address.abx, Operations.loadAccumulator, 4),
    lda_aby(0xb9u, Address.aby, Operations.loadAccumulator, 4),
    lda_z(0xa5u, Address.z, Operations.loadAccumulator, 3),
    lda_zx(0xb5u, Address.zx, Operations.loadAccumulator, 4),
    lda_ixir(0xa1u, Address.ixir, Operations.loadAccumulator, 6),
    lda_irix(0xb1u, Address.irix, Operations.loadAccumulator, 5),

    ldx_i(0xa2u, Address.i, Operations.loadx, 2),
    ldx_ab(0xaeu, Address.ab, Operations.loadx, 4),
    ldx_aby(0xbeu, Address.aby, Operations.loadx, 4),
    ldx_z(0xa6u, Address.z, Operations.loadx, 3),
    ldx_zx(0xb6u, Address.zx, Operations.loadx, 4),

    ldy_i(0xa0u, Address.i, Operations.loady, 2),
    ldy_ab(0xacu, Address.ab, Operations.loady, 4),
//    ldy_abx(0xbcu, Address.abx, Operations.loady, 4),
    ldy_z(0xa4u, Address.z, Operations.loady, 3),
    ldy_zx(0xb4u, Address.zx, Operations.loady, 4),

    nop(0xeau, Address.none, Operations.noOperation, 2),

    ora_i(0x09u, Address.i, Operations.orWithAccumulator, 2),
//    ora_z(0x05u, Address.z, Operations.orWithAccumulator, 3),
//    ora_zx(0x15u, Address.zx, Operations.orWithAccumulator, 4),
//    ora_ab(0x0Du, Address.ab, Operations.orWithAccumulator, 4),
//    ora_abx(0x1Du, Address.abx, Operations.orWithAccumulator, 4),
//    ora_aby(0x19u, Address.aby, Operations.orWithAccumulator, 4),

    pha(0x48u, Address.none, Operations.pushAccumulator, 3),
    pla(0x68u, Address.none, Operations.pullAccumulator, 4),
    php(0x08u, Address.none, Operations.pushProcessorStatus, 3),
    plp(0x28u, Address.none, Operations.pullProcessorStatus, 4),

    rts(0x60u, Address.none, Operations.returnFromSubroutine, 6),
    rti(0x40u, Address.none, Operations.returnFromInterrupt, 6),

    sec(0x38u, Address.none, Operations.setCarry, 2),

    sta_ab(0x8du, Address.ab, Operations.storeAccumulator, 4),
//    sta_abx(0x9du, Address.abx, Operations.storeAccumulator, 5),
    sta_aby(0x99u, Address.aby, Operations.storeAccumulator, 5),
    sta_z(0x85u, Address.z, Operations.storeAccumulator, 3),
    sta_zx(0x95u, Address.zx, Operations.storeAccumulator, 4),
    sta_ixir(0x81u, Address.ixir, Operations.storeAccumulator, 6),
    sta_irix(0x91u, Address.irix, Operations.storeAccumulator, 6),

    stx_ab(0x8eu, Address.ab, Operations.storeX, 4),
    stx_z(0x86u, Address.z, Operations.storeX, 3),
    stx_zy(0x96u, Address.zy, Operations.storeX, 4),

    tay(0xa8u, Address.none, Operations.transferAccumulatorToY, 2),
    tax(0xaau, Address.none, Operations.transferAccumulatorToX, 2),
    tya(0x98u, Address.none, Operations.transferYtoAccumulator, 2),
    txa(0x8au, Address.none, Operations.transferXtoAccumulator, 2),
    txs(0x9au, Address.none, Operations.transferXToStack, 2),
    tsx(0xbau, Address.none, Operations.transferStackToX, 2);

    fun run(state: CpuState, memory: Memory) = op(this, state, memory)

    companion object {
        private val instructions = values().associateBy { it.u }

        init {
            if (instructions.size != values().size) {
                throw Error("instructions size is not equal to values size")
            }
        }

        fun from(u: UByte) = instructions[u]
    }
}

enum class Address(val size: Int) {
    none(1),
    i(2),
    irix(100000001),
    ixir(100000002),
    ir(1000000006),
    ab(3),
    abx(3),
    aby(100000004),
    z(2),
    zx(100000005),
    zy(100000007)
}