package com.cablemc.pokemoncobbled.common.api.types

import net.minecraft.text.MutableText
import net.minecraft.text.Text

/**
 * Registry for all known ElementalTypes
 */
object ElementalTypes {

    private val allTypes = mutableListOf<ElementalType>()

    val NORMAL = register(
        name = "normal",
        displayName = Text.translatable("pokemoncobbled.type.normal"),
        hue = 0xC0C0C0,
        textureXMultiplier = 0
    )

    val FIRE = register(
        name = "fire",
        displayName = Text.translatable("pokemoncobbled.type.fire"),
        hue = 0xCD663A,
        textureXMultiplier = 1
    )

    val WATER = register(
        name = "water",
        displayName = Text.translatable("pokemoncobbled.type.water"),
        hue = 0x3575E9,
        textureXMultiplier = 2
    )

    val GRASS = register(
        name = "grass",
        displayName = Text.translatable("pokemoncobbled.type.grass"),
        hue = 0x46B446,
        textureXMultiplier = 3
    )

    val ELECTRIC = register(
        name = "electric",
        displayName = Text.translatable("pokemoncobbled.type.electric"),
        hue = 0xF0D25D,
        textureXMultiplier = 4
    )

    val ICE = register(
        name = "ice",
        displayName = Text.translatable("pokemoncobbled.type.ice"),
        hue = 0x9BE3EE,
        textureXMultiplier = 5
    )

    val FIGHTING = register(
        name = "fighting",
        displayName = Text.translatable("pokemoncobbled.type.fighting"),
        hue = 0xA4474A,
        textureXMultiplier = 6
    )

    val POISON = register(
        name = "poison",
        displayName = Text.translatable("pokemoncobbled.type.poison"),
        hue = 0x7A47B9,
        textureXMultiplier = 7
    )

    val GROUND = register(
        name = "ground",
        displayName = Text.translatable("pokemoncobbled.type.ground"),
        hue = 0xCEA785,
        textureXMultiplier = 8
    )

    val FLYING = register(
        name = "flying",
        displayName = Text.translatable("pokemoncobbled.type.flying"),
        hue = 0x59D4CF,
        textureXMultiplier = 9
    )

    val PSYCHIC = register(
        name = "psychic",
        displayName = Text.translatable("pokemoncobbled.type.psychic"),
        hue = 0xB65ECC,
        textureXMultiplier = 10
    )

    val BUG = register(
        name = "bug",
        displayName = Text.translatable("pokemoncobbled.type.bug"),
        hue = 0xA2CE44,
        textureXMultiplier = 11
    )

    val ROCK = register(
        name = "rock",
        displayName = Text.translatable("pokemoncobbled.type.rock"),
        hue = 0x958677,
        textureXMultiplier = 12
    )

    val GHOST = register(
        name = "ghost",
        displayName = Text.translatable("pokemoncobbled.type.ghost"),
        hue = 0x6159A5,
        textureXMultiplier = 13
    )

    val DRAGON = register(
        name = "dragon",
        displayName = Text.translatable("pokemoncobbled.type.dragon"),
        hue = 0x458F7A,
        textureXMultiplier = 14
    )

    val DARK = register(
        name = "dark",
        displayName = Text.translatable("pokemoncobbled.type.dark"),
        hue = 0x4B5977,
        textureXMultiplier = 15
    )

    val STEEL = register(
        name = "steel",
        displayName = Text.translatable("pokemoncobbled.type.steel"),
        hue = 0x949494,
        textureXMultiplier = 16
    )

    val FAIRY = register(
        name = "fairy",
        displayName = Text.translatable("pokemoncobbled.type.fairy"),
        hue = 0xE089B9,
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