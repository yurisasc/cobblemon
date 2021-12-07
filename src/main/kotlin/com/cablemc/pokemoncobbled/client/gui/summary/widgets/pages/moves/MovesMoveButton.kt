package com.cablemc.pokemoncobbled.client.gui.summary.widgets.pages.moves

import net.minecraft.client.gui.components.Button
import net.minecraft.network.chat.Component

class MovesMoveButton(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    private val component: Component,
    onPress: OnPress
): Button(pX, pY, pWidth, pHeight, component, onPress) {

}