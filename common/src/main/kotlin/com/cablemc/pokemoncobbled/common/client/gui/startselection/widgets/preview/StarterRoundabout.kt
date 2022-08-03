package com.cablemc.pokemoncobbled.common.client.gui.startselection.widgets.preview

import com.cablemc.pokemoncobbled.common.client.gui.drawProfilePokemon
import com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.SoundlessWidget
import com.cablemc.pokemoncobbled.common.pokemon.RenderablePokemon
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
    var pokemon: RenderablePokemon
): SoundlessWidget(pX, pY, pWidth, pHeight, LiteralText("StarterRoundabout")) {

    companion object {
        const val MODEL_WIDTH = 30
        const val MODEL_HEIGHT = 30
    }

    val minecraft = MinecraftClient.getInstance()

    init {

    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        matrices.push()
        RenderSystem.enableScissor(
            (x * minecraft.window.scaleFactor).toInt(),
            (minecraft.window.height - y * minecraft.window.scaleFactor).toInt(),
            (MODEL_WIDTH * minecraft.window.scaleFactor).toInt(),
            (MODEL_HEIGHT * minecraft.window.scaleFactor).toInt()
        )
        // Use a big red blit to debug whether you have the scissor placed correctly
//        blitk(
//            matrices,
//            CobbledResources.RED,
//            0, 0, 1000, 1000
//        )

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

        drawProfilePokemon(
            renderablePokemon = pokemon,
            matrixStack = matrices,
            rotation = Quaternion.fromEulerXyzDegrees(Vec3f(13F, 35F, 0F)),
            state = null,
            scale = 18F
        )

        RenderSystem.disableScissor()

        matrices.pop()
    }
}