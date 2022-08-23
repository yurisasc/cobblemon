package com.cablemc.pokemoncobbled.common.api.storage.adapter;

import com.cablemc.pokemoncobbled.common.api.storage.PokemonStore
import com.cablemc.pokemoncobbled.common.api.storage.StorePosition
import java.util.UUID

interface CobbledAdapter<S> {

    /**
     * Attempts to load a store using the specified class and UUID. This would return null if
     * the file does not exist or if this store adapter doesn't know how to load this storage class.
     */
    fun <E : StorePosition, T : PokemonStore<E>> load(storeClass: Class<T>, uuid: UUID): T?

}
