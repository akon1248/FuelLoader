package com.akon.fuel.loader.mixin;

import com.akon.fuel.loader.FuelModJarsProvider;
import io.papermc.paper.plugin.PluginInitializerManager;
import io.papermc.paper.plugin.provider.source.FileArrayProviderSource;
import io.papermc.paper.plugin.util.EntrypointUtil;
import joptsimple.OptionSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PluginInitializerManager.class)
public abstract class PluginInitializerManagerMixin {

    @Inject(method = "load",
        at = @At(
            value = "INVOKE",
            target = "Lio/papermc/paper/plugin/util/EntrypointUtil;registerProvidersFromSource(Lio/papermc/paper/plugin/provider/source/ProviderSource;Ljava/lang/Object;)V",
            shift = At.Shift.AFTER,
            ordinal = 1
        )
    )
    private static void loadDummyPlugins(OptionSet optionSet, CallbackInfo ci) {
        EntrypointUtil.registerProvidersFromSource(
            new FileArrayProviderSource(),
            FuelModJarsProvider.getPluginModJarsArray()
        );
    }
}
