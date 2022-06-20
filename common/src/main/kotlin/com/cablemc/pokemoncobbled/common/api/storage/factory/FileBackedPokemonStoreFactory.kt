package com.cablemc.pokemoncobbled.common.api.storage.factory

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.PokemonCobbled.LOGGER
import com.cablemc.pokemoncobbled.common.api.reactive.Observable.Companion.emitWhile
import com.cablemc.pokemoncobbled.common.api.storage.PokemonStore
import com.cablemc.pokemoncobbled.common.api.storage.StorePosition
import com.cablemc.pokemoncobbled.common.api.storage.adapter.FileStoreAdapter
import com.cablemc.pokemoncobbled.common.api.storage.adapter.SerializedStore
import com.cablemc.pokemoncobbled.common.api.storage.party.PlayerPartyStore
import com.cablemc.pokemoncobbled.common.api.storage.pc.PCStore
import com.cablemc.pokemoncobbled.common.util.subscribeOnServer
import dev.architectury.event.events.common.LifecycleEvent
import dev.architectury.event.events.common.TickEvent
import java.util.UUID
import java.util.concurrent.Executors

/**
 * A [PokemonStoreFactory] that is backed by a file. This implementation will now handle persistence and scheduling
 * for saving, as well as simple map cache.
 *
 * @author Hiroku
 * @since November 29th, 2021
 */
open class FileBackedPokemonStoreFactory<S>(
    protected val adapter: FileStoreAdapter<S>,
    protected val createIfMissing: Boolean
) : PokemonStoreFactory {

    var passedTicks = 0

    init {
        LifecycleEvent.SERVER_STARTING.register {
            if (saveExecutor.isShutdown) {
                saveExecutor = Executors.newSingleThreadExecutor()
            }
        }

        TickEvent.SERVER_PRE.register {
            passedTicks++
            if (passedTicks > 20 * PokemonCobbled.config.pokemonSaveIntervalSeconds) {
                saveAll()
                passedTicks = 0
            }
        }

        LifecycleEvent.SERVER_STOPPING.register {
            saveAll()
            saveExecutor.shutdown()
        }
    }

    protected var saveExecutor = Executors.newSingleThreadExecutor()
    protected val storeCaches = mutableMapOf<Class<out PokemonStore<*>>, StoreCache<*, *>>()
    protected inner class StoreCache<E : StorePosition, T : PokemonStore<E>> {
        val cacheMap = mutableMapOf<UUID, T>()
    }

    protected fun <E : StorePosition, T : PokemonStore<E>> getStoreCache(storeClass: Class<T>): StoreCache<E, T> {
        val cache = storeCaches.getOrPut(storeClass) {  StoreCache<E, T>() }
        return cache as StoreCache<E, T>
    }

    private val dirtyStores = mutableSetOf<PokemonStore<*>>()

    override fun getPlayerParty(playerID: UUID) = getStore(PlayerPartyStore::class.java, playerID)
    override fun getPC(playerID: UUID) = getStore(PCStore::class.java, playerID)


    override fun <E : StorePosition, T : PokemonStore<E>> getCustomStore(storeClass: Class<T>, uuid: UUID) = getStore(storeClass, uuid)

    fun <E : StorePosition, T : PokemonStore<E>> getStore(storeClass: Class<T>, uuid: UUID): T? {
        val cache = getStoreCache(storeClass).cacheMap
        val cached = cache[uuid]
        if (cached != null) {
            return cached
        } else {
            val loaded = adapter.load(storeClass, uuid)
                ?: run {
                    if (createIfMissing) {
                        return@run storeClass.getConstructor(UUID::class.java).newInstance(uuid)
                    } else {
                        return@run null
                    }
                }
                ?: return null

            loaded.initialize()
            track(loaded)
            cache[uuid] = loaded
            return loaded
        }
    }

    fun save(store: PokemonStore<*>) {
        val serialized = SerializedStore(store::class.java, store.uuid, adapter.serialize(store))
        dirtyStores.remove(store)
        saveExecutor.submit { adapter.save(serialized.storeClass, serialized.uuid, serialized.serializedForm) }
    }

    fun saveAll() {
        LOGGER.debug("Serializing ${dirtyStores.size} Pokémon stores.")
        val serializedStores = dirtyStores.map { SerializedStore(it::class.java, it.uuid, adapter.serialize(it)) }
        dirtyStores.clear()
        LOGGER.debug("Queueing save.")
        saveExecutor.submit {
            serializedStores.forEach { adapter.save(it.storeClass, it.uuid, it.serializedForm) }
            LOGGER.debug("Saved ${serializedStores.size} Pokémon stores.")
        }
    }

    fun isCached(store: PokemonStore<*>) = storeCaches[store::class.java]?.cacheMap?.containsKey(store.uuid) == true

    fun track(store: PokemonStore<*>) {
        store.getAnyChangeObservable()
            .pipe(emitWhile { isCached(store) })
            .subscribeOnServer { dirtyStores.add(store) }
    }
}