package com.cablemc.pokemoncobbled.common.api.events.starter

import com.cablemc.pokemoncobbled.common.api.events.Cancelable
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonProperties
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.server.network.ServerPlayerEntity

/**
 * Event fired when a starter Pokémon is chosen.
 *
 * @author Hiroku
 * @since August 1st, 2022
 */
data class StarterChosenEvent(val player: ServerPlayerEntity, val properties: PokemonProperties, var pokemon: Pokemon) : Cancelable()