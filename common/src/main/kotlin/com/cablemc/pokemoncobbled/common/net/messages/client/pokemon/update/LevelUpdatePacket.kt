package com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update

import com.cablemc.pokemoncobbled.common.net.IntSize
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon

class LevelUpdatePacket() : IntUpdatePacket() {
    constructor(pokemon: Pokemon, value: Int): this() {
        this.setTarget(pokemon)
        this.value = value
    }

    override fun getSize() = IntSize.U_SHORT
    override fun set(pokemon: Pokemon, value: Int) { pokemon.level = value }
}