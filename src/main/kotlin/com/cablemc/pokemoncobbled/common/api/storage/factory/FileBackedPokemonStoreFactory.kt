package com.cablemc.pokemoncobbled.common.api.storage.factory

import com.cablemc.pokemoncobbled.common.api.reactive.Observable.Companion.emitWhile
import com.cablemc.pokemoncobbled.common.api.storage.PokemonStore
import com.cablemc.pokemoncobbled.common.api.storage.StorePosition
import com.cablemc.pokemoncobbled.common.api.storage.adapter.FileStoreAdapter
import com.cablemc.pokemoncobbled.common.api.storage.adapter.SerializedStore
import com.cablemc.pokemoncobbled.common.api.storage.party.PlayerPartyStore
import com.cablemc.pokemoncobbled.common.util.subscribeOnServer
import com.cablemc.pokemoncobbled.mod.PokemonCobbledMod.LOGGER
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.server.ServerStartingEvent
import net.minecraftforge.event.server.ServerStoppingEvent
import net.minecraftforge.eventbus.api.SubscribeEvent

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
    init {
        MinecraftForge.EVENT_BUS.register(this)
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

    override fun getPlayerParty(uuid: UUID) = getStore(PlayerPartyStore::class.java, uuid)
    // override fun getPC(uuid: UUID) = getStore(uuid, dirtyPCs, cachedPCs)
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

    var passedTicks = 0
    @SubscribeEvent
    fun onServerStarted(event: ServerStartingEvent) {
        if (saveExecutor.isShutdown) {
            saveExecutor = Executors.newSingleThreadExecutor()
        }
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.ServerTickEvent) {
        if (event.phase == TickEvent.Phase.START) {
            passedTicks++
            // TODO config option
            if (passedTicks > 20 * 30) {
                saveAll()
                passedTicks = 0
            }
        }
    }

    @SubscribeEvent
    fun onServerStopping(event: ServerStoppingEvent) {
        saveAll()
        saveExecutor.shutdown()
    }
}