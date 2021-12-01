package com.cablemc.pokemoncobbled.common.api.storage.factory

import com.cablemc.pokemoncobbled.common.api.storage.PokemonStore
import com.cablemc.pokemoncobbled.common.api.storage.adapter.FileStoreAdapter
import java.util.UUID

/**
 * A typical [PokemonStoreFactory], specifically a [FileBackedPokemonStoreFactory], which will not provide a custom
 * store. It is also guaranteed to return a non-null party or PC, and the only customisable element is the adapter
 * used to write and read from files.
 *
 * @author Hiroku
 * @since November 29th, 2021
 */
class DefaultPokemonStoreFactory(adapter: FileStoreAdapter) : FileBackedPokemonStoreFactory(adapter, true) {
    override fun <E, T : PokemonStore<E>> getCustomStore(uuid: UUID) = null
}