/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.pasture

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.client.gui.drawProfilePokemon
import com.cobblemon.mod.common.client.gui.summary.widgets.EvolutionSelectScreen
import com.cobblemon.mod.common.util.cobblemonResource
import com.cobblemon.mod.common.util.lang
import com.cobblemon.mod.common.util.math.fromEulerXYZDegrees
import java.util.UUID
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import org.joml.Quaternionf
import org.joml.Vector3f

class PasturePokemonEntry(
    val pokemonId: UUID,
    var entityKnown: Boolean,
    val name: Text,
    val species: Identifier,
    val aspects: Set<String>,
) : AlwaysSelectedEntryListWidget.Entry<PasturePokemonEntry>() {
    companion object {
        val entryBackground = cobblemonResource("textures/gui/pasture/pasture_base.png")
    }

    override fun getNarration() = lang("pasture.entry")
    override fun render(matrices: MatrixStack, index: Int, y: Int, x: Int, entryWidth: Int, entryHeight: Int, mouseX: Int, mouseY: Int, hovered: Boolean, tickDelta: Float) {
        blitk(
            matrixStack = matrices,
            texture = entryBackground,
            x = x,
            y = y,
            height = entryHeight,
            width = entryWidth
        )

        // Render Pokémon
        matrices.push()
        matrices.translate(x + (EvolutionSelectScreen.PORTRAIT_DIAMETER / 2) + 65.0, y - 5.0, 0.0)
        matrices.scale(2.5F, 2.5F, 1F)
        drawProfilePokemon(
            species = species,
            aspects = aspects,
            matrixStack = matrices,
            rotation = Quaternionf().fromEulerXYZDegrees(Vector3f(13F, 35F, 0F)),
            state = null,
            scale = 6F
        )
        matrices.pop()
    }
}