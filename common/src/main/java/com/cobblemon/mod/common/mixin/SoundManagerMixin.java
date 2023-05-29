/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.mixin;

import com.cobblemon.mod.common.access.SoundManagerDuck;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundManager.class)
public class SoundManagerMixin implements SoundManagerDuck {

    private boolean[] blockedCategories = new boolean[SoundCategory.values().length];

    @Override
    public void toggleCategories(SoundCategory... categories) {
        for (SoundCategory category : categories) {
            int index = category.ordinal();
            boolean blocked = blockedCategories[index];
            blockedCategories[index] = !blocked;
        }
    }

    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At("HEAD"), cancellable = true)
    public void play(SoundInstance sound, CallbackInfo cb) {
        if (blockedCategories[sound.getCategory().ordinal()]) cb.cancel();
    }

    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;I)V", at = @At("HEAD"), cancellable = true)
    public void play(SoundInstance sound, int delay, CallbackInfo cb) {
        if (blockedCategories[sound.getCategory().ordinal()]) cb.cancel();
    }
}
