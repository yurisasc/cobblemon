/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.client.gui.battle.subscreen

import com.cablemc.pokemoncobbled.common.battles.ShowdownPokemon
import com.cablemc.pokemoncobbled.common.battles.SwitchActionResponse
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.battle.SingleActionRequest
import com.cablemc.pokemoncobbled.common.client.gui.battle.BattleGUI
import com.cablemc.pokemoncobbled.common.client.gui.battle.BattleOverlay
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.util.battleLang
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.Selectable
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.util.math.MatrixStack
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

    val tiles = mutableListOf<SwitchTile>()
    val backButton = BattleBackButton(x - 3F, MinecraftClient.getInstance().window.scaledHeight - 22F )

    class SwitchTile(
        val selection: BattleSwitchPokemonSelection,
        val x: Float,
        val y: Float,
        val pokemon: Pokemon,
        val showdownPokemon: ShowdownPokemon
    ) {
        fun isHovered(mouseX: Double, mouseY: Double) = mouseX in x..(x + SWITCH_TILE_WIDTH) && mouseY in (y..(y + SWITCH_TILE_HEIGHT))
        fun render(matrices: MatrixStack, mouseX: Double, mouseY: Double, deltaTicks: Float) {
            PokemonCobbledClient.battleOverlay.drawBattleTile(
                matrices = matrices,
                x = x,
                y = y,
                reversed = false,
                species = pokemon.species,
                level = pokemon.level,
                aspects = pokemon.aspects,
                displayName = pokemon.species.translatedName,
                gender = pokemon.gender,
                hpRatio = pokemon.currentHealth.toFloat() / pokemon.hp,
                state = null,
                colour = null,
                opacity = selection.opacity
            )
        }
    }

    init {
        val pendingActionRequests = PokemonCobbledClient.battle!!.pendingActionRequests
        val switchingInPokemon = pendingActionRequests.mapNotNull { it.response }.filterIsInstance<SwitchActionResponse>().map { it.newPokemonId }
        val showdownPokemonToPokemon = request.side!!.pokemon
            .mapNotNull { showdownPokemon ->
                battleGUI.actor!!.pokemon
                    .find { it.uuid == showdownPokemon.uuid }
                    ?.let { showdownPokemon to it }
            }
            .filter { it.second.uuid !in battleGUI.actor!!.activePokemon.map { it.battlePokemon?.uuid } }
            .filter { it.second.uuid !in switchingInPokemon }

        showdownPokemonToPokemon.forEachIndexed { index, (showdownPokemon, pokemon) ->
            val row = index / 2
            val column = index % 2

            val x = this.x.toFloat() + column * (SWITCH_TILE_HORIZONTAL_SPACING + SWITCH_TILE_WIDTH)
            val y = this.y.toFloat() + row * (SWITCH_TILE_VERTICAL_SPACING + SWITCH_TILE_HEIGHT)

            tiles.add(SwitchTile(this, x, y, pokemon, showdownPokemon))
        }
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        if (opacity <= 0.05F) {
            return
        }
        tiles.forEach { it.render(matrices, mouseX.toDouble(), mouseY.toDouble(), delta) }
        backButton.render(matrices, mouseX, mouseY, delta)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (backButton.isHovered(mouseX, mouseY)) {
            battleGUI.changeActionSelection(null)
            playDownSound(MinecraftClient.getInstance().soundManager)
            return true
        }
        val clicked = tiles.find { it.isHovered(mouseX, mouseY) } ?: return false
        val pokemon = clicked.pokemon
        playDownSound(MinecraftClient.getInstance().soundManager)
        battleGUI.selectAction(request, SwitchActionResponse(pokemon.uuid))

        return true
    }

    override fun appendNarrations(builder: NarrationMessageBuilder) {
    }

    override fun getType() = Selectable.SelectionType.HOVERED
}