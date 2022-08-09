package com.cablemc.pokemoncobbled.common.pokemon.properties.tags

import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.pokemon.properties.IdentifierProperty
import net.minecraft.util.Identifier

class PokemonTagQueryProperty(key: String, value: Identifier) : IdentifierProperty(key, value) {

    override fun apply(pokemon: Pokemon) {}

    override fun matches(pokemon: Pokemon) = pokemon.hasTags(this.value)

}