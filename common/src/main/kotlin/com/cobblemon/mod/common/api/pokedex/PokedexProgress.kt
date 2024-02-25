
/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.pokedex

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.PrimitiveCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.util.StringIdentifiable

/**
 * Contains stats about a specific pokemon for putting in the pokedex
 *
 * @author JPAK, Apion
 * @since February 21, 2024
 */
enum class PokedexProgress : StringIdentifiable {
    NONE,
    ENCOUNTERED,
    CAUGHT;
    override fun asString(): String {
        return this.name
    }
    companion object {
        val CODEC: Codec<PokedexProgress> = RecordCodecBuilder.create { instance ->
            instance.group(
                PrimitiveCodec.STRING.fieldOf("name").forGetter { it.name }
            ).apply(instance, PokedexProgress::valueOf)

        }
    }
}
