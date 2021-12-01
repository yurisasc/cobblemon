package com.cablemc.pokemoncobbled.common.api.storage.adapter

import com.cablemc.pokemoncobbled.common.api.storage.PokemonStore
import java.io.File
import java.util.UUID

/**
 * Interface for some type of file backend for [PokemonStore] saving.
 *
 * @author Hiroku
 * @since November 29th, 2021
 */
interface FileStoreAdapter {
    fun <E, T : PokemonStore<E>> getFile(uuid: UUID): File
    fun <E, T : PokemonStore<E>> load(uuid: UUID, instance: T): T?
    fun <E, T : PokemonStore<E>> save(store: T)
}