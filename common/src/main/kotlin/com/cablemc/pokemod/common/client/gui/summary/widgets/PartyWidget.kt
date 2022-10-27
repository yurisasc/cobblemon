/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client.gui.summary.widgets

import com.cablemc.pokemod.common.client.gui.summary.Summary
import com.cablemc.pokemod.common.pokemon.Pokemon
import java.security.InvalidParameterException
import kotlin.math.roundToInt
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
class PartyWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    val isParty: Boolean,
    val summary: Summary,
    private val pokemonList: List<Pokemon?>
) : SoundlessWidget(pX, pY + 7, pWidth, pHeight, Text.literal("PartyOverlay")) {

    private val partySize = pokemonList.size
    private val partyWidgets = arrayListOf<PartyMemberWidget>()

    init {
        if (partySize > 6 || partySize < 1)
            throw InvalidParameterException("Invalid party size")
        this.pokemonList.forEachIndexed { index, pokemon ->
            var y = this.y
            if (index != 0) {
                y += (index * PARTY_BOX_HEIGHT_DIFF + index * -0.5).roundToInt()
            }
            PartyMemberWidget(
                x = this.x, y = y,
                summary = this.summary,
                pokemon = pokemon,
                index = index,
                isClientPartyMember = this.isParty,
                partySize = this.partySize
            ).also { widget ->
                this.addWidget(widget)
                this.partyWidgets.add(widget)
            }
        }
    }

    override fun render(pMatrixStack: MatrixStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        this.partyWidgets.forEach { widget -> widget.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks) }
    }

    companion object {

        private const val PARTY_BOX_HEIGHT_DIFF = 29

    }

}