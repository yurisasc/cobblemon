package com.cablemc.pokemoncobbled.common.api.storage.adapter.flatifle

import com.cablemc.pokemoncobbled.common.api.storage.PokemonStore
import com.cablemc.pokemoncobbled.common.api.storage.StorePosition
import com.cablemc.pokemoncobbled.common.api.storage.adapter.CobbledAdapter
import java.util.UUID

/**
 * Interface for some type of file backend for [PokemonStore] saving and loading.
 *
 * @author Hiroku
 * @since November 29th, 2021
 */
interface FileStoreAdapter<S> : CobbledAdapter<S> {
    /** Converts the specified store into a serialized form. This is expected to run on the server thread, and as fast as possible. */
    fun <E : StorePosition, T : PokemonStore<E>> serialize(store: T): S
    /** Writes the serialized form of a store into the appropriate file. This should be threadsafe. */
    fun save(storeClass: Class<out PokemonStore<*>>, uuid: UUID, serialized: S)
}