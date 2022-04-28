package com.cablemc.pokemoncobbled.common.net.messages.server.pokemon.update.evolution

import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.EvolutionDisplay
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon

class AcceptEvolutionPacket() : EvolutionDisplayUpdatePacket() {

    constructor(pokemon: Pokemon, evolution: EvolutionDisplay): this() {
        this.setTarget(pokemon)
        this.current = evolution
    }

    override fun applyToPokemon(pokemon: Pokemon) {
        val evolution = pokemon.species.evolutions.firstOrNull { evolution -> evolution.id.equals(this.evolutionId, true) } ?: return
        pokemon.pendingEvolutions.start(evolution)
    }

}