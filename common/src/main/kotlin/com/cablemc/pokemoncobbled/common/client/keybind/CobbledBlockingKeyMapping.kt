package com.cablemc.pokemoncobbled.common.client.keybind

import com.mojang.blaze3d.platform.InputConstants

/**
 * An extension for the [CobbledKeyMapping] to prevent the [onPress] from rapidly triggering when holding down the associated key
 *
 * @author Qu
 * @since 2022-02-23
 */
abstract class CobbledBlockingKeyMapping(
    name: String,
    type: InputConstants.Type = InputConstants.Type.KEYSYM,
    key: Int,
    category: String
) : CobbledKeyMapping(name, type, key, category) {
    private var wasDown = false

    override fun onKeyInput() {
        if (isDown && !wasDown) {
            wasDown = true
            onPress()
        } else if (!isDown)
            wasDown = false
    }
}