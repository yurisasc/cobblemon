package com.cablemc.pokemoncobbled.common.client.keybind

import com.mojang.blaze3d.platform.InputConstants

/**
 * [CobbledKeyMapping] extension if the action on press shall be something that is not
 * platform-agnostic
 *
 * @author Qu
 * @since 2022-02-17
 */
abstract class CobbledCustomKeyMapping(
    name: String,
    type: InputConstants.Type = InputConstants.Type.KEYSYM,
    key: Int,
    category: String
) : CobbledKeyMapping(name, type, key, category) {

    var run: Runnable? = null

    override fun onPress() {
        run?.run()
    }
}