package com.cablemc.pokemoncobbled.common.client.storage

import com.cablemc.pokemoncobbled.common.api.storage.StorePosition
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import java.util.UUID

abstract class ClientStorage<T : StorePosition>(val uuid: UUID) {
    abstract fun findByUUID(uuid: UUID): Pokemon?
    abstract fun set(position: T, pokemon: Pokemon?)
    abstract fun get(position: T): Pokemon?
    abstract fun getPosition(pokemon: Pokemon): T?

    fun swap(pokemonID1: UUID, pokemonID2: UUID) {
        val pokemon1 = findByUUID(pokemonID1)
        val pokemon2 = findByUUID(pokemonID2)
        val position1 = pokemon1?.let { getPosition(it) }
        val position2 = pokemon2?.let { getPosition(it) }
        position1?.run { set(this, pokemon2) }
        position2?.run { set(this, pokemon1) }
    }

    fun remove(pokemonID: UUID) {
        val pokemon = findByUUID(pokemonID) ?: return
        getPosition(pokemon)?.let { set(it, null) }
    }

    fun move(pokemonID: UUID, newPosition: T) {
        val pokemon = findByUUID(pokemonID) ?: return
        getPosition(pokemon)?.let {
            set(it, null)
            set(newPosition, pokemon)
        }
    }
}