import io.papermc.paperweight.userdev.ReobfArtifactConfiguration

plugins {
    kotlin("jvm") version "2.1.20"
    id("com.gradleup.shadow") version "8.3.0"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.14"
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
}

dependencies {
    implementation("space.vectrix.ignite:ignite-api:1.1.0")
    implementation("org.spongepowered:mixin:0.8.5")
    implementation("io.github.llamalad7:mixinextras-common:0.3.5")
    paperweight.paperDevBundle("1.21.4-R0.1-SNAPSHOT")
    include(kotlin("stdlib-jdk8"))
    include(kotlin("reflect"))
    include("net.neoforged:bus:8.0.2") {
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

fun tryFindProperty(name: String): String {
    return project.findProperty(name) as? String ?: throw IllegalStateException("Property $name not found")
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/akon1248/FuelLoader")
            credentials {
                username = tryFindProperty("gpr.user")
                password = tryFindProperty("gpr.key")
            }
        }
    }
    publications {
        create<MavenPublication>("gpr") {
            from(components["java"])
        }
    }
}

paperweight.reobfArtifactConfiguration = ReobfArtifactConfiguration.MOJANG_PRODUCTION
