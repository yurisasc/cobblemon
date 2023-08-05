/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.interact.partyselect

import com.cobblemon.mod.common.api.gui.blitk
import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.client.CobblemonResources
import com.cobblemon.mod.common.client.gui.drawProfilePokemon
import com.cobblemon.mod.common.client.gui.summary.widgets.PartySlotWidget
import com.cobblemon.mod.common.client.render.drawScaledText
import com.cobblemon.mod.common.client.render.getDepletableRedGreen
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonFloatingState
import com.cobblemon.mod.common.client.render.renderScaledGuiItemIcon
import com.cobblemon.mod.common.util.asIdentifierDefaultingNamespace
import com.cobblemon.mod.common.util.cobblemonResource
import com.cobblemon.mod.common.util.lang
import com.cobblemon.mod.common.util.math.fromEulerXYZDegrees
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.ButtonWidget.NarrationSupplier
import net.minecraft.client.sound.SoundManager
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import org.joml.Quaternionf
import org.joml.Vector3f

class PartySlotButton(
    x: Int, y: Int,
    val pokemon: PokemonProperties,
    val hpRatio: Float,
    val heldItem: ItemStack,
    val enabled: Boolean = true,
    val parent: PartySelectGUI,
    onPress: PressAction
) : ButtonWidget(x, y, WIDTH, HEIGHT, Text.literal("Pokemon"), onPress, NarrationSupplier { "".text() }) {

    companion object {
        private val slotResource = cobblemonResource("textures/gui/summary/summary_party_slot.png")
        private val slotFaintedResource = cobblemonResource("textures/gui/summary/summary_party_slot_fainted.png")
        private val slotEmptyResource = cobblemonResource("textures/gui/summary/summary_party_slot_empty.png")

        const val WIDTH = 46
        const val HEIGHT = 27
    }

    var animationSeconds = 0F
    var lastTime = System.currentTimeMillis()
    val state = PokemonFloatingState()

    private val renderablePokemon = pokemon.asRenderablePokemon()

    override fun render(context: DrawContext, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        hovered = pMouseX >= x && pMouseY >= y && pMouseX < x + width && pMouseY < y + height && enabled
        val alpha = if (enabled) 1.0 else 0.5
        val matrices = context.matrices

        blitk(
            matrixStack = matrices,
            texture = if (hpRatio <= 0F) slotFaintedResource else slotResource,
            x = x,
            y = y,
            width = width,
            height = height,
            vOffset = if (hovered) height else 0,
            textureHeight = height * 2,
            alpha = alpha
        )

        context.matrices.push()
        context.matrices.translate(x.toDouble() + 13, y.toDouble() - 2, 0.0)

//        if (!hovered) {
//            state.reset()
//        }

        drawProfilePokemon(
            renderablePokemon = renderablePokemon,
            matrixStack = context.matrices,
            rotation = Quaternionf().fromEulerXYZDegrees(Vector3f(13F, 35F, 0F)),
            state = state,
            scale = 10F,
            partialTicks = if (!hovered) 0F else pPartialTicks
        )
        context.matrices.pop()

        val halfScale = 0.5F
        val ballIcon = cobblemonResource("textures/gui/ball/" + pokemon.pokeball!!.asIdentifierDefaultingNamespace().path + ".png")
        val ballHeight = 22
        blitk(
            matrixStack = matrices,
            texture = ballIcon,
            x = (x - 2) / halfScale,
            y = (y - 3) / halfScale,
            height = ballHeight,
            width = 18,
            textureHeight = ballHeight * 2,
            scale = halfScale
        )

        val status = pokemon.status
        if (hpRatio > 0F && status != null) {
            blitk(
                matrixStack = matrices,
                texture = cobblemonResource("textures/gui/party/status_$status.png"),
                x = x + 42,
                y = y + 5,
                height = 14,
                width = 4
            )
        }

        val barWidthMax = 37
        val barWidth = hpRatio * barWidthMax
        val (red, green) = getDepletableRedGreen(hpRatio)

        blitk(
            matrixStack = matrices,
            texture = CobblemonResources.WHITE,
            x = x + 4,
            y = y + 25,
            width = barWidth,
            height = 1,
            textureWidth = barWidth / hpRatio,
            uOffset = barWidthMax - barWidth,
            red = red * 0.8F,
            green = green * 0.8F,
            blue = 0.27F
        )

        if ("male" in pokemon.aspects || "female" in pokemon.aspects) {
            blitk(
                matrixStack = matrices,
                texture = if ("male" in pokemon.aspects) PartySlotWidget.genderIconMale else PartySlotWidget.genderIconFemale,
                x = (x + 40) / halfScale,
                y = (y + 20) / halfScale,
                height = 7,
                width = 5,
                scale = halfScale
            )
        }

        drawScaledText(
            context = context,
            text = lang("ui.lv.number", pokemon.level!!),
            x = x + 31,
            y = y + 13,
            centered = true,
            shadow = true,
            scale = halfScale
        )

        // Held Item
        if (!heldItem.isEmpty) {
            renderScaledGuiItemIcon(
                itemStack = heldItem,
                x = x + 14.0,
                y = y + 9.5,
                scale = 0.5,
                matrixStack = matrices
            )
        }

        // Pokémon Name
        val displayName = pokemon.nickname ?: renderablePokemon.species.translatedName
        drawScaledText(
            context = context,
            text = displayName.copy(),
            x = x + 4,
            y = y + HEIGHT - 7,
            scale = halfScale
        )
    }

    override fun playDownSound(soundManager: SoundManager) {}
}
