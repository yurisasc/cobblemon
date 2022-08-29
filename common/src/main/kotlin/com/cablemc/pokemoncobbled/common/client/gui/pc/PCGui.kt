package com.cablemc.pokemoncobbled.common.client.gui.pc

import com.cablemc.pokemoncobbled.common.api.gui.ColourLibrary
import com.cablemc.pokemoncobbled.common.api.gui.blitk
import com.cablemc.pokemoncobbled.common.client.CobbledResources
import com.cablemc.pokemoncobbled.common.client.render.drawScaledText
import com.cablemc.pokemoncobbled.common.client.storage.ClientPC
import com.cablemc.pokemoncobbled.common.client.storage.ClientParty
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text

class PCGui(
    private val pc: ClientPC,
    private val party: ClientParty
) : Screen(Text.translatable("pokemoncobbled.ui.pc.title")) {

    companion object {
        // Size of Background
        private const val backgroundHeight = 200
        private const val backgroundWidth = 300
        // Textures
        private val background = cobbledResource("ui/pc/pc_base.png")
        private val underlay = cobbledResource("ui/pc/pc_underlay.png")
    }

    private lateinit var pcWidget: PCWidget

    override fun init() {
        val x = (width - backgroundWidth) / 2
        val y = (height - backgroundHeight) / 2

        // Add Exit Button
        this.addDrawableChild(
            ExitButton(
                pX = x + 249, pY = y + 4,
                pWidth = 16, pHeight = 12,
                pXTexStart = 0, pYTexStart = 0, pYDiffText = 0
            ) {
                MinecraftClient.getInstance().setScreen(null)
            })

        // Add Forward Button
        this.addDrawableChild(
            NavigationButton(
                pX = x + 230, pY = y + 9,
                pWidth = 9, pHeight = 14,
                pXTexStart = 0, pYTexStart = 0, pYDiffText = 0,
                forward = true
            ) {
                pcWidget.box += 1
            })

        // Add Backwards Button
        this.addDrawableChild(
            NavigationButton(
                pX = x + 108, pY = y + 9,
                pWidth = 9, pHeight = 14,
                pXTexStart = 0, pYTexStart = 0, pYDiffText = 0,
                forward = false
            ) {
                pcWidget.box -= 1
            })

        // Add PC
        this.pcWidget = PCWidget(
            pX = x + 116, pY = y + 32,
            pWidth = 175, pHeight = 145,
            pcGui = this,
            pc = pc,
            party = party
        )
        this.addDrawableChild(pcWidget)
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

        drawScaledText(
            matrixStack = matrices,
            font = CobbledResources.DEFAULT_LARGE,
            text = Text.translatable("pokemoncobbled.ui.box.title", (this.pcWidget.box + 1).toString()),
            x = (x + 173.5), y = (y + 8.75),
            colour = ColourLibrary.WHITE, shadow = false,
            centered = true,
            scale = 1.5f,
        )

        super.render(matrices, mouseX, mouseY, delta)
    }

    /**
     * Whether this Screen should pause the Game in SinglePlayer
     */
    override fun shouldPause(): Boolean {
        return false
    }

}