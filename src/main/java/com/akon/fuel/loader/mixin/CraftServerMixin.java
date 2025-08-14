package com.akon.fuel.loader.mixin;

import com.akon.fuel.loader.DummyPlugin;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(CraftServer.class)
public abstract class CraftServerMixin {

    @Inject(method = "reload", at = @At("HEAD"))
    private void blockReloading(CallbackInfo ci) {
        if (Arrays.stream(Bukkit.getPluginManager().getPlugins())
            .anyMatch(plugin -> plugin instanceof DummyPlugin)
        ) {
            throw new IllegalStateException(DummyPlugin.RELOADING_DISABLED_MESSAGE);
        }
    }
}
