package com.tsikhe.shardscript.acceptance

import com.tsikhe.shardscript.composition.CompilerFrontend
import com.tsikhe.shardscript.composition.LocalSourceStore
import com.tsikhe.shardscript.composition.SourceStore
import com.tsikhe.shardscript.eval.EvalAstVisitor
import com.tsikhe.shardscript.semantics.core.*
import com.tsikhe.shardscript.semantics.workflow.createSystemScopes
import org.junit.Assert
import java.math.BigDecimal
import java.math.BigInteger

object TestArchitecture : Architecture {
    override val defaultNodeCost: BigInteger = (1).toBigInteger()
    override val distributedPluginCost: BigInteger = (1000).toBigInteger()
    override val costUpperLimit: BigInteger = (5000).toBigInteger()
}


object LargeComputationArchitecture : Architecture {
    override val defaultNodeCost: BigInteger = (1).toBigInteger()
    override val distributedPluginCost: BigInteger = (1000).toBigInteger()
    override val costUpperLimit: BigInteger = (100000000).toBigInteger()
}

fun eval(
    fileName: String,
    source: String,
    architecture: Architecture,
    sourceStore: SourceStore,
    transient: Boolean
): Value {
    val frontend = CompilerFrontend(architecture, sourceStore)

    val systemScopes = createSystemScopes(architecture)
    val executionArtifacts = frontend.compile(fileName, source, systemScopes, transient)

    val prelude = executionArtifacts.semanticArtifacts.userScopes.systemScopes.prelude

    val router = SymbolRouterValueTable(
        prelude,
        executionArtifacts.semanticArtifacts.file
    )
    val globalScope = ValueTable(router)
    val evalVisitor = EvalAstVisitor(prelude)

    router.initFunctionCallback = { fv ->
        fv.globalScope = globalScope
        fv.evalCallback = { a, v ->
            a.accept(evalVisitor, v)
        }
    }

    val executionScope = ValueTable(globalScope)
    return executionArtifacts.processedAst.accept(evalVisitor, executionScope)
}

fun testEval(
    source: String,
    architecture: Architecture,
    transient: Boolean = false
): Value {
    val sourceStore = LocalSourceStore()

    sourceStore.addArtifacts(
        listOf("test", "deep", "left"),
        """
            def deepLeft(x: Int, y: Int): Int {
                x * y
            }
        """.trimIndent()
    )

    sourceStore.addArtifacts(
        listOf("test", "deep", "right"),
        """
            def deepRight(x: Int, y: Int): Int {
                x * y
            }
        """.trimIndent()
    )

    sourceStore.addArtifacts(
        listOf("test", "imported"),
        """
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
            def duplicateFunction(x: Int, y: Int): Int {
                x * y
            }
        """.trimIndent()
    )

    sourceStore.addArtifacts(
        listOf("test", "transient"),
        """
            record K(val x: Int, val y: Int)
            record V(val s: String<20>)
        """.trimIndent()
    )

    return if(transient) {
        eval("test.transient", source, architecture, sourceStore, transient)
    } else {
        eval("script", source, architecture, sourceStore, transient)
    }
}

fun failTest(source: String, expectedCount: Int, predicate: (LanguageError) -> Boolean) {
    failTest(source, expectedCount, predicate, TestArchitecture, false)
}

fun failTest(source: String, expectedCount: Int, predicate: (LanguageError) -> Boolean, architecture: Architecture, transient: Boolean) {
    try {
        testEval(source, architecture, transient)
        Assert.fail()
    } catch (ex: LanguageException) {
        Assert.assertEquals(expectedCount, ex.errors.size)
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
            Assert.assertTrue(expected.canonicalForm.compareTo(actual.canonicalForm) == 0)
        }
        else -> Assert.assertEquals(expected, actual)
    }
}

fun assertEqualsDec(value: String, res: Value) {
    if (res is DecimalValue) {
        val dec = BigDecimal(value)
        Assert.assertEquals(dec.stripTrailingZeros().toPlainString(), res.canonicalForm.stripTrailingZeros().toPlainString())
    } else {
        Assert.fail()
    }
}