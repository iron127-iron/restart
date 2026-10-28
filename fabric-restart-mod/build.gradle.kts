plugins {
    id("fabric-loom") version "1.18.4"
    id("maven-publish")
}

base {
    archivesName = rootProject.name
}

version = "1.0.0"
group = "me.restart.mod"

repositories {
    maven {
        name = "Fabric"
        url = uri("https://maven.fabricmc.net/")
    }
    mavenCentral()
}

dependencies {
    // Minecraft
    minecraft("com.mojang:minecraft:1.21.11")
    mappings("net.fabricmc:yarn:1.21.11+build.6:v2")

    // Fabric Loader API (compileOnly for ModInitializer etc.)
    modCompileOnly("net.fabricmc:fabric-loader:0.15.9")

    // Fabric API
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.138.2+1.21.11")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

java {
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            url = layout.buildDirectory.dir("repos").get().asFile.toURI()
        }
    }
}
