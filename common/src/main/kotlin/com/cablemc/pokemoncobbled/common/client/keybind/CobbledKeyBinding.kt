package com.cablemc.pokemoncobbled.common.client.keybind

import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil

/**
 * An extensions for Minecraft's [KeyBinding]
 * When creating a new [CobbledKeyBinding] [onPress] will be called when the key is pressed.
 *
 * @author Qu
 * @since 2022-02-17
 */
abstract class CobbledKeyBinding(
    name: String,
    type: InputUtil.Type = InputUtil.Type.KEYSYM,
    key: Int,
    category: String
): KeyBinding(name, type, key, category) {

    abstract fun onPress()

    open fun onKeyInput() {
        if (this.wasPressed()) {
            onPress()
        }
    }
}