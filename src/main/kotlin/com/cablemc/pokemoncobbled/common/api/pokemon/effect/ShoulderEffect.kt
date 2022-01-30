package com.cablemc.pokemoncobbled.common.api.pokemon.effect

import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.google.gson.JsonObject
import net.minecraft.server.level.ServerPlayer

/**
 * Interface for all ShoulderEffects
 *
 * @author Qu
 * @since 2022-01-26
 */
interface ShoulderEffect {
    fun applyEffect(pokemonEntity: PokemonEntity?, player: ServerPlayer, isLeft: Boolean)
    fun removeEffect(pokemonEntity: PokemonEntity?, player: ServerPlayer, isLeft: Boolean)

    fun serialize(json: JsonObject): JsonObject
    fun deserialize(json: JsonObject): ShoulderEffect
}