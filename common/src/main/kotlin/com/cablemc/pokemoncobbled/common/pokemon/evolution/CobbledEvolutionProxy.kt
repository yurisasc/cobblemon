package com.cablemc.pokemoncobbled.common.pokemon.evolution

import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.*
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.pokemon.evolution.controller.CobbledClientEvolutionController
import com.cablemc.pokemoncobbled.common.pokemon.evolution.controller.CobbledServerEvolutionController

class CobbledEvolutionProxy(private val pokemon: Pokemon, private val clientSide: Boolean) : EvolutionProxy<EvolutionDisplay, Evolution> {

    private val controller = if (this.clientSide) CobbledClientEvolutionController(this.pokemon) else CobbledServerEvolutionController(this.pokemon)

    override fun isClient(): Boolean = this.clientSide

    override fun current(): EvolutionController<out EvolutionLike> = this.controller

    override fun client(): EvolutionController<EvolutionDisplay> {
        return this.controller as? EvolutionController<EvolutionDisplay> ?: throw ClassCastException("Cannot use the client implementation from the server side")
    }

    override fun server(): EvolutionController<Evolution> {
        return this.controller as? EvolutionController<Evolution> ?: throw ClassCastException("Cannot use the server implementation from the client side")
    }

}