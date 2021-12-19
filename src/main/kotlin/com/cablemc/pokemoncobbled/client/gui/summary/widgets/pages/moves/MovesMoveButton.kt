package com.cablemc.pokemoncobbled.client.gui.summary.widgets.pages.moves

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.components.Button
import net.minecraft.network.chat.TextComponent
import net.minecraft.resources.ResourceLocation

class MovesMoveButton(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    onPress: OnPress
): Button(pX, pY, pWidth, pHeight, TextComponent("MoveButton"), onPress) {

    companion object {
        private val buttonResource = ResourceLocation(PokemonCobbled.MODID, "ui/summary/summary_moves_overlay_swap.png")
    }

    override fun renderButton(pMatrixStack: PoseStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        RenderSystem.setShaderTexture(0, buttonResource)
        blit(pMatrixStack, x, y, 0F, 0F, width, height, width, height)
    }

    override fun onPress() {
        super.onPress()
    }
}