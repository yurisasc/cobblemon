package com.cablemc.pokemoncobbled.common.client.gui.pc

import com.cablemc.pokemoncobbled.common.api.gui.blitk
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.storage.ClientPC
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.TranslatableText

class PCGui(
    private val pc: ClientPC
) : Screen(TranslatableText("pokemoncobbled.ui.pc.title")) {

    companion object {
        // Size of Background
        private const val backgroundHeight = 200
        private const val backgroundWidth = 300
        // Textures
        private val background = cobbledResource("ui/pc/pc_base.png")
        private val underlay = cobbledResource("ui/pc/pc_underlay.png")
    }

    override fun init() {
        val x = (width - backgroundWidth) / 2
        val y = (height - backgroundHeight) / 2

        // Add Exit Button
        addDrawableChild(
            ExitButton(
                pX = x + 248, pY = y + 3,
                pWidth = 18, pHeight = 13,
                pXTexStart = 0, pYTexStart = 0, pYDiffText = 0
            ) {
                MinecraftClient.getInstance().setScreen(null)
            })

        // Add Party
        addDrawableChild(
            PCWidget(
                pX = x + 116, pY = y + 32,
                pWidth = 175, pHeight = 145,
                pcGui = this,
                pc = pc
            )
        )

        super.init()
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(matrices)

        val x = (width - backgroundWidth) / 2
        val y = (height - backgroundHeight) / 2

        // Rendering PC Underlay
        blitk(
            matrixStack = matrices,
            texture = underlay,
            x = x + 7, y = y + 25,
            width = 65, height = 65
        )

        // Rendering UI Background
        blitk(
            matrixStack = matrices,
            texture = background,
            x = x, y = y,
            width = backgroundWidth, height = backgroundHeight
        )

        super.render(matrices, mouseX, mouseY, delta)
    }

}