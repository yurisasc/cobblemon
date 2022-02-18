package com.cablemc.pokemoncobbled.forge.common.net.messages.client.pokemon.update

import com.cablemc.pokemoncobbled.common.entity.pokemon.Pokemon

class ShinyUpdatePacket() : BooleanUpdatePacket() {
    constructor(pokemon: Pokemon, value: Boolean): this() {
        this.setTarget(pokemon)
        this.value = value
    }

    override fun set(pokemon: Pokemon, value: Boolean) { pokemon.shiny = value }
}