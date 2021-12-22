package com.cablemc.pokemoncobbled.client.gui.summary.widgets.pages.moves

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.components.Button
import net.minecraft.network.chat.TextComponent
import net.minecraft.resources.ResourceLocation

/**
 * This Button is specifically made for the Summary to change the order of the Moves
 *
 * The blocked var was added to prevent the switching the order of Buttons triggering another switch
 */
class MovesMoveButton(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    private val pTextureWidth: Int, private val pTextureHeight: Int,
    private val isUp: Boolean,
    onPress: OnPress
): Button(pX, pY, pWidth, pHeight, TextComponent("MoveButton"), onPress) {

    companion object {
        private val upButtonResource = ResourceLocation(PokemonCobbled.MODID, "ui/summary/summary_moves_overlay_swap_up.png")
        private val downButtonResource = ResourceLocation(PokemonCobbled.MODID, "ui/summary/summary_moves_overlay_swap_down.png")
        private var blocked = false
    }

    override fun renderButton(pMatrixStack: PoseStack, pMouseX: Int, pMouseY: Int, pPartialTicks: Float) {
        if(isHovered) {
            if(isUp) {
                RenderSystem.setShaderTexture(0, upButtonResource)
                blit(pMatrixStack, x - 4, y - 29, 0F, 0F, pTextureWidth, pTextureHeight, pTextureWidth, pTextureHeight)
            } else {
                RenderSystem.setShaderTexture(0, downButtonResource)
                blit(pMatrixStack, x - 4, y - 41, 0F, 0F, pTextureWidth, pTextureHeight, pTextureWidth, pTextureHeight)
            }
        }
    }

    override fun onRelease(pMouseX: Double, pMouseY: Double) {
        blocked = false
    }

    override fun onClick(pMouseX: Double, pMouseY: Double) {
        if(!blocked) {
            blocked = true
            onPress.onPress(this)
        }
    }
}