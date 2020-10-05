import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.61"
    `maven-publish`
}

group = "com.tsikhe"
version = "0.1"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.tsikhe:shardscript-grammar:1.0")
    implementation("com.tsikhe:shardscript-semantics:1.0")
    implementation("org.apache.commons:commons-text:1.8")
    implementation("junit:junit:4.12")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs += "-Xuse-experimental=kotlin.Experimental"
}

tasks.register<Jar>("sourcesJar") {
    from(sourceSets.main.get().allSource)
    archiveClassifier.set("sources")
}

publishing {
    publications {
        create<MavenPublication>("default") {
            from(components["java"])
            artifact(tasks["sourcesJar"])
        }
    }
}
