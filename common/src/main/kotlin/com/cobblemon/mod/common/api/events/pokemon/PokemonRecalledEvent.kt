package com.cobblemon.mod.common.api.events.pokemon

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.pokemon.Pokemon

/**
 * Event fired when a [PokemonEntity] is recalled.
 *
 * @author Segfault Guy
 * @since March 25th, 2023
 */
data class PokemonRecalledEvent (
    val pokemon: Pokemon,
    val oldEntity: PokemonEntity?
)