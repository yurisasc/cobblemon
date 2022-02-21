package com.cablemc.pokemoncobbled.fabric.mixin;

import com.cablemc.pokemoncobbled.fabric.client.FabricClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Inject(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/ResourceLoadStateTracker;startReload(Lnet/minecraft/client/ResourceLoadStateTracker$ReloadReason;Ljava/util/List;)V",
                    shift = At.Shift.BEFORE
            )
    )
    public void beforeInitialReloadHook(GameConfig gameConfig, CallbackInfo ci) {
        FabricClient.INSTANCE.beforeFirstResourceManagerReload();
    }
}
