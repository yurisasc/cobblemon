package com.cablemc.pokemoncobbled.common.api.entity

import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.entity.damage.DamageSource

interface PokemonSideDelegate : EntitySideDelegate<PokemonEntity> {
    fun changePokemon(pokemon: Pokemon)
    fun drop(source: DamageSource?) {}
}