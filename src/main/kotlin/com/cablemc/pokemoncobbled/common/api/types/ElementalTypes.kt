package com.cablemc.pokemoncobbled.common.api.types

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TranslatableComponent

/**
 * Registry for all known ElementalTypes
 */
object ElementalTypes {

    private val allTypes = mutableListOf<ElementalType>()

    val FIRE = register(
        name = "fire",
        displayName = TranslatableComponent("pokemoncobbled.type.fire"),
        textureXMultiplier = 0
    )
    val WATER = register(
        name = "water",
        displayName = TranslatableComponent("pokemoncobbled.type.water"),
        textureXMultiplier = 1
    )
    val GRASS = register(
        name = "grass",
        displayName = TranslatableComponent("pokemoncobbled.type.grass"),
        textureXMultiplier = 2
    )
    val ELECTRIC = register(
        name = "electric",
        displayName = TranslatableComponent("pokemoncobbled.type.electric"),
        textureXMultiplier = 3
    )
    val ICE = register(
        name = "ice",
        displayName = TranslatableComponent("pokemoncobbled.type.ice"),
        textureXMultiplier = 4
    )
    val NORMAL = register(
        name = "normal",
        displayName = TranslatableComponent("pokemoncobbled.type.normal"),
        textureXMultiplier = 5
    )
    val FIGHTING = register(
        name = "fighting",
        displayName = TranslatableComponent("pokemoncobbled.type.fighting"),
        textureXMultiplier = 6
    )
    val GHOST = register(
        name = "ghost",
        displayName = TranslatableComponent("pokemoncobbled.type.ghost"),
        textureXMultiplier = 7
    )
    val DARK = register(
        name = "dark",
        displayName = TranslatableComponent("pokemoncobbled.type.dark"),
        textureXMultiplier = 8
    )
    val FAIRY = register(
        name = "fairy",
        displayName = TranslatableComponent("pokemoncobbled.type.fairy"),
        textureXMultiplier = 9
    )
    val BUG = register(
        name = "bug",
        displayName = TranslatableComponent("pokemoncobbled.type.bug"),
        textureXMultiplier = 10
    )
    val PSYCHIC = register(
        name = "psychic",
        displayName = TranslatableComponent("pokemoncobbled.type.psychic"),
        textureXMultiplier = 11
    )
    val FLYING = register(
        name = "flying",
        displayName = TranslatableComponent("pokemoncobbled.type.flying"),
        textureXMultiplier = 12
    )
    val STEEL = register(
        name = "steel",
        displayName = TranslatableComponent("pokemoncobbled.type.steel"),
        textureXMultiplier = 13
    )
    val GROUND = register(
        name = "ground",
        displayName = TranslatableComponent("pokemoncobbled.type.ground"),
        textureXMultiplier = 14
    )
    val ROCK = register(
        name = "rock",
        displayName = TranslatableComponent("pokemoncobbled.type.rock"),
        textureXMultiplier = 15
    )
    val POISON = register(
        name = "poison",
        displayName = TranslatableComponent("pokemoncobbled.type.poison"),
        textureXMultiplier = 16
    )
    val DRAGON = register(
        name = "dragon",
        displayName = TranslatableComponent("pokemoncobbled.type.dragon"),
        textureXMultiplier = 17
    )

    fun register(name: String, displayName: Component, textureXMultiplier: Int): ElementalType {
        return ElementalType(
            name = name,
            displayName = displayName,
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