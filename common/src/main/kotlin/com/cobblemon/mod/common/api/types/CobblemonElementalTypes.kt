/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.types

import com.cobblemon.mod.common.api.registry.CobblemonRegistries
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.util.Identifier
import java.util.Optional

/**
 * The official [ElementalType]s and the ones the Cobblemon mod ships with.
 * These are not guaranteed to be present in a game instance as we can't control what datapacks may remove them.
 * It's also not safe to assume they function the exact same as they would in the official games, please check the various properties of [ElementalType].
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
object CobblemonElementalTypes {

    val BUG_KEY: Identifier = cobblemonResource("bug")
    val BUG: Optional<ElementalType> get() = Optional.ofNullable(CobblemonRegistries.ELEMENTAL_TYPE.get(BUG_KEY))

    val DARK_KEY: Identifier = cobblemonResource("dark")
    val DARK: Optional<ElementalType> get() = Optional.ofNullable(CobblemonRegistries.ELEMENTAL_TYPE.get(DARK_KEY))

    val DRAGON_KEY: Identifier = cobblemonResource("dragon")
    val DRAGON: Optional<ElementalType> get() = Optional.ofNullable(CobblemonRegistries.ELEMENTAL_TYPE.get(DRAGON_KEY))

    val ELECTRIC_KEY: Identifier = cobblemonResource("electric")
    val ELECTRIC: Optional<ElementalType> get() = Optional.ofNullable(CobblemonRegistries.ELEMENTAL_TYPE.get(ELECTRIC_KEY))

    val FAIRY_KEY: Identifier = cobblemonResource("fairy")
    val FAIRY: Optional<ElementalType> get() = Optional.ofNullable(CobblemonRegistries.ELEMENTAL_TYPE.get(FAIRY_KEY))

    val FIGHTING_KEY: Identifier = cobblemonResource("fighting")
    val FIGHTING: Optional<ElementalType> get() = Optional.ofNullable(CobblemonRegistries.ELEMENTAL_TYPE.get(FIGHTING_KEY))

    val FIRE_KEY: Identifier = cobblemonResource("fire")
    val FIRE: Optional<ElementalType> get() = Optional.ofNullable(CobblemonRegistries.ELEMENTAL_TYPE.get(FIRE_KEY))

    val FLYING_KEY: Identifier = cobblemonResource("flying")
    val FLYING: Optional<ElementalType> get() = Optional.ofNullable(CobblemonRegistries.ELEMENTAL_TYPE.get(FLYING_KEY))

    val GHOST_KEY: Identifier = cobblemonResource("ghost")
    val GHOST: Optional<ElementalType> get() = Optional.ofNullable(CobblemonRegistries.ELEMENTAL_TYPE.get(GHOST_KEY))

    val GRASS_KEY: Identifier = cobblemonResource("grass")
    val GRASS: Optional<ElementalType> get() = Optional.ofNullable(CobblemonRegistries.ELEMENTAL_TYPE.get(GRASS_KEY))

    val GROUND_KEY: Identifier = cobblemonResource("ground")
    val GROUND: Optional<ElementalType> get() = Optional.ofNullable(CobblemonRegistries.ELEMENTAL_TYPE.get(GROUND_KEY))

    val ICE_KEY: Identifier = cobblemonResource("ice")
    val ICE: Optional<ElementalType> get() = Optional.ofNullable(CobblemonRegistries.ELEMENTAL_TYPE.get(ICE_KEY))

    val NORMAL_KEY: Identifier = cobblemonResource("normal")
    val NORMAL: Optional<ElementalType> get() = Optional.ofNullable(CobblemonRegistries.ELEMENTAL_TYPE.get(NORMAL_KEY))

    val POISON_KEY: Identifier = cobblemonResource("poison")
    val POISON: Optional<ElementalType> get() = Optional.ofNullable(CobblemonRegistries.ELEMENTAL_TYPE.get(POISON_KEY))

    val PSYCHIC_KEY: Identifier = cobblemonResource("psychic")
    val PSYCHIC: Optional<ElementalType> get() = Optional.ofNullable(CobblemonRegistries.ELEMENTAL_TYPE.get(PSYCHIC_KEY))

    val ROCK_KEY: Identifier = cobblemonResource("rock")
    val ROCK: Optional<ElementalType> get() = Optional.ofNullable(CobblemonRegistries.ELEMENTAL_TYPE.get(ROCK_KEY))

    val STEEL_KEY: Identifier = cobblemonResource("steel")
    val STEEL: Optional<ElementalType> get() = Optional.ofNullable(CobblemonRegistries.ELEMENTAL_TYPE.get(STEEL_KEY))

    val WATER_KEY: Identifier = cobblemonResource("water")
    val WATER: Optional<ElementalType> get() = Optional.ofNullable(CobblemonRegistries.ELEMENTAL_TYPE.get(WATER_KEY))

}