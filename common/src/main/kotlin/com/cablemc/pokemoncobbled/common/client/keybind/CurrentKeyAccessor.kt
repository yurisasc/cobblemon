package com.cablemc.pokemoncobbled.common.client.keybind

import com.mojang.blaze3d.platform.InputConstants.Key
import net.minecraft.client.KeyMapping

/**
 * Interface for us to retrieve the current [Key] if it has been changed from the default [Key].
 *
 * @author Qu
 * @since 2022-02-17
 */
interface CurrentKeyAccessor {
    fun currentKey(): Key
}

fun KeyMapping.currentKey(): Key {
    return (this as CurrentKeyAccessor).currentKey()
}