package com.cablemc.pokemoncobbled.common.client.keybind

import net.minecraft.client.util.InputUtil


/**
 * [CobbledKeyBinding] extension if the action on press shall be something that is not
 * platform-agnostic
 *
 * @author Qu
 * @since 2022-02-17
 */
abstract class CobbledCustomKeyBinding(
    name: String,
    type: InputUtil.Type = InputUtil.Type.KEYSYM,
    key: Int,
    category: String
) : CobbledKeyBinding(name, type, key, category) {

    var run: Runnable? = null

    override fun onPress() {
        run?.run()
    }
}