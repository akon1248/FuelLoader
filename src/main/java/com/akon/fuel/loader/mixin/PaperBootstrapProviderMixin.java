package com.akon.fuel.loader.mixin;

import com.akon.fuel.loader.entrypoint.DummyPluginBootstrap;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.papermc.paper.plugin.provider.type.paper.PaperPluginParent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PaperPluginParent.PaperBootstrapProvider.class)
public abstract class PaperBootstrapProviderMixin {

    @Shadow
    @Final
    PaperPluginParent this$0;

    @ModifyExpressionValue(
        method = "createInstance()Lio/papermc/paper/plugin/bootstrap/PluginBootstrap;",
        at = @At(
            value = "INVOKE",
            target = "Lio/papermc/paper/plugin/provider/util/ProviderUtil;loadClass(Ljava/lang/String;Ljava/lang/Class;Ljava/lang/ClassLoader;Ljava/lang/Runnable;)Ljava/lang/Object;"
        )
    )
    public <T> T loadClass(@NotNull T original) {
        if (original instanceof DummyPluginBootstrap bootstrap) {
            bootstrap.setClassLoader(((PaperPluginParentAccessor) this$0).getClassLoader());
        }
        return original;
    }
}
