package com.cobblemon.mod.common.mixin;

import com.cobblemon.mod.common.sherds.CobblemonSherds;
import net.minecraft.block.DecoratedPotPatterns;
import net.minecraft.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DecoratedPotPatterns.class)
public abstract class DecoratedPotPatternsMixin {
    @Inject(method = "registerAndGetDefault", at=@At("TAIL"))
    private static void cobblemon$registerCobblemonSherds(Registry<String> registry, CallbackInfoReturnable<String> cir) {

    }
}
