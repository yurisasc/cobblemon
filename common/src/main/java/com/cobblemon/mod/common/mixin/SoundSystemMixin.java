/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.mixin;

import com.google.common.collect.Multimap;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundSystem.class)
public abstract class SoundSystemMixin {

    @Shadow
    private Multimap<SoundCategory, SoundInstance> sounds;

    @Shadow
    protected abstract void stop(SoundInstance sound);

    @Inject(method = "stopSounds(Lnet/minecraft/util/Identifier;Lnet/minecraft/sound/SoundCategory;)V", at = @At("HEAD"), cancellable = true)
    public void stopSounds(@Nullable Identifier id, @Nullable SoundCategory category, CallbackInfo cb) {
        if (id == null && category != null) {
            sounds.get(category).forEach(this::stop);
            cb.cancel();
        }
    }
}
