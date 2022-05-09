package com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update.evolution

import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.Evolution
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon

class AddEvolutionPacket() : EvolutionUpdatePacket() {

    constructor(pokemon: Pokemon, evolution: Evolution): this() {
        this.setTarget(pokemon)
        this.current = evolution
        this.sending = this.createSending(pokemon)
    }

    override fun applyToPokemon(pokemon: Pokemon) {
        PokemonCobbledClient.storage.addPendingEvolution(pokemon, this.sending)
    }

}