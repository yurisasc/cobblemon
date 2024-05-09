/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.battle.subscreen

import com.cobblemon.mod.common.battles.ForfeitActionResponse
import com.cobblemon.mod.common.client.battle.SingleActionRequest
import com.cobblemon.mod.common.client.gui.battle.BattleGUI
import com.cobblemon.mod.common.client.gui.battle.widgets.BattleOptionTile
import com.cobblemon.mod.common.util.battleLang
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext

class ForfeitConfirmationSelection(
    battleGUI: BattleGUI,
    request: SingleActionRequest
) : BattleActionSelection(
    battleGUI,
    request,
    x = 12,
    y = 12,
    width = 250,
    height = 100,
    battleLang("ui.forfeit_confirmation")
) {
    init {
        BattleBackButton(x - 3, MinecraftClient.getInstance().window.scaledHeight - 22) {
            battleGUI.changeActionSelection(null)
            playDownSound(MinecraftClient.getInstance().soundManager)
        }.also { addWidget(it) }

        val x = (MinecraftClient.getInstance().window.scaledWidth / 2) - (BattleOptionTile.OPTION_WIDTH / 2)
        val y = (MinecraftClient.getInstance().window.scaledHeight / 2) - (BattleOptionTile.OPTION_HEIGHT / 2)

        BattleOptionTile(battleGUI, x, y, BattleGUI.runResource, battleLang("ui.forfeit")) {
            battleGUI.selectAction(request, ForfeitActionResponse())
            playDownSound(MinecraftClient.getInstance().soundManager)
        }.also { addWidget(it) }
    }

    override fun renderWidget(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        if (opacity <= 0.05F) {
            return
        }
        // TODO: use super call to prevent rendering widgets
    }
}