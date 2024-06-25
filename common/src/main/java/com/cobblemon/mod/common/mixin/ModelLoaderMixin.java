/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.mixin;

import com.cobblemon.mod.common.client.CobblemonBakingOverrides;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.InjectionPoint;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.points.BeforeStringInvoke;

@Mixin(ModelBakery.class)
public abstract class ModelLoaderMixin {

    @Shadow protected abstract void addModelToBake(ModelResourceLocation modelId, UnbakedModel unbakedModel);

    @Final
    @Shadow
    private Map<ResourceLocation, UnbakedModel> unbakedModels;

    @Final
    @Shadow
    private static Logger LOGGER;

    @Inject(
        method = "<init>",
        at = @At("TAIL")
    )
    public void init(BlockColors blockColors,
        ProfilerFiller profiler,
        Map jsonUnbakedModels,
        Map blockStates,
        CallbackInfo ci) {
        CobblemonBakingOverrides.INSTANCE.getModels().forEach(bakingOverride -> {
            try {
                JsonUnbakedModel unbakedModel = this.loadModelFromJson(bakingOverride.getModelLocation());
                this.unbakedModels.put(bakingOverride.getModelIdentifier().id(), unbakedModel);
                this.addModelToBake(bakingOverride.getModelIdentifier(), unbakedModel);
            } catch (IOException e) {
                LOGGER.error("Error loading a Cobblemon BakedModel:", e);
                throw new RuntimeException(e);
            }
        });
    }

    @Shadow
    private BlockModel loadModelFromJson(ResourceLocation id) throws IOException {
        return null;
    }
}
