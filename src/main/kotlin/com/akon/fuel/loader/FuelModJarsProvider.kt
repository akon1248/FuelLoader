package com.akon.fuel.loader

import com.mojang.logging.LogUtils
import space.vectrix.ignite.Ignite
import space.vectrix.ignite.mod.ModContainer
import java.io.File
import java.io.IOException
import java.nio.file.FileSystems
import java.nio.file.Path
import kotlin.io.path.isRegularFile

object FuelModJarsProvider {

    const val LAUNCHER_LOCATOR = "launcher_locator"
    const val GAME_LOCATOR = "game_locator"

    private val LOGGER = LogUtils.getClassLogger()

    fun getModJars(): Sequence<Pair<ModContainer, Path>> =
        Ignite.mods()
            .containers()
            .asSequence()
            .map { Pair(it, it.resource()) }
            .filter { (_, resource) -> resource.locator() != LAUNCHER_LOCATOR && resource.locator() != GAME_LOCATOR }
            .map { (container, resource) -> Pair(container, resource.path()) }

    @JvmStatic
    fun getPluginModJarsArray(): Array<File> =
        getModJars()
            .filter { (_, path) ->
                try {
                    FileSystems.newFileSystem(path).use { fs ->
                        fs.getPath("/paper-plugin.yml").isRegularFile()
                    }
                } catch (e: IOException) {
                    LOGGER.warn("Failed to read jar file $path", e)
                    false
                }
            }
            .map { it.second.toFile() }
            .toList()
            .toTypedArray()
}
