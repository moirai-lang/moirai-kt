plugins {
    antlr
    `maven-publish`
}

group = "com.tsikhe"
version = "0.2.2"

repositories {
    mavenCentral()
}

dependencies {
    antlr("org.antlr:antlr4:4.7")
}

publishing {
    publications {
        create<MavenPublication>("default") {
            from(components["java"])
        }
    }
}

tasks.generateGrammarSource {
    arguments = arguments + listOf("-visitor", "-package", "com.tsikhe.shardscript.grammar")
}
