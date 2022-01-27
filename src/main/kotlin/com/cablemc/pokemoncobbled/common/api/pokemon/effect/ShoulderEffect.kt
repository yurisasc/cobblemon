package com.cablemc.pokemoncobbled.common.api.pokemon.effect

import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import net.minecraft.server.level.ServerPlayer

/**
 * Base class for all ShoulderEffects
 *
 * @author Qu
 * @since 2022-01-26
 */
abstract class ShoulderEffect {
    abstract val name: String

    abstract fun applyEffect(pokemonEntity: PokemonEntity?, player: ServerPlayer)
    abstract fun removeEffect(pokemonEntity: PokemonEntity?, player: ServerPlayer)
}