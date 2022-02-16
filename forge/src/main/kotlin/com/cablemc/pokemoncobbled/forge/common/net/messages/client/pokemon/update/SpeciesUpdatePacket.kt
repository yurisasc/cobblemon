package com.cablemc.pokemoncobbled.forge.common.net.messages.client.pokemon.update

import com.cablemc.pokemoncobbled.forge.common.api.pokemon.PokemonSpecies
import com.cablemc.pokemoncobbled.common.net.IntSize
import com.cablemc.pokemoncobbled.forge.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.forge.common.pokemon.Species

class SpeciesUpdatePacket() : IntUpdatePacket() {
    constructor(pokemon: Pokemon, species: Species): this() {
        setTarget(pokemon)
        value = species.nationalPokedexNumber
    }
    override fun getSize() = IntSize.U_SHORT
    override fun set(pokemon: Pokemon, value: Int) { pokemon.species = PokemonSpecies.getByPokedexNumber(value)!! // TODO: Proper check
    }
}