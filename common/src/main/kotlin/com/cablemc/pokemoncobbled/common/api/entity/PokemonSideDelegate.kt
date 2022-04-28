package com.cablemc.pokemoncobbled.common.api.entity

import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon

interface PokemonSideDelegate : EntitySideDelegate<PokemonEntity> {
    fun changePokemon(pokemon: Pokemon)
}