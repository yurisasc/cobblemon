package com.cablemc.pokemoncobbled.forge.common.api.pokemon.effect

import com.cablemc.pokemoncobbled.forge.common.pokemon.Pokemon
import com.google.gson.JsonObject
import net.minecraft.server.level.ServerPlayer

/**
 * Interface for all ShoulderEffects
 *
 * @author Qu
 * @since 2022-01-26
 */
interface ShoulderEffect {
    fun applyEffect(pokemon: Pokemon, player: ServerPlayer, isLeft: Boolean)
    fun removeEffect(pokemon: Pokemon, player: ServerPlayer, isLeft: Boolean)

    fun serialize(json: JsonObject): JsonObject
    fun deserialize(json: JsonObject): ShoulderEffect
}