package com.cablemc.pokemoncobbled.client.gui.summary.widgets

import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.Widget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.sounds.SoundManager
import net.minecraft.network.chat.Component

abstract class SoundlessWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    component: Component
): Widget, AbstractWidget(pX, pY, pWidth, pHeight, component) {
    /**
     * Do not play sounds when clicking, because clicking a Widget anywhere produces a sound... :(
     */
    override fun playDownSound(pHandler: SoundManager) {
    }

    /**
     * I don't even know exactly what this does...
     */
    override fun updateNarration(pNarrationElementOutput: NarrationElementOutput) {
    }
}