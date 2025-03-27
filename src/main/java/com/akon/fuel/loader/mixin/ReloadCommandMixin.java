package com.akon.fuel.loader.mixin;

import com.akon.fuel.loader.DummyPlugin;
import com.llamalad7.mixinextras.sugar.Local;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.ReloadCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ReloadCommand.class)
public abstract class ReloadCommandMixin {

    @Inject(
        method = "execute",
        at = @At(
            value = "INVOKE",
            target = "Ljava/lang/IllegalStateException;getMessage()Ljava/lang/String;"
        ),
        cancellable = true
    )
    private void blockReloading(
        CommandSender sender,
        String currentAlias,
        String[] args,
        CallbackInfoReturnable<Boolean> cir,
        @Local IllegalStateException ex
    ) {
        if (ex.getMessage().equals(DummyPlugin.RELOADING_DISABLED_MESSAGE)) {
            Command.broadcastCommandMessage(sender, ChatColor.RED + DummyPlugin.RELOADING_DISABLED_MESSAGE);
            cir.setReturnValue(true);
        }
    }
}
