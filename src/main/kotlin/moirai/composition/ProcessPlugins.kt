package moirai.composition

import moirai.semantics.core.*
import moirai.semantics.prelude.Lang
import org.antlr.v4.runtime.tree.ParseTreeWalker

internal fun parsePlugins(fileName: String, pluginSource: String): List<PluginDefLiteral> {
    val parser = createParser(pluginSource)
    val parseTree = parser.grammar.file()

    val errors = LanguageErrors()
    val pluginsParseTreeListener = PluginsParseTreeListener(fileName, errors)

    ParseTreeWalker.DEFAULT.walk(pluginsParseTreeListener, parseTree)

    if (parser.listener.hasErrors()) {
        parser.listener.populateErrors(errors, fileName)
        if (errors.toSet().isNotEmpty()) {
            throw LanguageException(errors.toSet())
        }
    }

    val plugins = pluginsParseTreeListener.listPlugins()
    val seen: HashSet<String> = hashSetOf()

    plugins.forEach {
        if (seen.contains(it.id.name)) {
            errors.add(it.id.ctx, IdentifierAlreadyExists(toError(it.id)))
        } else {
            seen.add(it.id.name)
        }
    }

    if (errors.toSet().isNotEmpty()) {
        throw LanguageException(errors.toSet())
    }

    return plugins
}

internal fun processPlugins(fileName: String, pluginSource: String): List<ParameterizedStaticPluginSymbol> {
    val plugins = parsePlugins(fileName, pluginSource)
    val res: MutableList<ParameterizedStaticPluginSymbol> = mutableListOf()

    

    return res.toList()
}

internal fun createPluginScope(pluginSource: String): SymbolTable {
    val res = SymbolTable(Lang.prelude)

    if (pluginSource.isNotEmpty()) {
        val plugins = processPlugins("plugins", pluginSource)
        plugins.forEach {
            res.define(it.identifier, it)
        }
    }

    return res
}
