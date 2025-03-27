package com.akon.fuel.loader

import com.akon.fuel.loader.entrypoint.Entrypoint
import com.mojang.logging.LogUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import java.nio.file.FileSystems
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import java.util.stream.Stream
import kotlin.streams.asStream

object FuelModInitializer {

    private val LOGGER = LogUtils.getClassLogger()

    @JvmStatic
    fun initializeMods() {
        FuelModJarsProvider.getModJars()
            .asStream()
            .parallel()
            .flatMap { (container, jar) ->
                locateEntrypoint(jar)?.let { Stream.of(Triple(container, jar, it)) } ?: Stream.empty()
            }
            .sequential()
            .forEach { (container, jar, entrypointName) ->
                LOGGER.info("Initializing mod {} with the entrypoint {}", jar.fileName, entrypointName)
                val entrypoint =
                    try {
                        val clazz = Class.forName(entrypointName)
                        clazz.kotlin.objectInstance ?: clazz.getConstructor().newInstance()
                    } catch (e: Exception) {
                        val msg = when (e) {
                            is ClassNotFoundException ->
                                "the entrypoint class $entrypointName not found"
                            is NoSuchMethodException ->
                                "an entrypoint class must be an object or have a no-arg constructor"
                            else -> null
                        }
                        LOGGER.error(
                            "Failed to initialize mod ${jar.fileName}" + (if (msg == null) "" else ": $msg"),
                            e
                        )
                        return@forEach
                    }
                (entrypoint as Entrypoint).initialize(container)
            }
    }

    private fun locateEntrypoint(jar: Path): String? {
        FileSystems.newFileSystem(jar).use { fs ->
            val entrypoints = mutableListOf<String>()
            Files.walkFileTree(fs.getPath("/"), object : SimpleFileVisitor<Path>() {
                override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                    val path = file.toString()
                    if (!path.endsWith(".class") || path.endsWith("/module-info.class")) {
                        return FileVisitResult.CONTINUE
                    }
                    Files.newInputStream(file).use { input ->
                        ClassReader(input).accept(
                            object : ClassVisitor(Opcodes.ASM9) {
                                override fun visit(
                                    version: Int,
                                    access: Int,
                                    name: String,
                                    signature: String?,
                                    superName: String?,
                                    interfaces: Array<out String>?
                                ) {
                                    if (path == "/$name.class" &&
                                        interfaces != null &&
                                        Type.getInternalName(Entrypoint::class.java) in interfaces
                                    ) {
                                        entrypoints.add(name.replace('/', '.'))
                                    }
                                    super.visit(version, access, name, signature, superName, interfaces)
                                }
                            },
                            0
                        )
                    }
                    return FileVisitResult.CONTINUE
                }
            })
            if (entrypoints.isEmpty()) return null
            if (entrypoints.size > 1) {
                LOGGER.warn(
                    "Multiple entrypoints found in the mod {}: {}",
                    jar.fileName,
                    entrypoints.joinToString(", ")
                )
            }
            return entrypoints.first()
        }
    }
}
