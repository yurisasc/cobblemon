package com.cablemc.pokemoncobbled.client.gui.summary.widgets

import com.cablemc.pokemoncobbled.client.gui.ParentWidget
import com.cablemc.pokemoncobbled.client.gui.summary.Summary
import net.minecraft.client.sounds.SoundManager
import net.minecraft.network.chat.Component

abstract class SoundlessWidget(
    pX: Int, pY: Int,
    pWidth: Int, pHeight: Int,
    component: Component
): ParentWidget(pX, pY, pWidth, pHeight, component) {
    /**
     * Do not play sounds when clicking, because clicking a Widget anywhere produces a sound... :(
     */
    override fun playDownSound(pHandler: SoundManager) {
    }

}