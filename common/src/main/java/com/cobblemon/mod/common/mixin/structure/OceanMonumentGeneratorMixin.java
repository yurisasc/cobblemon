package com.cobblemon.mod.common.mixin.structure;

import com.cobblemon.mod.common.Cobblemon;
import net.minecraft.structure.OceanMonumentGenerator;
import net.minecraft.util.math.BlockBox;
import net.minecraft.world.StructureWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OceanMonumentGenerator.Piece.class)
public class OceanMonumentGeneratorMixin {
    @Inject(method = "spawnElderGuardian", at = @At(value = "HEAD"), cancellable = true)
    private void cobblemon$cancelElderGuardianSpawn(StructureWorldAccess world, BlockBox box, int x, int y, int z, CallbackInfo ci) {
        if (!Cobblemon.config.getDoVanillaSpawns()) {
            ci.cancel();
        }
    }
}
