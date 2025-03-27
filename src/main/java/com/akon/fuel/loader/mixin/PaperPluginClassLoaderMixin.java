package com.akon.fuel.loader.mixin;

import com.akon.fuel.loader.interfaces.PaperPluginClassLoaderExtensions;
import io.papermc.paper.plugin.configuration.PluginMeta;
import io.papermc.paper.plugin.entrypoint.classloader.PaperPluginClassLoader;
import io.papermc.paper.plugin.entrypoint.classloader.PaperSimplePluginClassLoader;
import org.spongepowered.asm.mixin.Mixin;

import java.io.IOException;
import java.nio.file.Path;
import java.security.ProtectionDomain;
import java.util.jar.JarFile;

@Mixin(PaperPluginClassLoader.class)
public abstract class PaperPluginClassLoaderMixin
    extends PaperSimplePluginClassLoader
    implements PaperPluginClassLoaderExtensions {

    public PaperPluginClassLoaderMixin(
        Path source,
        JarFile file,
        PluginMeta configuration,
        ClassLoader parentLoader
    ) throws IOException {
        super(source, file, configuration, parentLoader);
    }

    @Override
    public Class<?> fuel$defineClass(String className, byte[] b) {
        return super.defineClass(className, b, 0, b.length, (ProtectionDomain) null);
    }
}
