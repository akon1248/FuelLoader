package com.akon.fuel.loader.mixin;

import com.akon.fuel.loader.FuelModPackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.repository.ServerPacksSource;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Arrays;

@Mixin(ServerPacksSource.class)
public abstract class ServerPacksSourceMixin {

    @ModifyArg(
        method = "createPackRepository(Ljava/nio/file/Path;Lnet/minecraft/world/level/validation/DirectoryValidator;)Lnet/minecraft/server/packs/repository/PackRepository;",
        at = @At(
            value = "INVOKE",
            opcode = Opcodes.NEW,
            target = "Lnet/minecraft/server/packs/repository/PackRepository;<init>(Lnet/minecraft/world/level/validation/DirectoryValidator;[Lnet/minecraft/server/packs/repository/RepositorySource;)V"
        ),
        index = 1
    )
    private static RepositorySource[] addPacksSource(RepositorySource[] providers) {
        var newProviders = Arrays.copyOf(providers, providers.length + 1);
        newProviders[providers.length] = FuelModPackSource.INSTANCE;
        return newProviders;
    }
}
