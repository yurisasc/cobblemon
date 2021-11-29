package com.cablemc.pokemoncobbled.common.api.storage.party

import com.cablemc.pokemoncobbled.common.api.storage.PokemonStore
import com.cablemc.pokemoncobbled.common.api.storage.StorageCoordinates
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.server.level.ServerPlayer
import java.util.UUID

open class PartyStore(val uuid: UUID) : PokemonStore<PartyPosition>() {
    // Allow more or fewer than 6 as a maximum perhaps?
    protected val slots = mutableListOf<Pokemon?>()

    override fun getUUID() = this.uuid
    override fun iterator() = slots.filterNotNull().iterator()
    override fun getAll() = slots.filterNotNull()

    override fun get(position: PartyPosition) = position.slot.takeIf { it < slots.size }?.let { slots[it] }
    override fun set(position: PartyPosition, pokemon: Pokemon) {
        if (position.slot >= slots.size) {
            throw IllegalArgumentException("Slot position is out of bounds")
        } else {
            slots[position.slot] = pokemon
            pokemon.storageCoordinates.set(StorageCoordinates(this, position))
        }
    }

    override fun getFirstAvailablePosition(): PartyPosition? {
        for (i in slots.indices) {
            if (slots[i] == null) {
                return PartyPosition(i)
            }
        }
        return null
    }

    override fun remove(position: PartyPosition): Boolean {
        if (position.slot >= slots.size) {
            return false
        } else {
            val current = slots[position.slot]
            slots[position.slot] = null
            return current != null
        }
    }

    override fun getObservingPlayers(): Iterable<ServerPlayer> {
        TODO("Not yet implemented")
    }
}

