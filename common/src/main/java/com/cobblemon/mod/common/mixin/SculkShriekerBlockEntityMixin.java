package com.cobblemon.mod.common.mixin;

import com.cobblemon.mod.common.Cobblemon;
import net.minecraft.block.entity.SculkShriekerBlockEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SculkShriekerBlockEntity.class)
public class SculkShriekerBlockEntityMixin {
    @Inject(method = "canWarn", at = @At(value = "HEAD"), cancellable = true)
    private void cobblemon$cancelWardenSpawn(ServerWorld world, CallbackInfoReturnable<Boolean> cir) {
        if (!Cobblemon.config.getDoVanillaSpawns()) {
            cir.setReturnValue(false);
        }
    }
}
