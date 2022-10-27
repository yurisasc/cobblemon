/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client.gui.battle

import com.cablemc.pokemod.common.battles.ShowdownActionResponse
import com.cablemc.pokemod.common.client.PokemodClient
import com.cablemc.pokemod.common.client.battle.ClientBattleActor
import com.cablemc.pokemod.common.client.battle.SingleActionRequest
import com.cablemc.pokemod.common.client.gui.battle.subscreen.BattleActionSelection
import com.cablemc.pokemod.common.client.gui.battle.subscreen.BattleGeneralActionSelection
import com.cablemc.pokemod.common.client.gui.battle.subscreen.BattleSwitchPokemonSelection
import com.cablemc.pokemod.common.client.gui.battle.widgets.BattleMessagePane
import com.cablemc.pokemod.common.client.keybind.currentKey
import com.cablemc.pokemod.common.client.keybind.keybinds.PartySendBinding
import com.cablemc.pokemod.common.client.render.drawScaledText
import com.cablemc.pokemod.common.util.battleLang
import com.cablemc.pokemod.common.util.pokemodResource
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
class BattleGUI : Screen(battleLang("gui.title")) {
    companion object {
        const val OPTION_VERTICAL_SPACING = 3
        const val OPTION_HORIZONTAL_SPACING = 3
        const val OPTION_ROOT_X = 12
        const val OPTION_VERTICAL_OFFSET = 85

        val fightResource = pokemodResource("ui/battle/battle_menu_fight.png")
        val bagResource = pokemodResource("ui/battle/battle_menu_bag.png")
        val switchResource = pokemodResource("ui/battle/battle_menu_switch.png")
        val runResource = pokemodResource("ui/battle/battle_menu_run.png")
    }

    var opacity = 0F
    val actor = PokemodClient.battle?.side1?.actors?.find { it.uuid == MinecraftClient.getInstance().player?.uuid }

    var queuedActions = mutableListOf<() -> Unit>()

    override fun init() {
        super.init()
        addDrawableChild(BattleMessagePane(this, PokemodClient.battle!!.messages))
    }

    fun changeActionSelection(newSelection: BattleActionSelection?) {
        val current = children().find { it is BattleActionSelection }
        queuedActions.add {
            current?.let(this::remove)
            if (newSelection != null) {
                addDrawableChild(newSelection)
            }
        }
    }

    fun getCurrentActionSelection() = children().filterIsInstance<BattleActionSelection>().firstOrNull()

    fun selectAction(request: SingleActionRequest, response: ShowdownActionResponse) {
        val battle = PokemodClient.battle ?: return
        if (request.response == null) {
            request.response = response
            changeActionSelection(null)
            battle.checkForFinishedChoosing()
        }
    }

    override fun render(poseStack: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        opacity = PokemodClient.battleOverlay.opacityRatio.toFloat()

        queuedActions.forEach { it() }
        queuedActions.clear()
        super.render(poseStack, mouseX, mouseY, delta)
        val battle = PokemodClient.battle
        if (battle == null) {
            close()
            return
        } else if (PokemodClient.battleOverlay.opacityRatio <= 0.1 && PokemodClient.battle?.minimised == true) {
            close()
            return
        }

        if (actor != null) {
            if (battle.mustChoose) {
                if (getCurrentActionSelection() == null) {
                    val unanswered = battle.getFirstUnansweredRequest()
                    if (unanswered != null) {
                        changeActionSelection(deriveRootActionSelection(actor, unanswered))
                    }
                }
            } else if (getCurrentActionSelection() != null) {
                changeActionSelection(null)
            }
        }

        val currentSelection = getCurrentActionSelection()
        if (currentSelection == null || currentSelection is BattleGeneralActionSelection ) {
            drawScaledText(
                matrixStack = poseStack,
                text = battleLang("ui.hide_label", PartySendBinding.currentKey().localizedText),
                x = MinecraftClient.getInstance().window.scaledWidth / 2,
                y = (MinecraftClient.getInstance().window.scaledHeight / 2) - 25,
                opacity = 0.75F * opacity,
                centered = true
            )
        }


        queuedActions.forEach { it() }
        queuedActions.clear()
    }

    fun deriveRootActionSelection(actor: ClientBattleActor, request: SingleActionRequest): BattleActionSelection {
        return if (request.forceSwitch) {
            BattleSwitchPokemonSelection(this, request)
        } else {
            BattleGeneralActionSelection(this, request)
        }
    }

    override fun shouldPause() = false
    override fun close() {
        super.close()
        PokemodClient.battle?.minimised = true
    }

    override fun charTyped(chr: Char, modifiers: Int): Boolean {
        if (chr.toString() == PartySendBinding.currentKey().localizedText.string && PokemodClient.battleOverlay.opacity == BattleOverlay.MAX_OPACITY) {
            val battle = PokemodClient.battle ?: return false
            battle.minimised = !battle.minimised
            return true
        }
        return super.charTyped(chr, modifiers)
    }
}