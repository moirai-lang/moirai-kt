package moirai.composition

import moirai.semantics.core.LanguageErrors
import moirai.semantics.core.LanguageException
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

    if (errors.toSet().isNotEmpty()) {
        throw LanguageException(errors.toSet())
    }

    return pluginsParseTreeListener.listPlugins()
}