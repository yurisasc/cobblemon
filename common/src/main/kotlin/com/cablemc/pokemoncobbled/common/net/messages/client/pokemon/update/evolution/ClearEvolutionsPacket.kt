package com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update.evolution

import com.cablemc.pokemoncobbled.common.pokemon.Pokemon

class ClearEvolutionsPacket() : EvolutionUpdatePacket() {

    constructor(pokemon: Pokemon): this() {
        this.setTarget(pokemon)
    }

    override fun applyToPokemon(pokemon: Pokemon) {
        pokemon.evolutionProxy.client().clear()
    }

}