/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.mixin;

import com.cobblemon.mod.common.client.render.shader.CobblemonShaders;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderStage;
import com.cobblemon.mod.common.api.scheduling.ClientTaskTracker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.resource.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Unique
    long lastTime = -1;

    @Inject(
            method = "render",
            at = @At(value = "TAIL")
    )
    public void render(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        long newTime = System.currentTimeMillis();
        // Don't play scheduled animations when the game is paused
        if (client.isPaused()) {
            lastTime = newTime;
            return;
        }

        if (lastTime != -1) {
            ClientTaskTracker.INSTANCE.update((newTime - lastTime) / 1000F);
        }
        lastTime = newTime;
    }

}
