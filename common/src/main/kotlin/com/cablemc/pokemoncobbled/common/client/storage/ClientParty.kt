package com.cablemc.pokemoncobbled.common.client.storage

import com.cablemc.pokemoncobbled.common.api.storage.party.PartyPosition
import com.cablemc.pokemoncobbled.common.entity.pokemon.Pokemon
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

    fun get(slot: Int) = get(PartyPosition(slot))
    override fun get(position: PartyPosition): Pokemon? {
        if (position.slot >= slots.size) {
            return null
        }

        return slots[position.slot]
    }

    fun getPosition(pokemonID: UUID) = slots.indexOfFirst { it?.uuid == pokemonID }
    override fun getPosition(pokemon: Pokemon): PartyPosition? {
        for (slotNumber in slots.indices) {
            if (slots[slotNumber] == pokemon) {
                return PartyPosition(slotNumber)
            }
        }
        return null
    }
}