/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.battle.subscreen

import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.battles.*
import com.cobblemon.mod.common.client.CobblemonClient
import com.cobblemon.mod.common.client.battle.SingleActionRequest
import com.cobblemon.mod.common.client.gui.battle.BattleGUI
import com.cobblemon.mod.common.client.gui.battle.BattleOverlay
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.battleLang
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Selectable
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.widget.PressableWidget
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.client.sound.SoundManager
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.MathHelper.ceil
class BattleSwitchPokemonSelection(
    battleGUI: BattleGUI,
    request: SingleActionRequest
) : BattleActionSelection(
    battleGUI,
    request,
    x = 12,
    y = ceil((MinecraftClient.getInstance().window.scaledHeight / 2) - (((SWITCH_TILE_HEIGHT * 3) + (SWITCH_TILE_VERTICAL_SPACING * 2)) / 2)),
    width = 250,
    height = 100,
    battleLang("switch_pokemon")
) {
    companion object {
        const val SWITCH_TILE_WIDTH = BattleOverlay.TILE_WIDTH
        const val SWITCH_TILE_HEIGHT = BattleOverlay.TILE_HEIGHT
        const val SWITCH_TILE_HORIZONTAL_SPACING = 10F
        const val SWITCH_TILE_VERTICAL_SPACING = 5F
    }

    init {
        val pendingActionRequests = CobblemonClient.battle!!.pendingActionRequests
        val switchingInPokemon = pendingActionRequests.mapNotNull { it.response }.filterIsInstance<SwitchActionResponse>().map { it.newPokemonId }
        val showdownPokemonToPokemon = request.side!!.pokemon
            .mapNotNull { showdownPokemon ->
                battleGUI.actor!!.pokemon
                    .find { it.uuid == showdownPokemon.uuid }
                    ?.let { showdownPokemon to it }
            }
            .filter {
                if (request.side.pokemon[0].reviving) {
                    "fnt" in it.first.condition
                } else {
                    "fnt" !in it.first.condition
                }
            }
            .filter { it.second.uuid !in battleGUI.actor!!.activePokemon.map { it.battlePokemon?.uuid } }
            .filter { it.second.uuid !in switchingInPokemon }

        showdownPokemonToPokemon.forEachIndexed { index, (showdownPokemon, pokemon) ->
            val row = index / 2
            val column = index % 2

            val x = this.x.toFloat() + column * (SWITCH_TILE_HORIZONTAL_SPACING + SWITCH_TILE_WIDTH)
            val y = this.y.toFloat() + row * (SWITCH_TILE_VERTICAL_SPACING + SWITCH_TILE_HEIGHT)

            SwitchTile(
                this, x.toInt(), y.toInt(),
                pokemon, showdownPokemon
            ).also { addWidget(it) }
        }

        BattleBackButton(x - 3F, MinecraftClient.getInstance().window.scaledHeight - 22F ) {
            battleGUI.changeActionSelection(null)
            playDownSound(MinecraftClient.getInstance().soundManager)
        }.also { addWidget(it) }
    }

    override fun renderWidget(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        if (opacity <= 0.05F) {
            return
        }
        // TODO: make widgets not render if opacity is low
    }

    override fun playDownSound(soundManager: SoundManager) {
        soundManager.play(PositionedSoundInstance.master(CobblemonSounds.GUI_CLICK, 1.0F))
    }

    class SwitchTile(
        val selection: BattleSwitchPokemonSelection,
        x: Int,
        y: Int,
        val pokemon: Pokemon,
        val showdownPokemon: ShowdownPokemon
    ) : PressableWidget(x, y, SWITCH_TILE_WIDTH, SWITCH_TILE_HEIGHT, pokemon.getDisplayName()) {
        override fun renderButton(context: DrawContext, mouseX: Int, mouseY: Int, deltaTicks: Float) {
            val healthRatioSplits = showdownPokemon.condition.split(" ")[0].split("/")
            try {
                val (hp, maxHp) = if (healthRatioSplits.size == 1) {
                    0 to 0
                } else {
                    healthRatioSplits[0].toInt() to pokemon.hp
                }
                CobblemonClient.battleOverlay.drawBattleTile(
                    context = context,
                    x = x.toFloat(),
                    y = y.toFloat(),
                    reversed = false,
                    species = pokemon.species,
                    level = pokemon.level,
                    aspects = pokemon.aspects,
                    displayName = pokemon.getDisplayName(),
                    gender = pokemon.gender,
                    status = pokemon.status?.status,
                    maxHealth = maxHp,
                    health = hp.toFloat(),
                    isFlatHealth = true,
                    state = null,
                    colour = null,
                    opacity = selection.opacity,
                    partialTicks = deltaTicks
                )
            } catch (exception: Exception) {
                throw exception
            }
        }

        override fun onPress() {
            playDownSound(MinecraftClient.getInstance().soundManager)
            selection.battleGUI.selectAction(selection.request, SwitchActionResponse(pokemon.uuid))
        }

        override fun appendClickableNarrations(builder: NarrationMessageBuilder) {
        }
    }
}