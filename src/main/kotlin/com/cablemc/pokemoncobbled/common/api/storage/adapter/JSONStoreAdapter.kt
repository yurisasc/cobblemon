package com.cablemc.pokemoncobbled.common.api.storage.adapter

import com.cablemc.pokemoncobbled.common.api.storage.PokemonStore
import java.io.File
import java.util.UUID

/**
 * A [FileStoreAdapter] for JSON files. This allows a [PokemonStore] to be serialized to a .json file.
 *
 * @author Hiroku
 * @since November 29th, 2021
 */
class JSONStoreAdapter(val root: String) : FileStoreAdapter {
    init {
        File(root).mkdirs()
    }

    override fun <E, T : PokemonStore<E>> getFile(uuid: UUID): File {
        return File(root, "somefile.uuid") // TODO
    }

    override fun <E, T : PokemonStore<E>> load(uuid: UUID, instance: T): T? {
        return null
    }

    override fun <E, T : PokemonStore<E>> save(store: T) {
        // TODO do nothing
    }
}