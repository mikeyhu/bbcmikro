package net.chompsoftware.k6502.hardware

import net.chompsoftware.k6502.hardware.operations.*
import net.chompsoftware.k6502.hardware.operations.Operations.withPosition
import net.chompsoftware.k6502.hardware.operations.Operations.withRead

@ExperimentalUnsignedTypes
enum class InstructionSet(val u: UByte, val ad: Address, val op: Operation, val cy: Long) {
    // Add With Carry
    adc_i(0x69u, Address.i, withRead(MathOperations.addWithCarry), 2),
//    adc_ab(0x6du, Address.ab, withRead(MathOperations.addWithCarry), 4),
//    adc_abx(0x7du, Address.abx, withRead(MathOperations.addWithCarry), 4),
//    adc_aby(0x79u, Address.aby, withRead(MathOperations.addWithCarry), 4),
//    adc_z(0x65u, Address.z, withRead(MathOperations.addWithCarry), 3),
//    adc_zx(0x75u, Address.zx, withRead(MathOperations.addWithCarry), 4),
//    adc_iix(0x61u, Address.iix, withRead(MathOperations.addWithCarry), 6),
//    adc_iiy(0x71u, Address.iiy, withRead(MathOperations.addWithCarry), 5),

    // And With Accumulator
//    and_i(0x29u, Address.i, Operations.notImplementedOperation, 2),
//    and_z(0x2du, Address.z, Operations.notImplementedOperation, 3),
//    and_zx(0x3du, Address.zx, Operations.notImplementedOperation, 4),
//    and_ab(0x39u, Address.ab, Operations.notImplementedOperation, 4),
//    and_abx(0x25u, Address.abx, Operations.notImplementedOperation, 4),
//    and_aby(0x35u, Address.aby, Operations.notImplementedOperation, 4),
//    and_iix(0x21u, Address.iix, Operations.notImplementedOperation, 6),
//    and_iiy(0x31u, Address.iiy, Operations.notImplementedOperation, 5),

    // Arithmetic Shift Left
    asl_none(0x0au, Address.none, MathOperations.arithmeticShiftLeft, 2),
//    asl_ab(0x0eu, Address.ab, MathOperations.arithmeticShiftLeft, 6),
//    asl_abx(0x1eu, Address.abx, MathOperations.arithmeticShiftLeft, 7),
    asl_z(0x06u, Address.z, MathOperations.arithmeticShiftLeft, 5),
//    asl_zx(0x16u, Address.zx, MathOperations.arithmeticShiftLeft, 6),

    // Bit
    bit_z(0x24u, Address.z, withRead(MathOperations.bitWithAccumulator), 3),
    bit_ab(0x2cu, Address.ab, withRead(MathOperations.bitWithAccumulator), 3),

    // Branch
    bcc(0x90u, Address.i, withRead(BranchOperations.branchOnCarryClear), 2),
    bcs(0xb0u, Address.i, withRead(BranchOperations.branchOnCarrySet), 2),
    beq(0xf0u, Address.i, withRead(BranchOperations.branchOnEqual), 2),
    bmi(0x30u, Address.i, withRead(BranchOperations.branchOnMinus), 2),
    bne(0xd0u, Address.i, withRead(BranchOperations.branchOnNotEqual), 2),
    bpl(0x10u, Address.i, withRead(BranchOperations.branchOnPlus), 2),
    bvc(0x50u, Address.i, withRead(BranchOperations.branchOnOverflowClear), 2),
    bvs(0x70u, Address.i, withRead(BranchOperations.branchOnOverflowSet), 2),

    // Break
    brk(0x00u, Address.none, Operations.brk, 7),

    // Clear flags
    clc(0x18u, Address.none, FlagOperations.clearCarry, 2),
    cld(0xd8u, Address.none, FlagOperations.clearDecimal, 2),
    clv(0xb8u, Address.none, FlagOperations.clearOverflow, 2),
    cli(0x58u, Address.none, FlagOperations.clearInterrupt, 2),

    // Compare Accumulator
    cmp_i(0xc9u, Address.i, withRead(ComparisonOperations.compareAccumulator), 2),
    cmp_ab(0xcdu, Address.ab, withRead(ComparisonOperations.compareAccumulator), 4),
    cmp_abx(0xddu, Address.abx, withRead(ComparisonOperations.compareAccumulator), 4),
    cmp_aby(0xd9u, Address.aby, withRead(ComparisonOperations.compareAccumulator), 4),
    cmp_z(0xc5u, Address.z, withRead(ComparisonOperations.compareAccumulator), 3),
    cmp_zx(0xd5u, Address.zx, withRead(ComparisonOperations.compareAccumulator), 4),

    cmp_iix(0xc1u, Address.iix, withRead(ComparisonOperations.compareAccumulator), 6),
    cmp_iiy(0xd1u, Address.iiy, withRead(ComparisonOperations.compareAccumulator), 5),

    // Compare X
    cpx_i(0xe0u, Address.i, withRead(ComparisonOperations.compareX), 2),
    cpx_ab(0xecu, Address.ab, withRead(ComparisonOperations.compareX), 4),
    cpx_z(0xe4u, Address.z, withRead(ComparisonOperations.compareX), 3),

    // Compare Y
    cpy_i(0xc0u, Address.i, withRead(ComparisonOperations.compareY), 2),
    cpy_ab(0xccu, Address.ab, withRead(ComparisonOperations.compareY), 4),
    cpy_z(0xc4u, Address.z, withRead(ComparisonOperations.compareY), 3),

    // Decrement
//    dec_ab(0xceu, Address.ab, Operations.notImplementedOperation, 5),
//    dec_abx(0xdeu, Address.abx, Operations.notImplementedOperation, 6),
//    dec_z(0xc6u, Address.z, Operations.notImplementedOperation, 6),
//    dec_zx(0xd6u, Address.zx, Operations.notImplementedOperation, 7),

    // Decrement X, Y
    dex(0xcau, Address.none, MathOperations.decrementx, 2),
    dey(0x88u, Address.none, MathOperations.decrementy, 2),

    // Exclusive OR
    eor_i(0x49u, Address.i, MathOperations.exclusiveOr, 2),
//    eor_ab(0x4du, Address.ab, MathOperations.exclusiveOr, 4),
//    eor_abx(0x5du, Address.abx, MathOperations.exclusiveOr, 4),
//    eor_aby(0x59u, Address.aby, MathOperations.exclusiveOr, 4),
//    eor_z(0x45u, Address.z, MathOperations.exclusiveOr, 3),
//    eor_zx(0x55u, Address.zx, MathOperations.exclusiveOr, 4),
//    eor_iix(0x41u, Address.iix, MathOperations.exclusiveOr, 6),
//    eor_iiy(0x51u, Address.iiy, MathOperations.exclusiveOr, 5),

    // Increment
//    inc_z(0xe6u, Address.z, Operations.notImplementedOperation, 5),
//    inc_zx(0xf6u, Address.zx, Operations.notImplementedOperation, 6),
//    inc_ab(0xeeu, Address.ab, Operations.notImplementedOperation, 6),
//    inc_abx(0xfeu, Address.abx, Operations.notImplementedOperation, 7),

    // Increment X,Y
    inx(0xe8u, Address.none, MathOperations.incrementx, 2),
    iny(0xc8u, Address.none, MathOperations.incrementy, 2),

    // Jump
    jmp_ab(0x4cu, Address.ab, Operations.jump, 3),
    jmp_ir(0x6cu, Address.ir, Operations.jump, 5),
    jsr_ab(0x20u, Address.ab, Operations.jumpToSubroutine, 6),

    // Load Accumulator
    lda_i(0xa9u, Address.i, withRead(MemoryOperations.loadAccumulator), 2),
    lda_ab(0xadu, Address.ab, withRead(MemoryOperations.loadAccumulator), 4),
    lda_abx(0xbdu, Address.abx, withRead(MemoryOperations.loadAccumulator), 4),
    lda_aby(0xb9u, Address.aby, withRead(MemoryOperations.loadAccumulator), 4),
    lda_z(0xa5u, Address.z, withRead(MemoryOperations.loadAccumulator), 3),
    lda_zx(0xb5u, Address.zx, withRead(MemoryOperations.loadAccumulator), 4),
    lda_iix(0xa1u, Address.iix, withRead(MemoryOperations.loadAccumulator), 6),
    lda_iiy(0xb1u, Address.iiy, withRead(MemoryOperations.loadAccumulator), 5),

    // Load X
    ldx_i(0xa2u, Address.i, withRead(MemoryOperations.loadx), 2),
    ldx_ab(0xaeu, Address.ab, withRead(MemoryOperations.loadx), 4),
    ldx_aby(0xbeu, Address.aby, withRead(MemoryOperations.loadx), 4),
    ldx_z(0xa6u, Address.z, withRead(MemoryOperations.loadx), 3),
    ldx_zy(0xb6u, Address.zy, withRead(MemoryOperations.loadx), 4),

    // Load Y
    ldy_i(0xa0u, Address.i, withRead(MemoryOperations.loady), 2),
    ldy_ab(0xacu, Address.ab, withRead(MemoryOperations.loady), 4),
    ldy_abx(0xbcu, Address.abx, withRead(MemoryOperations.loady), 4),
    ldy_z(0xa4u, Address.z, withRead(MemoryOperations.loady), 3),
    ldy_zx(0xb4u, Address.zx, withRead(MemoryOperations.loady), 4),

    // Logical Shift Right
    lsr_none(0x4au, Address.none, MathOperations.logicalShiftRight, 2),
    lsr_z(0x46u, Address.z, MathOperations.logicalShiftRight, 5),
//    lsr_zx(0x56u, Address.zx, MathOperations.logicalShiftRight, 6),
//    lsr_ab(0x4eu, Address.ab, MathOperations.logicalShiftRight, 6),
//    lsr_abx(0x5eu, Address.abx, MathOperations.logicalShiftRight, 7),

    // No Operation
    nop(0xeau, Address.none, Operations.noOperation, 2),

    // Or With Accumulator
    ora_i(0x09u, Address.i, MathOperations.orWithAccumulator, 2),
//    ora_z(0x05u, Address.z, MathOperations.orWithAccumulator, 3),
//    ora_zx(0x15u, Address.zx, MathOperations.orWithAccumulator, 4),
//    ora_ab(0x0Du, Address.ab, MathOperations.orWithAccumulator, 4),
//    ora_abx(0x1Du, Address.abx, MathOperations.orWithAccumulator, 4),
//    ora_aby(0x19u, Address.aby, MathOperations.orWithAccumulator, 4),

    // Rotate Left
//    rol_none(0x2au, Address.none, Operations.notImplementedOperation, 2),
//    rol_z(0x26u, Address.z, Operations.notImplementedOperation, 5),
//    rol_zx(0x36u, Address.zx, Operations.notImplementedOperation, 6),
//    rol_ab(0x2eu, Address.ab, Operations.notImplementedOperation, 6),
//    rol_abx(0x3eu, Address.abx, Operations.notImplementedOperation, 7),

    // Rotate Right
//    ror_none(0x6au, Address.none, Operations.notImplementedOperation, 2),
//    ror_z(0x66u, Address.z, Operations.notImplementedOperation, 5),
//    ror_zx(0x76u, Address.zx, Operations.notImplementedOperation, 6),
//    ror_ab(0x6eu, Address.ab, Operations.notImplementedOperation, 6),
//    ror_abx(0x7eu, Address.abx, Operations.notImplementedOperation, 7),

    // Push and Pull Stack Operations
    pha(0x48u, Address.none, StackOperations.pushAccumulator, 3),
    php(0x08u, Address.none, StackOperations.pushProcessorStatus, 3),
    pla(0x68u, Address.none, StackOperations.pullAccumulator, 4),
    plp(0x28u, Address.none, StackOperations.pullProcessorStatus, 4),

    // Return
    rts(0x60u, Address.none, Operations.returnFromSubroutine, 6),
    rti(0x40u, Address.none, Operations.returnFromInterrupt, 6),

    // Subtract With Carry
//    sbc_i(0xe9u, Address.i, Operations.notImplementedOperation, 2),
//    sbc_z(0xe5u, Address.z, Operations.notImplementedOperation, 3),
//    sbc_zx(0xf5u, Address.zx, Operations.notImplementedOperation, 4),
//    sbc_ab(0xedu, Address.ab, Operations.notImplementedOperation, 4),
//    sbc_abx(0xfdu, Address.abx, Operations.notImplementedOperation, 4),
//    sbc_aby(0xf9u, Address.aby, Operations.notImplementedOperation, 4),
//    sbc_iix(0xe1u, Address.iix, Operations.notImplementedOperation, 6),
//    sbc_iiy(0xf1u, Address.iiy, Operations.notImplementedOperation, 5),

    // Set Flags
    sec(0x38u, Address.none, FlagOperations.setCarry, 2),
    sei(0x78u, Address.none, FlagOperations.setInterrupt, 2),
    sed(0xf8u, Address.none, FlagOperations.setDecimal, 2),

    // Store Accumulator
    sta_ab(0x8du, Address.ab, withPosition(MemoryOperations.storeAccumulator), 4),
    sta_abx(0x9du, Address.abx, withPosition(MemoryOperations.storeAccumulator), 5),
    sta_aby(0x99u, Address.aby, withPosition(MemoryOperations.storeAccumulator), 5),
    sta_z(0x85u, Address.z, withPosition(MemoryOperations.storeAccumulator), 3),
    sta_zx(0x95u, Address.zx, withPosition(MemoryOperations.storeAccumulator), 4),
    sta_iix(0x81u, Address.iix, withPosition(MemoryOperations.storeAccumulator), 6),
    sta_iiy(0x91u, Address.iiy, withPosition(MemoryOperations.storeAccumulator), 6),

    // Store X
    stx_ab(0x8eu, Address.ab, withPosition(MemoryOperations.storeX), 4),
    stx_z(0x86u, Address.z, withPosition(MemoryOperations.storeX), 3),
    stx_zy(0x96u, Address.zy, withPosition(MemoryOperations.storeX), 4),

    // Store Y
    sty_ab(0x8cu, Address.ab, withPosition(MemoryOperations.storeY), 4),
    sty_z(0x84u, Address.z, withPosition(MemoryOperations.storeY), 3),
    sty_zx(0x94u, Address.zx, withPosition(MemoryOperations.storeY), 4),

    // Transfer
    tay(0xa8u, Address.none, TransferOperations.transferAccumulatorToY, 2),
    tax(0xaau, Address.none, TransferOperations.transferAccumulatorToX, 2),
    tya(0x98u, Address.none, TransferOperations.transferYtoAccumulator, 2),
    txa(0x8au, Address.none, TransferOperations.transferXtoAccumulator, 2),
    txs(0x9au, Address.none, TransferOperations.transferXToStack, 2),
    tsx(0xbau, Address.none, TransferOperations.transferStackToX, 2);

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
    iiy(2),
    iix(2),
    ir(3),
    ab(3),
    abx(3),
    aby(3),
    z(2),
    zx(2),
    zy(2)
}