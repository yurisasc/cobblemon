package com.cablemc.pokemoncobbled.common.api.storage

import com.cablemc.pokemoncobbled.common.CobbledNetwork.sendPacket
import com.cablemc.pokemoncobbled.common.api.PrioritizedList
import com.cablemc.pokemoncobbled.common.api.Priority
import com.cablemc.pokemoncobbled.common.api.storage.factory.PokemonStoreFactory
import com.cablemc.pokemoncobbled.common.api.storage.party.PartyStore
import com.cablemc.pokemoncobbled.common.api.storage.party.PlayerPartyStore
import com.cablemc.pokemoncobbled.common.api.storage.pc.PCStore
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.party.SetPartyReferencePacket
import com.cablemc.pokemoncobbled.common.world.level.block.entity.PCBlockEntity
import java.util.UUID
import net.minecraft.server.network.ServerPlayerEntity

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
        factory.shutdown()
        factories.remove(factory)
    }

    open fun unregisterAll() {
        factories.toList().forEach(::unregisterFactory)
    }

    open fun getParty(player: ServerPlayerEntity) = getParty(player.uuid)

    @Throws(NoPokemonStoreException::class)
    open fun getParty(playerID: UUID): PlayerPartyStore {
        for (factory in factories) {
            factory.getPlayerParty(playerID)?.run { return this }
        }

        throw NoPokemonStoreException("No factory was able to provide a party for $playerID - this should not be possible unless someone has removed the default provider!")
    }

    @Throws(NoPokemonStoreException::class)
    open fun getPC(playerID: UUID): PCStore {
        for (factory in factories) {
            factory.getPC(playerID)?.run { return this }
        }

        throw NoPokemonStoreException("No factory was able to provide a PC for $playerID - this should not be possible unless someone has removed the default provider!")
    }

    open fun getPCForPlayer(player: ServerPlayerEntity, pcBlockEntity: PCBlockEntity): PCStore? {
        for (factory in factories) {
            factory.getPCForPlayer(player, pcBlockEntity)?.run { return this }
        }
        return null
    }

    open fun getParties(playerID: UUID): Iterable<PartyStore> {
        val parties = mutableListOf<PartyStore>()
        for (factory in factories) {
            factory.getPlayerParty(playerID)?.let { parties.add(it) }
        }
        return parties.asIterable()
    }

    open fun getPCs(playerID: UUID): Iterable<PCStore> {
        val pcs = mutableListOf<PCStore>()
        for (factory in factories) {
            factory.getPC(playerID)?.let { pcs.add(it) }
        }
        return pcs.asIterable()
    }
    inline fun <E : StorePosition, reified T : PokemonStore<E>> getCustomStore(uuid: UUID) = getCustomStore(T::class.java, uuid)
    open fun <E : StorePosition, T : PokemonStore<E>> getCustomStore(storeClass: Class<T>, uuid: UUID): T? {
        for (factory in factories) {
            factory.getCustomStore(storeClass, uuid)?.run { return this }
        }

        return null
    }

    open fun onPlayerLogin(player: ServerPlayerEntity) {
        val parties = getParties(player.uuid)
        parties.forEach { party -> party.sendTo(player) }
        getPCs(player.uuid).forEach { pc -> pc.sendTo(player) }
        player.sendPacket(SetPartyReferencePacket(parties.first().uuid))
    }
}