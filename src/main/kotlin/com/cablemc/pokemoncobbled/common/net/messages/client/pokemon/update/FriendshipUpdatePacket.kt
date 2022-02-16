package com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update

import com.cablemc.pokemoncobbled.common.api.event.pokemon.FriendshipUpdateEvent
import com.cablemc.pokemoncobbled.common.net.IntSize
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.util.postAndThen

class FriendshipUpdatePacket() : IntUpdatePacket() {
    constructor(pokemon: Pokemon, value: Int) : this() {
        this.setTarget(pokemon)
        this.value = value
    }

    override fun getSize() = IntSize.U_BYTE
    override fun set(pokemon: Pokemon, value: Int) {
        if (pokemon.isPlayerOwned()) {
            pokemon.getOwnerPlayer()
                ?.let { FriendshipUpdateEvent(it, pokemon, value).postAndThen { pokemon.friendship = value } }
        } else {
            pokemon.setFriendship(value)
        }
    }
}