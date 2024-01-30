plugins {
    kotlin("jvm") version "1.9.0"
    antlr
}

group = "org.shardscript"
version = "0.4.6"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    antlr("org.antlr:antlr4:4.7")
    implementation("org.apache.commons:commons-lang3:3.0")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

tasks.compileKotlin {
    dependsOn(tasks.generateGrammarSource)
}

tasks.generateGrammarSource {
    arguments = arguments + listOf("-visitor", "-package", "org.shardscript.grammar")
    outputDirectory = File("$buildDir/generated-src/antlr/main/org/shardscript/grammar")
}