package com.akon.fuel.loader.mixin;

import com.akon.fuel.loader.FuelModInitializer;
import com.akon.fuel.event.FuelEvents;
import com.akon.fuel.event.PostBootstrapEvent;
import joptsimple.OptionSet;
import net.minecraft.server.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Main.class)
public abstract class MainMixin {

    @Inject(method = "main", at = @At("HEAD"))
    private static void initialize(OptionSet optionset, CallbackInfo ci) {
        FuelModInitializer.initializeMods();
    }

    @Inject(
        method = "main",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/Bootstrap;validate()V",
            shift = At.Shift.AFTER
        )
    )
    private static void postBootstrap(OptionSet optionset, CallbackInfo ci) {
        FuelEvents.BUS.post(PostBootstrapEvent.INSTANCE);
    }
}
