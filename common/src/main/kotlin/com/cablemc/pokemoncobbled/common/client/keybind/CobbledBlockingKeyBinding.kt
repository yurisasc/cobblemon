package com.cablemc.pokemoncobbled.common.client.keybind

import net.minecraft.client.util.InputUtil

/**
 * An extension for the [CobbledKeyBinding] to prevent the [onPress] from rapidly triggering when holding down the associated key
 *
 * @author Qu
 * @since 2022-02-23
 */
abstract class CobbledBlockingKeyBinding(
    name: String,
    type: InputUtil.Type = InputUtil.Type.KEYSYM,
    key: Int,
    category: String
) : CobbledKeyBinding(name, type, key, category) {
    private var wasDown = false

    override fun onKeyInput() {
        if (isPressed && !wasDown) {
            wasDown = true
            onPress()
        } else if (!isPressed)
            wasDown = false
    }
}