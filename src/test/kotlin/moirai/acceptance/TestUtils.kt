package moirai.acceptance

import moirai.composition.CompilerFrontend
import moirai.composition.LocalSourceStore
import moirai.composition.SourceStore
import moirai.semantics.core.*
import java.math.BigDecimal
import org.junit.jupiter.api.Assertions
import moirai.eval.*

object TestArchitecture : Architecture {
    override val defaultNodeCost: Long = (1).toLong()
    override val costUpperLimit: Long = (5000).toLong()
}


object LargeComputationArchitecture : Architecture {
    override val defaultNodeCost: Long = (1).toLong()
    override val costUpperLimit: Long = (100000000).toLong()
}

fun testEval(
    source: String,
    architecture: Architecture
): Value {
    val sourceStore = LocalSourceStore()

    sourceStore.addArtifacts(
        listOf("test", "deep", "left"),
        """
            script test.deep.left
            
            def deepLeft(x: Int, y: Int): Int {
                x * y
            }
        """.trimIndent()
    )

    sourceStore.addArtifacts(
        listOf("test", "deep", "right"),
        """
            script test.deep.right
            
            def deepRight(x: Int, y: Int): Int {
                x * y
            }
        """.trimIndent()
    )

    sourceStore.addArtifacts(
        listOf("test", "imported"),
        """
            script test.imported
            
            import test.deep.left
            import test.deep.right
            
            record ImportedRecord(val a: Int, val b: Int)
            
            def importedFunction(x: Int, y: Int): Int {
                x * y
            }
            
            def duplicateFunction(x: Int, y: Int): Int {
                x * y
            }
        """.trimIndent()
    )

    sourceStore.addArtifacts(
        listOf("test", "duplicates"),
        """
            script test.duplicates
            
            def duplicateFunction(x: Int, y: Int): Int {
                x * y
            }
        """.trimIndent()
    )

    return eval(source, architecture, sourceStore)
}

fun failTest(source: String, expectedCount: Int, predicate: (LanguageError) -> Boolean) {
    failTest(source, expectedCount, predicate, TestArchitecture)
}

fun failTest(source: String, expectedCount: Int, predicate: (LanguageError) -> Boolean, architecture: Architecture) {
    try {
        testEval(source, architecture)
        Assertions.fail()
    } catch (ex: LanguageException) {
        Assertions.assertEquals(expectedCount, ex.errors.size)
        val predicateFailures: MutableSet<LanguageError> = HashSet()
        ex.errors.forEach {
            if (!predicate(it)) {
                predicateFailures.add(it)
            }
        }
        if (predicateFailures.isNotEmpty()) {
            throw LanguageException(predicateFailures)
        }
    }
}

fun splitTest(
    fullText: String,
    architecture: Architecture = TestArchitecture
) {
    val parts = fullText.split("^^^^^")
    val sourceActual = parts[0]
    val sourceExpected = parts[1]

    val actual = testEval(sourceActual, architecture)
    val expected = testEval(sourceExpected, architecture)

    when {
        actual is DecimalValue && expected is DecimalValue -> {
            Assertions.assertTrue(expected.canonicalForm.compareTo(actual.canonicalForm) == 0)
        }
        else -> Assertions.assertEquals(expected, actual)
    }
}

fun typeTest(source: String, predicate: (Value) -> Boolean) {
    val actual = testEval(source, TestArchitecture)
    val success = predicate(actual)
    Assertions.assertTrue(success)
}

fun assertEqualsDec(value: String, res: Value) {
    if (res is DecimalValue) {
        val dec = BigDecimal(value)
        Assertions.assertEquals(dec.stripTrailingZeros().toPlainString(), res.canonicalForm.stripTrailingZeros().toPlainString())
    } else {
        Assertions.fail()
    }
}