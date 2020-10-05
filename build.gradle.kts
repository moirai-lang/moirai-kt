plugins {
    java
    `maven-publish`
}

group = "com.tsikhe"
version = "0.1"

repositories {
    mavenCentral()
}

publishing {
    publications {
        create<MavenPublication>("default") {
            from(components["java"])
        }
    }
}