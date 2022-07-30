package com.cablemc.pokemoncobbled.common.client.gui.startselection.widgets.preview

import com.cablemc.pokemoncobbled.common.api.gui.ParentWidget
import com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.ModelWidget
import com.cablemc.pokemoncobbled.common.client.gui.summary.widgets.SoundlessWidget
import com.cablemc.pokemoncobbled.common.config.starter.StarterCategory
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.LiteralText

class PreviewWidget(
    val starterCategory: StarterCategory,
    val pX: Int, val pY: Int,
    val pWidth: Int, val pHeight: Int,
) : ParentWidget(pX, pY, pWidth, pHeight, LiteralText("PreviewWidget")) {

    var currentModel = 0
    companion object {

    }

    init {
    }
    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
    }


}