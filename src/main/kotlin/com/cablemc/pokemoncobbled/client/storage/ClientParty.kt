package com.cablemc.pokemoncobbled.client.storage

import com.cablemc.pokemoncobbled.common.api.storage.party.PartyPosition
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import java.util.UUID

class ClientParty(uuid: UUID, slots: Int) : ClientStorage<PartyPosition>(uuid), Iterable<Pokemon?> {
    val slots = MutableList<Pokemon?>(slots) { null }

    override fun iterator() = slots.iterator()
    override fun findByUUID(uuid: UUID) = slots.find { it?.uuid == uuid }
    override fun set(position: PartyPosition, pokemon: Pokemon?) {
        if (position.slot >= slots.size) {
            return
        }

        slots[position.slot] = pokemon
    }

    override fun get(position: PartyPosition): Pokemon? {
        if (position.slot >= slots.size) {
            return null
        }

        return slots[position.slot]
    }
}