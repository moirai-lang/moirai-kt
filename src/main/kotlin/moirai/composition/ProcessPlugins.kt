package moirai.composition

import moirai.semantics.core.IdentifierAlreadyExists
import moirai.semantics.core.LanguageErrors
import moirai.semantics.core.LanguageException
import moirai.semantics.core.toError
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