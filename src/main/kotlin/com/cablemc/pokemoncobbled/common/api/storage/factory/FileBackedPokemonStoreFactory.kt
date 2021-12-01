package com.cablemc.pokemoncobbled.common.api.storage.factory

import com.cablemc.pokemoncobbled.common.api.storage.PokemonStore
import com.cablemc.pokemoncobbled.common.api.storage.adapter.FileStoreAdapter
import com.cablemc.pokemoncobbled.common.api.storage.party.PlayerPartyStore
import java.util.UUID

/**
 * A [PokemonStoreFactory] that is backed by a file. This implementation will now handle persistence and scheduling
 * for saving, as well as simple map cache. This is still abstract, as no answer is provided for custom storages.
 *
 * @author Hiroku
 * @since November 29th, 2021
 */
abstract class FileBackedPokemonStoreFactory(val adapter: FileStoreAdapter, val createIfMissing: Boolean) : PokemonStoreFactory {
    protected val cachedParties = mutableMapOf<UUID, PlayerPartyStore>()
    // protected val cachedPCs = mutableMapOf<UUID, PCStore>()

    private val dirtyParties = mutableListOf<PlayerPartyStore>()
//    private val dirtyPCs = mutableListOf<PCStore>()

    override fun getPlayerParty(uuid: UUID) = getStore(uuid, dirtyParties, cachedParties) { PlayerPartyStore(uuid) }
//    override fun getPC(uuid: UUID) = getStore(uuid, dirtyPCs, cachedPCs) { PCStore(uuid) }

    fun <E, T : PokemonStore<E>> getStore(uuid: UUID, dirtyList: MutableList<T>, cache: MutableMap<UUID, T>, builder: () -> T): T? {
        return if (createIfMissing) {
            cache.getOrPut(uuid) { loadOrCreate(uuid, dirtyList, builder) }
        } else {
            cache[uuid] ?: adapter.load<E, T>(uuid, builder())?.also { cache[uuid] = it }
        }
    }

    fun <E, T : PokemonStore<E>> loadOrCreate(uuid: UUID, dirtyList: MutableList<T>, builder: () -> T): T {
        val loaded = adapter.load(uuid, builder())
        return if (loaded == null) {
            val store = builder()
            adapter.save(store)
            dirtyList.remove(store)
            store
        } else {
            loaded
        }
    }

    fun <E, T : PokemonStore<E>> save(store: T) {
        adapter.save(store)
    }
}