package com.cablemc.pokemoncobbled.common.client.gui.battle.subscreen

import com.cablemc.pokemoncobbled.common.api.gui.ParentWidget
import com.cablemc.pokemoncobbled.common.client.battle.SingleActionRequest
import com.cablemc.pokemoncobbled.common.client.gui.battle.BattleGUI
import net.minecraft.text.MutableText

abstract class BattleActionSelection(
    val battleGUI: BattleGUI,
    val request: SingleActionRequest,
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    name: MutableText
) : ParentWidget(x, y, width, height, name) {
    val opacity: Float
        get() = battleGUI.opacity
}