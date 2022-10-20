/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.api.types

import net.minecraft.text.MutableText
import net.minecraft.text.Text

/**
 * Registry for all known ElementalTypes
 */
object ElementalTypes {

    private val allTypes = mutableListOf<ElementalType>()

    val NORMAL = register(
        name = "normal",
        displayName = Text.translatable("pokemod.type.normal"),
        hue = 0xDDDDCF,
        textureXMultiplier = 0
    )

    val FIRE = register(
        name = "fire",
        displayName = Text.translatable("pokemod.type.fire"),
        hue = 0xE55C32,
        textureXMultiplier = 1
    )

    val WATER = register(
        name = "water",
        displayName = Text.translatable("pokemod.type.water"),
        hue = 0x4A9BE8,
        textureXMultiplier = 2
    )

    val GRASS = register(
        name = "grass",
        displayName = Text.translatable("pokemod.type.grass"),
        hue = 0x4DBC3C,
        textureXMultiplier = 3
    )

    val ELECTRIC = register(
        name = "electric",
        displayName = Text.translatable("pokemod.type.electric"),
        hue = 0xEFD128,
        textureXMultiplier = 4
    )

    val ICE = register(
        name = "ice",
        displayName = Text.translatable("pokemod.type.ice"),
        hue = 0x6BC3EF,
        textureXMultiplier = 5
    )

    val FIGHTING = register(
        name = "fighting",
        displayName = Text.translatable("pokemod.type.fighting"),
        hue = 0xC44C5C,
        textureXMultiplier = 6
    )

    val POISON = register(
        name = "poison",
        displayName = Text.translatable("pokemod.type.poison"),
        hue = 0xA24BD8,
        textureXMultiplier = 7
    )

    val GROUND = register(
        name = "ground",
        displayName = Text.translatable("pokemod.type.ground"),
        hue = 0xD89950,
        textureXMultiplier = 8
    )

    val FLYING = register(
        name = "flying",
        displayName = Text.translatable("pokemod.type.flying"),
        hue = 0xBCC1FF,
        textureXMultiplier = 9
    )

    val PSYCHIC = register(
        name = "psychic",
        displayName = Text.translatable("pokemod.type.psychic"),
        hue = 0xD86AD6,
        textureXMultiplier = 10
    )

    val BUG = register(
        name = "bug",
        displayName = Text.translatable("pokemod.type.bug"),
        hue = 0xA2C831,
        textureXMultiplier = 11
    )

    val ROCK = register(
        name = "rock",
        displayName = Text.translatable("pokemod.type.rock"),
        hue = 0xAA9666,
        textureXMultiplier = 12
    )

    val GHOST = register(
        name = "ghost",
        displayName = Text.translatable("pokemod.type.ghost"),
        hue = 0x9572E5,
        textureXMultiplier = 13
    )

    val DRAGON = register(
        name = "dragon",
        displayName = Text.translatable("pokemod.type.dragon"),
        hue = 0x535DE8,
        textureXMultiplier = 14
    )

    val DARK = register(
        name = "dark",
        displayName = Text.translatable("pokemod.type.dark"),
        hue = 0x5C6CB2,
        textureXMultiplier = 15
    )

    val STEEL = register(
        name = "steel",
        displayName = Text.translatable("pokemod.type.steel"),
        hue = 0xC3CCE0,
        textureXMultiplier = 16
    )

    val FAIRY = register(
        name = "fairy",
        displayName = Text.translatable("pokemod.type.fairy"),
        hue = 0xEA727E,
        textureXMultiplier = 17
    )

    fun register(name: String, displayName: MutableText, hue: Int, textureXMultiplier: Int): ElementalType {
        return ElementalType(
            name = name,
            displayName = displayName,
            hue = hue,
            textureXMultiplier = textureXMultiplier
        ).also {
            allTypes.add(it)
        }
    }

    fun register(elementalType: ElementalType): ElementalType {
        allTypes.add(elementalType)
        return elementalType
    }

    fun get(name: String): ElementalType? {
        return allTypes.firstOrNull { type -> type.name.equals(name, ignoreCase = true) }
    }

    fun getOrException(name: String): ElementalType {
        return allTypes.first { type -> type.name.equals(name, ignoreCase = true) }
    }

    fun count() = allTypes.size
}
