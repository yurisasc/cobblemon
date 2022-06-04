package com.cablemc.pokemoncobbled.common.client.gui.battle.subscreen

import com.cablemc.pokemoncobbled.common.client.battle.SingleActionRequest
import com.cablemc.pokemoncobbled.common.client.gui.battle.BattleGUI
import com.cablemc.pokemoncobbled.common.util.battleLang
import net.minecraft.client.gui.Selectable
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.util.math.MatrixStack

class BattleSwitchPokemonSelection(
    battleGUI: BattleGUI,
    request: SingleActionRequest
) : BattleActionSelection(
    battleGUI,
    request,
    10,
    10,
    100,
    100,
    battleLang("switch_pokemon")
) {

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {

        TODO("Not yet implemented")
    }

    override fun appendNarrations(builder: NarrationMessageBuilder) {
        TODO("Not yet implemented")
    }

    override fun getType() = Selectable.SelectionType.HOVERED
}