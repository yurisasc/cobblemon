package com.cablemc.pokemoncobbled.client.gui.pokenav

import net.minecraft.client.gui.components.ImageButton
import net.minecraft.resources.ResourceLocation

class PositionAwareImageButton(
    val posX: Int, val posY: Int,
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    pXTexStart: Int, pYTexStart: Int, pYDiffText: Int,
    resourceLocation: ResourceLocation, pTextureWidth: Int, pTextureHeight: Int,
    onPress: OnPress
): ImageButton(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, pYDiffText, resourceLocation, pTextureWidth, pTextureHeight, onPress) {

}