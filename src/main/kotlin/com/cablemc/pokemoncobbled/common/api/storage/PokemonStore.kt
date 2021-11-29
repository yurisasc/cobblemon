package com.cablemc.pokemoncobbled.common.api.storage

import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.server.level.ServerPlayer
import java.util.UUID

abstract class PokemonStore<T : StorePosition> : Iterable<Pokemon> {
    abstract fun getUUID(): UUID
    abstract fun getAll(): List<Pokemon>
    abstract fun get(position: T): Pokemon?
    abstract fun set(position: T, pokemon: Pokemon)
    abstract fun remove(position: T): Boolean
    fun remove(pokemon: Pokemon): Boolean {
        val position = pokemon.storageCoordinates.get()!!.position as T
        return remove(position)
    }
    abstract fun getFirstAvailablePosition(): T?
    abstract fun getObservingPlayers(): Iterable<ServerPlayer>

}