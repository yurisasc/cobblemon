/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.mixin.riding;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.JumpingMount;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ClientPlayerEntity.class)
public class JetMixin {

    @Shadow private int field_3938;
    @Shadow private float mountJumpStrength;

    private float jetStrength;

    @Redirect(method = "tickMovement", at = @At(target = "Lnet/minecraft/entity/JumpingMount;getJumpCooldown()I", value = "INVOKE"))
    public int cobblemon$tryHandlingJet(JumpingMount mount) {
        if(mount instanceof PokemonEntity pokemon) {
//            if(pokemon.getDataTracker().get(PokemonEntity.MOVING)) {
//                ++this.field_3938;
//                if (this.field_3938 < 11) {
//                    this.jetStrength = (float) this.field_3938 * 0.1F;
//                } else {
                    this.mountJumpStrength = 0.8F + 2.0F / (float) (this.field_3938 - 9) * 0.1F;
//                }
//            } else {
//                this.field_3938 = 0;
//                this.jetStrength = 0;
//            }

//            return -1;
        }

        return 0;
    }

    @Inject(
            method = "tickMovement",
            at = @At(
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;mountJumpStrength:F",
                    value = "FIELD",
                    ordinal = 4,
                    shift = At.Shift.AFTER
            ),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    public void cobblemon$applyCachedJumpStrength(CallbackInfo ci) {
        this.mountJumpStrength = this.jetStrength;
    }

}
