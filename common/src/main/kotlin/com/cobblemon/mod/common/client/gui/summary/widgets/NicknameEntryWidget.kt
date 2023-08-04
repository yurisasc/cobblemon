/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.summary.widgets

import com.cobblemon.mod.common.CobblemonNetwork
import com.cobblemon.mod.common.api.text.bold
import com.cobblemon.mod.common.client.CobblemonResources
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.net.messages.server.pokemon.update.SetNicknamePacket
import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.text.Text

class NicknameEntryWidget(
    var pokemon: Pokemon, x: Int, y: Int, width: Int, height: Int, val isParty: Boolean, text: Text
): TextFieldWidget(
    MinecraftClient.getInstance().textRenderer,
    x, y, width, height, text
) {
    companion object {
        private const val MAX_NAME_LENGTH = 12
    }

    var lastSavedName: String? = null

    init {
        setMaxLength(MAX_NAME_LENGTH)
        setSelectedPokemon(pokemon)
    }

    fun setSelectedPokemon(pokemon: Pokemon) {
        if (isFocused) {
            isFocused = false
        }
        this.pokemon = pokemon
        this.lastSavedName = pokemon.nickname?.string
        setChangedListener {
            pokemon.nickname = Text.literal(it)
        }
        text = pokemon.getDisplayName().string
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (mouseX.toInt() in x..(x + width) && mouseY.toInt() in y..(y + height)) {
            isFocused = true
            return true
        } else {
            return false
        }
    }

    override fun setFocused(focused: Boolean) {
        super.setFocused(focused)
        val oldText = text.trim()
        val pokemonName = pokemon.species.translatedName.string
        text = text.trim().ifBlank { pokemonName }
        if (!focused) {
            val newNickname = if (text == pokemonName) null else text
            if (oldText != lastSavedName && !(newNickname == null && pokemon.nickname == null)) {
                lastSavedName = text
                CobblemonNetwork.sendToServer(
                    SetNicknamePacket(
                        pokemonUUID = pokemon.uuid,
                        nickname = newNickname,
                        isParty = isParty
                    )
                )
            }
        }
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        if (cursor != text.length) setCursorToEnd()

        drawScaledText(
            context = context,
            font = CobblemonResources.DEFAULT_LARGE,
            text = Text.translatable(if (isFocused) "$text|" else text).bold(),
            x = x,
            y = y,
            shadow = true
        )
    }
}