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
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.client.CobblemonResources
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.net.messages.server.pokemon.update.SetNicknamePacket
import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.client.util.InputUtil
import net.minecraft.client.util.math.MatrixStack
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
        this.pokemon = pokemon
        this.lastSavedName = pokemon.nickname?.string
        setChangedListener {
            pokemon.nickname = Text.literal(it)
        }
        text = pokemon.getDisplayName().string
    }

    override fun setFocused(focused: Boolean) {
        super.setFocused(focused)
        val oldText = text.trim()
        text = text.trim().ifBlank { pokemon.species.translatedName.string }
        if (!focused) {
            if (oldText != lastSavedName) {
                lastSavedName = text
                CobblemonNetwork.sendToServer(
                    SetNicknamePacket(
                        pokemonUUID = pokemon.uuid,
                        nickname = text,
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