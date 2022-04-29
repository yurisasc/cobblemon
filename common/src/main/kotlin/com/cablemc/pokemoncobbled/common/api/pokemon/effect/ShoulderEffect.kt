package com.cablemc.pokemoncobbled.common.api.pokemon.effect

import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.google.gson.JsonObject
import net.minecraft.server.level.ServerPlayerEntity

/**
 * Interface for all ShoulderEffects
 *
 * @author Qu
 * @since 2022-01-26
 */
interface ShoulderEffect {
    fun applyEffect(pokemon: Pokemon, player: ServerPlayerEntity, isLeft: Boolean)
    fun removeEffect(pokemon: Pokemon, player: ServerPlayerEntity, isLeft: Boolean)

    fun serialize(json: JsonObject): JsonObject
    fun deserialize(json: JsonObject): ShoulderEffect
}