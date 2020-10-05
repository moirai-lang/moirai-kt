plugins {
    antlr
}

group = "com.tsikhe"
version = "0.1"

repositories {
    mavenCentral()
}

dependencies {
    antlr("org.antlr:antlr4:4.7")
}

tasks.generateGrammarSource {
    arguments = arguments + listOf("-visitor", "-package", "com.tsikhe.shardscript.grammar")
}
