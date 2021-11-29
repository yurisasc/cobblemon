package com.cablemc.pokemoncobbled.client.storage

import com.cablemc.pokemoncobbled.common.api.storage.StorePosition
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import java.util.UUID

abstract class ClientStorage<T : StorePosition>(val uuid: UUID) {
    abstract fun findByUUID(uuid: UUID): Pokemon?
    abstract fun set(position: T, pokemon: Pokemon?)
    abstract fun get(position: T): Pokemon?
}