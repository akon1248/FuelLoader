package com.akon.fuel.loader

import com.mojang.logging.LogUtils
import net.minecraft.SharedConstants
import net.minecraft.network.chat.Component
import net.minecraft.server.packs.FilePackResources
import net.minecraft.server.packs.PackLocationInfo
import net.minecraft.server.packs.PackSelectionConfig
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.repository.Pack
import net.minecraft.server.packs.repository.PackSource
import net.minecraft.server.packs.repository.RepositorySource
import java.nio.file.FileSystems
import java.util.Optional
import java.util.function.Consumer
import kotlin.io.path.isRegularFile

object FuelModPackSource : RepositorySource {

    private val LOGGER = LogUtils.getClassLogger()

    override fun loadPacks(profileAdder: Consumer<Pack>) {
        FuelModJarsProvider.getModJars().forEach { (container, jar) ->
            FileSystems.newFileSystem(jar, mapOf<String, Any?>()).use { fs ->
                if (!fs.getPath("/pack.mcmeta").isRegularFile()) return@forEach
            }
            val supplier = FilePackResources.FileResourcesSupplier(jar)
            val path = jar.toString()
            val info = PackLocationInfo(
                container.id(),
                Component.literal(path.substring(path.lastIndexOf('/') + 1)),
                PackSource.DEFAULT,
                Optional.empty()
            )
            val meta = Pack.readPackMetadata(
                info,
                supplier,
                SharedConstants.getCurrentVersion().packVersion(PackType.SERVER_DATA),
            )
            if (meta == null) {
                LOGGER.warn("Couldn't load pack from $jar")
                return@forEach
            }
            profileAdder.accept(Pack(info, supplier, meta, PackSelectionConfig(true, Pack.Position.BOTTOM, false)))
        }
    }
}
