import io.papermc.paperweight.userdev.ReobfArtifactConfiguration

plugins {
    kotlin("jvm") version "2.1.20"
    id("com.gradleup.shadow") version "8.3.0"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.18"
    id("maven-publish")
}

group = "com.akon"
version = "1.0"

val include: Configuration by configurations.creating

configurations.api {
    extendsFrom(include)
}

repositories {
    mavenCentral()
    maven(url = "https://repo.purpurmc.org/snapshots")
}

dependencies {
    implementation("space.vectrix.ignite:ignite-api:1.1.0")
    implementation("org.spongepowered:mixin:0.8.5")
    implementation("io.github.llamalad7:mixinextras-common:0.3.5")
    paperweight.devBundle(group = "org.purpurmc.purpur", version = "1.21.8-R0.1-SNAPSHOT")
    include(kotlin("stdlib-jdk8"))
    include(kotlin("reflect"))
    include("net.neoforged:bus:8.0.5") {
        exclude(group = "org.apache.logging.log4j", module = "log4j-api")
        exclude(group = "org.ow2.asm", module = "asm")
    }
}

val targetJavaVersion = 21

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    withSourcesJar()
}

kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks {
    withType<JavaCompile> {
        options.release.set(targetJavaVersion)
    }
    shadowJar {
        configurations = listOf(include)
        from(jar)
    }
    processResources {
        val properties = mapOf("version" to project.version)
        filteringCharset = "UTF-8"
        filesMatching(listOf("ignite.mod.json", "paper-plugin.yml")) {
            expand(properties)
        }
    }
}

artifacts {
    archives(tasks.shadowJar)
}
paperweight.reobfArtifactConfiguration = ReobfArtifactConfiguration.MOJANG_PRODUCTION
