/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.fabric.mixin;

import com.cobblemon.mod.common.client.CobblemonClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to give us a hook to render the PartyOverlay below the Chat
 *
 * @author Qu
 * @since 2022-02-22
 */
@Mixin(InGameHud.class)
public class GuiMixin {
    @Inject(
            method = "render",
            at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/MinecraftClient;getProfiler()Lnet/minecraft/util/profiler/Profiler;",
                shift = At.Shift.BEFORE,
                ordinal = 0
            ),
            slice = @Slice(
                from = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/InGameHud;renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V"
                )
            )
    )
    private void beforeChatHook(DrawContext context, float f, CallbackInfo ci) {
        CobblemonClient.INSTANCE.beforeChatRender(context, f);
    }
}
