package com.cablemc.pokemoncobbled.common.client.gui.startselection.widgets.preview

import com.cablemc.pokemoncobbled.common.api.gui.blitk
import com.cablemc.pokemoncobbled.common.client.CobbledResources
import com.cablemc.pokemoncobbled.common.client.gui.drawProfilePokemon
import com.cablemc.pokemoncobbled.common.client.gui.startselection.StarterSelectionScreen
import com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.ModelWidget
import com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.SoundlessWidget
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.LiteralText
import net.minecraft.util.math.Quaternion
import net.minecraft.util.math.Vec3f

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
    val starterSelectionScreen: StarterSelectionScreen
): SoundlessWidget(pX, pY, pWidth, pHeight, LiteralText("StarterRoundabout")) {

    companion object {
        const val MODEL_WIDTH = 58
        const val MODEL_HEIGHT = 58
    }

    lateinit var leftWidget: ModelWidget
    lateinit var middleWidget: ModelWidget
    lateinit var rightWidget: ModelWidget
    val minecraft = MinecraftClient.getInstance()

    init {

    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        matrices.push()
        RenderSystem.enableScissor(
            (x * minecraft.window.scaleFactor).toInt(),
            (minecraft.window.height - (y * minecraft.window.scaleFactor) - (height * minecraft.window.scaleFactor)).toInt(),
            (MODEL_WIDTH * minecraft.window.scaleFactor).toInt(),
            (MODEL_HEIGHT * minecraft.window.scaleFactor).toInt()
        )

        matrices.translate((x + width / 21.0), y.toDouble(), 0.0)
        matrices.scale(2.5F, 2.5F, 1F)

        drawProfilePokemon(
            pokemon = starterSelectionScreen.currentPokemon,
            matrixStack = matrices,
            rotation = Quaternion.fromEulerXyzDegrees(Vec3f(13F, 35F, 0F)),
            state = null,
            scale = 6F
        )

        RenderSystem.disableScissor()

        matrices.pop()
    }
}