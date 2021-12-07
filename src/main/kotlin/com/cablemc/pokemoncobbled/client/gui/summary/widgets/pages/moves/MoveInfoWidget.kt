package com.cablemc.pokemoncobbled.client.gui.summary.widgets.pages.moves

import com.cablemc.pokemoncobbled.client.gui.summary.mock.PokemonMove
import com.cablemc.pokemoncobbled.client.gui.summary.widgets.SoundlessWidget
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiComponent
import net.minecraft.network.chat.TextComponent

class MoveInfoWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    val move: PokemonMove
): SoundlessWidget(pX, pY, pWidth, pHeight, TextComponent(move.name)) {

    private val font = Minecraft.getInstance().font

    override fun render(pMatrixStack: PoseStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {

        GuiComponent.drawString(pMatrixStack, font, move.power.toString(), 0, 0, 0xFFFFFF)

        super.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks)
    }
}