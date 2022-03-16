package com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update

import com.cablemc.pokemoncobbled.common.net.IntSize
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon

/**
 * Updates the current health of the Pokémon
 *
 * @author Hiroku
 * @since February 12, 2022
 */
class HealthUpdatePacket() : IntUpdatePacket() {
    constructor(pokemon: Pokemon, value: Int) : this() {
        this.setTarget(pokemon)
        this.value = value
    }

    override fun getSize() = IntSize.U_SHORT
    override fun set(pokemon: Pokemon, value: Int) { pokemon.currentHealth = value }
}