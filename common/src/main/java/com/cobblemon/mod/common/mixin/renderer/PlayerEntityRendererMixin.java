/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.mixin.renderer;


import com.cobblemon.mod.common.client.render.player.MountedPlayerRenderer;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author landonjw
 */
@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {
    @Inject(
            method = "setupTransforms(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/client/util/math/MatrixStack;FFFF)V",
            at = @At("HEAD")
    )
    private void doABibarelRoll$modifyRoll(AbstractClientPlayerEntity player, MatrixStack poseStack, float a, float b, float partialTicks, float i, CallbackInfo ci) {
        if (player.hasVehicle()) {
            var vehicle = player.getVehicle();
            if (vehicle instanceof PokemonEntity entity) {
                MountedPlayerRenderer.INSTANCE.render(player, entity, poseStack);
            }
        }
    }
}