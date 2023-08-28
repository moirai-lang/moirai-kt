plugins {
    kotlin("jvm") version "1.9.0"
}

group = "org.shardscript"
version = "0.2.3"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}