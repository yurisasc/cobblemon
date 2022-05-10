package com.cablemc.pokemoncobbled.common.client.gui.battle

import com.cablemc.pokemoncobbled.common.util.battleLang
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack

class BattleGUI : Screen(battleLang("gui.title")) {
    override fun render(poseStack: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(poseStack, mouseX, mouseY, delta)

    }
}