package com.cablemc.pokemoncobbled.common.pokemon.properties

import com.cablemc.pokemoncobbled.common.api.properties.CustomPokemonProperty
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon

class StringProperty(
    val key: String,
    val value: String,
    private val applicator: (pokemon: Pokemon, value: String) -> Unit,
    private val matcher: (pokemon: Pokemon, value: String) -> Boolean
) : CustomPokemonProperty {

    override fun apply(pokemon: Pokemon) {
        this.applicator.invoke(pokemon, this.value)
    }

    override fun matches(pokemon: Pokemon) = this.matcher.invoke(pokemon, this.value)

    override fun asString() = "${this.key}=${this.value}"

}