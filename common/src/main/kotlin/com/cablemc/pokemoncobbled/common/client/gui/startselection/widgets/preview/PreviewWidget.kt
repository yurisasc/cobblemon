package com.cablemc.pokemoncobbled.common.client.gui.startselection.widgets.preview

import com.cablemc.pokemoncobbled.common.api.gui.ParentWidget
import com.cablemc.pokemoncobbled.common.config.starter.StarterCategory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text

class PreviewWidget(
    val starterCategory: StarterCategory,
    val pX: Int, val pY: Int,
    val pWidth: Int, val pHeight: Int,
) : ParentWidget(pX, pY, pWidth, pHeight, Text.literal("PreviewWidget")) {

    var currentModel = 0
    companion object {

    }

    init {
    }
    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
    }


}