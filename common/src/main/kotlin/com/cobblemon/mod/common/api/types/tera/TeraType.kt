/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.types.tera

import com.cobblemon.mod.common.api.data.ShowdownIdentifiable
import com.cobblemon.mod.common.api.registry.RegistryElement
import com.cobblemon.mod.common.api.resistance.Resistible
import com.cobblemon.mod.common.registry.CobblemonRegistries
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import net.minecraft.network.chat.Component
import java.util.function.Function

/**
 * The representation of the Pokémons Tera type.
 *
 * @see [Bulbapedia](https://bulbapedia.bulbagarden.net/wiki/Terastal_phenomenon)
 *
 */
interface TeraType : RegistryElement<TeraType>, ShowdownIdentifiable, Resistible {

    /**
     * The display name of this type.
     */
    fun displayName(): Component

    fun codec(): Codec<out TeraType>

    fun mapCodec(): MapCodec<out TeraType>

    companion object {

        @JvmStatic
        val CODEC: Codec<TeraType> get() = CobblemonRegistries.TERA_TYPE
            .byNameCodec()
            .dispatch(Function.identity(), TeraType::mapCodec)
    }

}