package com.cobblemon.mod.common.api.events.entity

import com.cobblemon.mod.common.api.events.Cancelable
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity

/**
 * Event fired when a Pokémon is about to be saved to the world. Cancel this event if the Pokémon
 * should not be saved to the world.
 *
 * @author Hiroku
 * @since January 7th, 2022
 */
class PokemonEntitySaveToWorldEvent(val pokemonEntity: PokemonEntity) : Cancelable()