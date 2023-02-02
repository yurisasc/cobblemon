/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.startselection.widgets.preview

import com.cobblemon.mod.common.client.gui.drawProfilePokemon
import com.cobblemon.mod.common.client.gui.summary.widgets.SoundlessWidget
import com.cobblemon.mod.common.pokemon.RenderablePokemon
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.util.math.RotationAxis
import org.joml.Vector3f

/**
 * The current/next/previous pokemon display thingy
 *
 * Very good name
 *
 * @author Qu
 * @since 2022-07-30
 */
class StarterRoundabout(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    var pokemon: RenderablePokemon
): SoundlessWidget(pX, pY, pWidth, pHeight, Text.literal("StarterRoundabout")) {

    companion object {
        const val MODEL_WIDTH = 30
        const val MODEL_HEIGHT = 30
    }

    val minecraft = MinecraftClient.getInstance()

    init {

    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        matrices.push()
        /*
         * This correction term is due to where scaling comes from in a render. We are giving the drawProfilePokemon
         * a different scale to usual, which means our position offsets that were used in the summary GUI (which is
         * what was used to calibrate those offsets) are slightly off. In this specific case, -3 on the Y axis is
         * enough to correct this deviation.
         *
         * If you want to up the scale, then you'll also need to change the correction term (trial and error it)
         * - Hiro
         */
        val correctionTerm = -3.0
        matrices.translate(x.toDouble() + MODEL_WIDTH / 2.0, y.toDouble() - MODEL_HEIGHT.toDouble() + correctionTerm, 0.0)

        // This uses more weird x and y because the component is in an abnormal position, could fix it but also who cares at this point
        DrawableHelper.enableScissor(
            x,
            y - MODEL_HEIGHT,
            x + MODEL_WIDTH,
            y
        )
        drawProfilePokemon(
            renderablePokemon = pokemon,
            matrixStack = matrices,
            rotation = RotationAxis.of(Vector3f(13F, 35F, 0F)).rotationDegrees(90F),
            state = null,
            scale = 18F
        )

        DrawableHelper.disableScissor()

        matrices.pop()
    }
}