/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.mixin;

import com.cablemc.pokemod.common.client.keybind.PokemodKeybinds;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin notifying our keybinds registry to trigger actions on our keybinds
 *
 * @author Qu
 * @since 2022-02-17
 */
@Mixin(Keyboard.class)
public class KeyboardHandlerMixin {
    @Final
    @Shadow
    private MinecraftClient client;

    @Inject(
            method = "onKey",
            at = @At(
                    value = "TAIL",
                    target = "Lnet/minecraft/client/Keyboard;onKey(JIIII)V"
            )
    )
    public void keyPress(long l, int i, int j, int k, int m, CallbackInfo ci) {
        if (l == this.client.getWindow().getHandle()) {
            if (this.client.currentScreen == null) {
                PokemodKeybinds.INSTANCE.onAnyKey(i, j, k, m);
            }
        }
    }
}
