package com.cablemc.pokemoncobbled.common.api.storage

import com.cablemc.pokemoncobbled.common.CobbledNetwork.sendPacket
import com.cablemc.pokemoncobbled.common.api.PrioritizedList
import com.cablemc.pokemoncobbled.common.api.Priority
import com.cablemc.pokemoncobbled.common.api.storage.factory.PokemonStoreFactory
import com.cablemc.pokemoncobbled.common.api.storage.party.PartyStore
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.party.SetPartyReferencePacket
import net.minecraft.server.level.ServerPlayer
import java.util.UUID

/**
 * Manages the providing of [PokemonStore]s for party, PC, and custom use. The main utilities of this class
 * include the getter functions to provide the stores you need, as well as registering custom [PokemonStoreFactory]
 * implementations at specific [Priority] levels to let Pokémon Cobbled use custom stores or custom store
 * factories.
 *
 * @author Hiroku
 * @since November 29th, 2021
 */
open class PokemonStoreManager {
    private val factories = PrioritizedList<PokemonStoreFactory>()

    open fun registerFactory(priority: Priority, factory: PokemonStoreFactory) {
        factories.add(priority, factory)
    }

    open fun unregisterFactory(factory: PokemonStoreFactory) {
        factories.remove(factory)
    }

    open fun getParty(player: ServerPlayer) = getParty(player.uuid)

    open fun getParty(uuid: UUID): PartyStore {
        for (factory in factories) {
            factory.getPlayerParty(uuid)?.run { return this }
        }

        throw NoPokemonStoreException("No factory was able to provide a party for $uuid - this should not be possible unless someone has removed the default provider!")
    }

    open fun getParties(uuid: UUID): Iterable<PartyStore> {
        val parties = mutableListOf<PartyStore>()
        for (factory in factories) {
            factory.getPlayerParty(uuid)?.let { parties.add(it) }
        }
        return parties.asIterable()
    }

    inline fun <E : StorePosition, reified T : PokemonStore<E>> getCustomStore(uuid: UUID) = getCustomStore(T::class.java, uuid)
    open fun <E : StorePosition, T : PokemonStore<E>> getCustomStore(storeClass: Class<T>, uuid: UUID): T? {
        for (factory in factories) {
            factory.getCustomStore(storeClass, uuid)?.run { return this }
        }

        return null
    }

    open fun onPlayerLogin(player: ServerPlayer) {
        val parties = getParties(player.uuid)
        parties.forEach { party -> party.sendTo(player) }
        player.sendPacket(SetPartyReferencePacket(parties.first().uuid))
    }
}