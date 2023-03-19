package com.cobblemon.mod.common.mixin;

import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.Registerable;
import net.minecraft.structure.PlainsVillageData;
import net.minecraft.structure.pool.StructurePool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlainsVillageData.class)
public class PlainsVillageDataMixin {


    @Inject(method = "bootstrap", at = @At("RETURN"))
    private static void afterBootstrap(Registerable<StructurePool> poolRegisterable, CallbackInfo ci) {
        System.out.println("LOOK HERE: ");
        System.out.println(BuiltinRegistries.REGISTRY_BUILDER);
    }}
