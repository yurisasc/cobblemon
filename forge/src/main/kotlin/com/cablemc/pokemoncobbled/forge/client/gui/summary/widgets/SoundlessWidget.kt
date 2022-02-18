package com.cablemc.pokemoncobbled.forge.client.gui.summary.widgets

import com.cablemc.pokemoncobbled.common.api.gui.ParentWidget
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