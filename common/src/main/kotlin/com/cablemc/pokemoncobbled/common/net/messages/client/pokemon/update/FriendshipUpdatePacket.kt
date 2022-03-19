package com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update

import com.cablemc.pokemoncobbled.common.api.events.CobbledEvents.FRIENDSHIP_UPDATED
import com.cablemc.pokemoncobbled.common.api.events.pokemon.FriendshipUpdateEvent
import com.cablemc.pokemoncobbled.common.net.IntSize
import com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update.IntUpdatePacket
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon

class FriendshipUpdatePacket() : IntUpdatePacket() {
    constructor(pokemon: Pokemon, value: Int) : this() {
        this.setTarget(pokemon)
        this.value = value
    }

    override fun getSize() = IntSize.U_BYTE
    override fun set(pokemon: Pokemon, value: Int) {
        val player = pokemon.getOwnerPlayer()
        if (player != null) {
            FRIENDSHIP_UPDATED.post(FriendshipUpdateEvent(player, pokemon, value)) {
                pokemon.friendship = it.newFriendship
            }
        } else {
            pokemon.setFriendship(value)
        }
    }
}