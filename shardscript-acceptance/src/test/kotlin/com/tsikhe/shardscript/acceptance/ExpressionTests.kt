package com.tsikhe.shardscript.acceptance

import org.junit.Test

class ExpressionTests {
    @Test
    fun plus() {
        splitTest("5 + 6^^^^^11")
    }

    @Test
    fun minus() {
        splitTest("5 - 1^^^^^4")
    }

    @Test
    fun mul() {
        splitTest("5 * 7^^^^^35")
    }

    @Test
    fun div() {
        splitTest("10 / 5^^^^^2")
    }

    @Test
    fun mod() {
        splitTest("5 % 2^^^^^1")
    }

    @Test
    fun orderOfOperations() {
        splitTest("5 + 2 * 6 + 9 / 3^^^^^20")
    }

    @Test
    fun testGreaterThan() {
        splitTest("5 > 6^^^^^false")
    }

    @Test
    fun testLessThan() {
        splitTest("5 < 6^^^^^true")
    }

    @Test
    fun testAnd() {
        splitTest("5 < 6 && 7 < 8^^^^^true")
    }

    @Test
    fun testOr() {
        splitTest("5 < 6 || 9 < 8^^^^^true")
    }

    @Test
    fun testComplexExpr() {
        splitTest("5 + 2 * 3 < 7 * 8 + 9 && false || 7 + 3 / 1 > 45 / 9^^^^^true")
    }

    @Test
    fun plusDec() {
        splitTest("5.0 + 6.0^^^^^11.0")
    }

    @Test
    fun minusDec() {
        splitTest("5.0 - 1.0^^^^^4.0")
    }

    @Test
    fun mulDec() {
        splitTest("5.0 * 7.0^^^^^35.0")
    }

    @Test
    fun divDec() {
        splitTest("10.0 / 5.0^^^^^2.0")
    }

    @Test
    fun modDec() {
        splitTest("5.0 % 2.0^^^^^1.0")
    }

    @Test
    fun orderOfOperationsDec() {
        splitTest("5.0 + 2.0 * 6.0 + 9.0 / 3.0^^^^^20.0")
    }

    @Test
    fun testGreaterThanDec() {
        splitTest("5.0 > 6.0^^^^^false")
    }

    @Test
    fun testLessThanDec() {
        splitTest("5.0 < 6.0^^^^^true")
    }

    @Test
    fun testAndDec() {
        splitTest("5.0 < 6.0 && 7.0 < 8.0^^^^^true")
    }

    @Test
    fun testOrDec() {
        splitTest("5.0 < 6.0 || 9.0 < 8.0^^^^^true")
    }

    @Test
    fun testComplexExprDec() {
        splitTest("5.0 + 2.0 * 3.0 < 7.0 * 8.0 + 9.0 && false || 7.0 + 3.0 / 1.0 > 45.0 / 9.0^^^^^true")
    }

    @Test
    fun testUnaryNot() {
        splitTest("!true^^^^^false")
    }

    @Test
    fun s8Plus() {
        splitTest("5s8 + 6s8^^^^^11s8")
    }

    @Test
    fun s8Minus() {
        splitTest("5s8 - 1s8^^^^^4s8")
    }

    @Test
    fun s8Mul() {
        splitTest("5s8 * 7s8^^^^^35s8")
    }

    @Test
    fun s8Div() {
        splitTest("10s8 / 5s8^^^^^2s8")
    }

    @Test
    fun s8Mod() {
        splitTest("5s8 % 2s8^^^^^1s8")
    }

    @Test
    fun s8OrderOfOperations() {
        splitTest("5s8 + 2s8 * 6s8 + 9s8 / 3s8^^^^^20s8")
    }

    @Test
    fun s8TestGreaterThan() {
        splitTest("5s8 > 6s8^^^^^false")
    }

    @Test
    fun s8TestLessThan() {
        splitTest("5s8 < 6s8^^^^^true")
    }

    @Test
    fun s8TestAnd() {
        splitTest("5s8 < 6s8 && 7s8 < 8s8^^^^^true")
    }

    @Test
    fun s8TestOr() {
        splitTest("5s8 < 6s8 || 9s8 < 8s8^^^^^true")
    }

    @Test
    fun s8TestComplexExpr() {
        splitTest("5s8 + 2s8 * 3s8 < 7s8 * 8s8 + 9s8 && false || 7s8 + 3s8 / 1s8 > 45s8 / 9s8^^^^^true")
    }

    @Test
    fun s16Plus() {
        splitTest("5s16 + 6s16^^^^^11s16")
    }

    @Test
    fun s16Minus() {
        splitTest("5s16 - 1s16^^^^^4s16")
    }

    @Test
    fun s16Mul() {
        splitTest("5s16 * 7s16^^^^^35s16")
    }

    @Test
    fun s16Div() {
        splitTest("10s16 / 5s16^^^^^2s16")
    }

    @Test
    fun s16Mod() {
        splitTest("5s16 % 2s16^^^^^1s16")
    }

    @Test
    fun s16OrderOfOperations() {
        splitTest("5s16 + 2s16 * 6s16 + 9s16 / 3s16^^^^^20s16")
    }

    @Test
    fun s16TestGreaterThan() {
        splitTest("5s16 > 6s16^^^^^false")
    }

    @Test
    fun s16TestLessThan() {
        splitTest("5s16 < 6s16^^^^^true")
    }

    @Test
    fun s16TestAnd() {
        splitTest("5s16 < 6s16 && 7s16 < 8s16^^^^^true")
    }

    @Test
    fun s16TestOr() {
        splitTest("5s16 < 6s16 || 9s16 < 8s16^^^^^true")
    }

    @Test
    fun s16TestComplexExpr() {
        splitTest("5s16 + 2s16 * 3s16 < 7s16 * 8s16 + 9s16 && false || 7s16 + 3s16 / 1s16 > 45s16 / 9s16^^^^^true")
    }

    @Test
    fun s64Plus() {
        splitTest("5s64 + 6s64^^^^^11s64")
    }

    @Test
    fun s64Minus() {
        splitTest("5s64 - 1s64^^^^^4s64")
    }

    @Test
    fun s64Mul() {
        splitTest("5s64 * 7s64^^^^^35s64")
    }

    @Test
    fun s64Div() {
        splitTest("10s64 / 5s64^^^^^2s64")
    }

    @Test
    fun s64Mod() {
        splitTest("5s64 % 2s64^^^^^1s64")
    }

    @Test
    fun s64OrderOfOperations() {
        splitTest("5s64 + 2s64 * 6s64 + 9s64 / 3s64^^^^^20s64")
    }

    @Test
    fun s64TestGreaterThan() {
        splitTest("5s64 > 6s64^^^^^false")
    }

    @Test
    fun s64TestLessThan() {
        splitTest("5s64 < 6s64^^^^^true")
    }

    @Test
    fun s64TestAnd() {
        splitTest("5s64 < 6s64 && 7s64 < 8s64^^^^^true")
    }

    @Test
    fun s64TestOr() {
        splitTest("5s64 < 6s64 || 9s64 < 8s64^^^^^true")
    }

    @Test
    fun s64TestComplexExpr() {
        splitTest("5s64 + 2s64 * 3s64 < 7s64 * 8s64 + 9s64 && false || 7s64 + 3s64 / 1s64 > 45s64 / 9s64^^^^^true")
    }

    @Test
    fun u8Plus() {
        splitTest("5u8 + 6u8^^^^^11u8")
    }

    @Test
    fun u8Minus() {
        splitTest("5u8 - 1u8^^^^^4u8")
    }

    @Test
    fun u8Mul() {
        splitTest("5u8 * 7u8^^^^^35u8")
    }

    @Test
    fun u8Div() {
        splitTest("10u8 / 5u8^^^^^2u8")
    }

    @Test
    fun u8Mod() {
        splitTest("5u8 % 2u8^^^^^1u8")
    }

    @Test
    fun u8OrderOfOperations() {
        splitTest("5u8 + 2u8 * 6u8 + 9u8 / 3u8^^^^^20u8")
    }

    @Test
    fun u8TestGreaterThan() {
        splitTest("5u8 > 6u8^^^^^false")
    }

    @Test
    fun u8TestLessThan() {
        splitTest("5u8 < 6u8^^^^^true")
    }

    @Test
    fun u8TestAnd() {
        splitTest("5u8 < 6u8 && 7u8 < 8u8^^^^^true")
    }

    @Test
    fun u8TestOr() {
        splitTest("5u8 < 6u8 || 9u8 < 8u8^^^^^true")
    }

    @Test
    fun u8TestComplexExpr() {
        splitTest("5u8 + 2u8 * 3u8 < 7u8 * 8u8 + 9u8 && false || 7u8 + 3u8 / 1u8 > 45u8 / 9u8^^^^^true")
    }

    @Test
    fun u16Plus() {
        splitTest("5u16 + 6u16^^^^^11u16")
    }

    @Test
    fun u16Minus() {
        splitTest("5u16 - 1u16^^^^^4u16")
    }

    @Test
    fun u16Mul() {
        splitTest("5u16 * 7u16^^^^^35u16")
    }

    @Test
    fun u16Div() {
        splitTest("10u16 / 5u16^^^^^2u16")
    }

    @Test
    fun u16Mod() {
        splitTest("5u16 % 2u16^^^^^1u16")
    }

    @Test
    fun u16OrderOfOperations() {
        splitTest("5u16 + 2u16 * 6u16 + 9u16 / 3u16^^^^^20u16")
    }

    @Test
    fun u16TestGreaterThan() {
        splitTest("5u16 > 6u16^^^^^false")
    }

    @Test
    fun u16TestLessThan() {
        splitTest("5u16 < 6u16^^^^^true")
    }

    @Test
    fun u16TestAnd() {
        splitTest("5u16 < 6u16 && 7u16 < 8u16^^^^^true")
    }

    @Test
    fun u16TestOr() {
        splitTest("5u16 < 6u16 || 9u16 < 8u16^^^^^true")
    }

    @Test
    fun u16TestComplexExpr() {
        splitTest("5u16 + 2u16 * 3u16 < 7u16 * 8u16 + 9u16 && false || 7u16 + 3u16 / 1u16 > 45u16 / 9u16^^^^^true")
    }

    @Test
    fun u32Plus() {
        splitTest("5u32 + 6u32^^^^^11u32")
    }

    @Test
    fun u32Minus() {
        splitTest("5u32 - 1u32^^^^^4u32")
    }

    @Test
    fun u32Mul() {
        splitTest("5u32 * 7u32^^^^^35u32")
    }

    @Test
    fun u32Div() {
        splitTest("10u32 / 5u32^^^^^2u32")
    }

    @Test
    fun u32Mod() {
        splitTest("5u32 % 2u32^^^^^1u32")
    }

    @Test
    fun u32OrderOfOperations() {
        splitTest("5u32 + 2u32 * 6u32 + 9u32 / 3u32^^^^^20u32")
    }

    @Test
    fun u32TestGreaterThan() {
        splitTest("5u32 > 6u32^^^^^false")
    }

    @Test
    fun u32TestLessThan() {
        splitTest("5u32 < 6u32^^^^^true")
    }

    @Test
    fun u32TestAnd() {
        splitTest("5u32 < 6u32 && 7u32 < 8u32^^^^^true")
    }

    @Test
    fun u32TestOr() {
        splitTest("5u32 < 6u32 || 9u32 < 8u32^^^^^true")
    }

    @Test
    fun u32TestComplexExpr() {
        splitTest("5u32 + 2u32 * 3u32 < 7u32 * 8u32 + 9u32 && false || 7u32 + 3u32 / 1u32 > 45u32 / 9u32^^^^^true")
    }

    @Test
    fun u64Plus() {
        splitTest("5u64 + 6u64^^^^^11u64")
    }

    @Test
    fun u64Minus() {
        splitTest("5u64 - 1u64^^^^^4u64")
    }

    @Test
    fun u64Mul() {
        splitTest("5u64 * 7u64^^^^^35u64")
    }

    @Test
    fun u64Div() {
        splitTest("10u64 / 5u64^^^^^2u64")
    }

    @Test
    fun u64Mod() {
        splitTest("5u64 % 2u64^^^^^1u64")
    }

    @Test
    fun u64OrderOfOperations() {
        splitTest("5u64 + 2u64 * 6u64 + 9u64 / 3u64^^^^^20u64")
    }

    @Test
    fun u64TestGreaterThan() {
        splitTest("5u64 > 6u64^^^^^false")
    }

    @Test
    fun u64TestLessThan() {
        splitTest("5u64 < 6u64^^^^^true")
    }

    @Test
    fun u64TestAnd() {
        splitTest("5u64 < 6u64 && 7u64 < 8u64^^^^^true")
    }

    @Test
    fun u64TestOr() {
        splitTest("5u64 < 6u64 || 9u64 < 8u64^^^^^true")
    }

    @Test
    fun u64TestComplexExpr() {
        splitTest("5u64 + 2u64 * 3u64 < 7u64 * 8u64 + 9u64 && false || 7u64 + 3u64 / 1u64 > 45u64 / 9u64^^^^^true")
    }
}