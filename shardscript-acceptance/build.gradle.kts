import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.61"
}

group = "com.tsikhe"
version = "0.1"

repositories {
    jcenter()
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.tsikhe:shardscript-semantics:1.0")
    implementation("com.tsikhe:shardscript-composition:1.0")
    implementation("com.tsikhe:shardscript-eval:1.0")
    implementation("junit:junit:4.12")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs += "-Xuse-experimental=kotlin.Experimental"
}