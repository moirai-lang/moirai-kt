package moirai.composition

import moirai.semantics.core.*
import moirai.semantics.prelude.Lang
import moirai.semantics.visitors.qualifiedName
import org.antlr.v4.runtime.tree.ParseTreeWalker

internal fun parsePlugins(fileName: String, pluginSource: String, errors: LanguageErrors): List<PluginDefLiteral> {
    val parser = createParser(pluginSource)
    val parseTree = parser.grammar.file()

    val pluginsParseTreeListener = PluginsParseTreeListener(fileName, errors)

    ParseTreeWalker.DEFAULT.walk(pluginsParseTreeListener, parseTree)

    if (parser.listener.hasErrors()) {
        parser.listener.populateErrors(errors, fileName)
        if (errors.toSet().isNotEmpty()) {
            throw LanguageException(errors.toSet())
        }
    }

    return pluginsParseTreeListener.listPlugins()
}

internal fun processPlugins(fileName: String, pluginSource: String, scope: SymbolTable): List<ParameterizedStaticPluginSymbol> {
    val errors = LanguageErrors()
    val plugins = parsePlugins(fileName, pluginSource, errors)
    val res: MutableList<ParameterizedStaticPluginSymbol> = mutableListOf()

    val seen: HashSet<String> = hashSetOf()

    plugins.forEach { pluginDef ->
        if (seen.contains(pluginDef.id.name)) {
            errors.add(pluginDef.id.ctx, IdentifierAlreadyExists(toError(pluginDef.id)))
        } else {
            seen.add(pluginDef.id.name)
        }

        val pluginSymbol = ParameterizedStaticPluginSymbol(scope, pluginDef.id, PluginInstantiationValidation(), true)
        if (pluginDef.typeParams.isNotEmpty()) {
            val seenTypeParameters: MutableSet<String> = HashSet()
            pluginSymbol.typeParams = pluginDef.typeParams.map { typeParamDef ->
                if (typeParamDef.type == TypeParameterKind.Fin) {
                    val typeParam = FinTypeParameter(
                        qualifiedName(fileName, pluginDef.id, typeParamDef.identifier),
                        typeParamDef.identifier
                    )
                    val postFix = typeParamDef.identifier.name
                    if (seenTypeParameters.contains(postFix)) {
                        errors.add(
                            typeParamDef.identifier.ctx,
                            DuplicateTypeParameter(toError(typeParamDef.identifier))
                        )
                    } else {
                        seenTypeParameters.add(postFix)
                        pluginSymbol.defineType(typeParamDef.identifier, typeParam)
                    }
                    typeParam
                } else {
                    val typeParam = StandardTypeParameter(
                        qualifiedName(fileName, pluginDef.id, typeParamDef.identifier),
                        typeParamDef.identifier
                    )
                    if (seenTypeParameters.contains(typeParamDef.identifier.name)) {
                        errors.add(
                            typeParamDef.identifier.ctx,
                            DuplicateTypeParameter(toError(typeParamDef.identifier))
                        )
                    } else {
                        seenTypeParameters.add(typeParamDef.identifier.name)
                        pluginSymbol.defineType(typeParamDef.identifier, typeParam)
                    }
                    typeParam
                }
            }
        }
    }

    if (errors.toSet().isNotEmpty()) {
        throw LanguageException(errors.toSet())
    }

    return res.toList()
}

internal fun createPluginScope(pluginSource: String): SymbolTable {
    val res = SymbolTable(Lang.prelude)

    if (pluginSource.isNotEmpty()) {
        val plugins = processPlugins("plugins", pluginSource, res)
        plugins.forEach {
            res.define(it.identifier, it)
        }
    }

    return res
}
