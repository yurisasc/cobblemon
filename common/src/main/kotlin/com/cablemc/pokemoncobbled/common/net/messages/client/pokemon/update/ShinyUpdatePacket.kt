package com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update

import com.cablemc.pokemoncobbled.common.pokemon.Pokemon

class ShinyUpdatePacket() : BooleanUpdatePacket() {
    constructor(pokemon: Pokemon, value: Boolean): this() {
        this.setTarget(pokemon)
        this.value = value
    }

    override fun set(pokemon: Pokemon, value: Boolean) { pokemon.shiny = value }
}