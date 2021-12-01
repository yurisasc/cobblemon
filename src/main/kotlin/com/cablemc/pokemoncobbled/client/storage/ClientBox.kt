package com.cablemc.pokemoncobbled.client.storage

import com.cablemc.pokemoncobbled.common.api.storage.pc.POKEMON_PER_BOX
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon

class ClientBox : Iterable<Pokemon?> {
    val slots = MutableList<Pokemon?>(POKEMON_PER_BOX) { null }
    override fun iterator() = slots.iterator()
}