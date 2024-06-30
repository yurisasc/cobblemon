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
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to give us a hook to render the PartyOverlay below the Chat
 *
 * @author Qu
 * @since 2022-02-22
 */
@Mixin(InGameHud.class)
public class GuiMixin {
    private Long lastTimeMillis = null;

    @Inject(method = "renderMiscOverlays", at = @At("HEAD"))
    private void beforeChatHook(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (lastTimeMillis != null) {
            CobblemonClient.INSTANCE.beforeChatRender(context, (System.currentTimeMillis() - lastTimeMillis) / 1000F * 20);
        }
        lastTimeMillis = System.currentTimeMillis();
    }
}
