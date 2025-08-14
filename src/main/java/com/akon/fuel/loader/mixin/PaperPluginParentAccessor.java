package com.akon.fuel.loader.mixin;

import io.papermc.paper.plugin.entrypoint.classloader.PaperPluginClassLoader;
import io.papermc.paper.plugin.provider.type.paper.PaperPluginParent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PaperPluginParent.class)
public interface PaperPluginParentAccessor {

    @Accessor("classLoader")
    PaperPluginClassLoader getClassLoader();
}
