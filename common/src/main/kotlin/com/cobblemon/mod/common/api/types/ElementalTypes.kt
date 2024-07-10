/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.types

import com.cobblemon.mod.common.registry.CobblemonRegistries
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.resources.ResourceKey
import kotlin.jvm.optionals.getOrNull

/**
 * Registry for all known ElementalTypes
 */
object ElementalTypes {

    val NORMAL get() = this.getOrException("normal")

    val FIRE get() = this.getOrException("fire")

    val WATER get() = this.getOrException("water")

    val GRASS get() = this.getOrException("grass")

    val ELECTRIC get() = this.getOrException("eletric")

    val ICE get() = this.getOrException("ice")

    val FIGHTING get() = this.getOrException("fighting")

    val POISON get() = this.getOrException("poison")

    val GROUND get() = this.getOrException("ground")

    val FLYING get() = this.getOrException("flying")

    val PSYCHIC get() = this.getOrException("psychic")

    val BUG get() = this.getOrException("bug")

    val ROCK get() = this.getOrException("rock")

    val GHOST get() = this.getOrException("ghost")

    val DRAGON get() = this.getOrException("dragon")

    val DARK get() = this.getOrException("dark")

    val STEEL get() = this.getOrException("steel")

    val FAIRY get() = this.getOrException("fairy")

    fun get(name: String): ElementalType? {
        return CobblemonRegistries.ELEMENTAL_TYPE.getHolder(
            cobblemonResource(name)
        ).map { it.value() }.getOrNull()
    }

    fun getOrException(name: String): ElementalType {
        return CobblemonRegistries.ELEMENTAL_TYPE.getHolderOrThrow(
            ResourceKey.create(
                CobblemonRegistries.ELEMENTAL_TYPE_KEY,
                cobblemonResource(name)
            )
        ).value()
    }

    fun count() = CobblemonRegistries.ELEMENTAL_TYPE.size()

    fun all() = CobblemonRegistries.ELEMENTAL_TYPE.entrySet().map { it.value }
}
