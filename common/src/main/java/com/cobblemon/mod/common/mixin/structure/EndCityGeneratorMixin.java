package com.cobblemon.mod.common.mixin.structure;

import com.cobblemon.mod.common.Cobblemon;
import net.minecraft.structure.EndCityGenerator;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndCityGenerator.Piece.class)
public class EndCityGeneratorMixin {
    @Inject(method = "handleMetadata", at = @At(value = "HEAD"), cancellable = true)
    private void cobblemon$cancelShulkerSpawns(String metadata, BlockPos pos, ServerWorldAccess world, Random random, BlockBox boundingBox, CallbackInfo ci) {
        if (!Cobblemon.config.getDoVanillaSpawns() && metadata.startsWith("Sentry")) {
            ci.cancel();
        }
    }
}
