package com.cablemc.pokemoncobbled.common.client.keybind

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.KeyMapping

/**
 * An extensions for Minecraft's [KeyMapping]
 * When creating a new [CobbledKeyMapping] [onPress] will be called when the key is pressed.
 *
 * @author Qu
 * @since 2022-02-17
 */
abstract class CobbledKeyMapping(
    name: String,
    type: InputConstants.Type = InputConstants.Type.KEYSYM,
    key: Int,
    category: String
): KeyMapping(name, type, key, category) {

    abstract fun onPress()

    open fun onKeyInput() {
        if (this.consumeClick())
            onPress()
    }
}