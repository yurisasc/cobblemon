package com.cablemc.pokemoncobbled.common.api.storage

import com.cablemc.pokemoncobbled.common.api.storage.factory.PokemonStoreFactory
import com.cablemc.pokemoncobbled.common.api.storage.party.PartyStore
import com.cablemc.pokemoncobbled.common.net.PokemonCobbledNetwork.sendPacket
import com.cablemc.pokemoncobbled.common.net.messages.client.storage.party.SetPartyReferencePacket
import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.eventbus.api.SubscribeEvent
import java.util.UUID

/**
 * Manages the providing of [PokemonStore]s for party, PC, and custom use. The main utilities of this class
 * include the getter functions to provide the stores you need, as well as registering custom [PokemonStoreFactory]
 * implementations at specific [EventPriority] levels to let Pokémon Cobbled use custom storages or custom storage
 * providers.
 *
 * @author Hiroku
 * @since November 29th, 2021
 */
object PokemonStoreManager {
    private val factories = Array(EventPriority.values().size) { mutableListOf<PokemonStoreFactory>() }

    fun registerFactory(priority: EventPriority, provider: PokemonStoreFactory) {
        factories[priority.ordinal].add(provider)
    }

    fun unregisterProvider(provider: PokemonStoreFactory) {
        factories.forEach { it.remove(provider) }
    }

    fun getParty(player: ServerPlayer) = getParty(player.uuid)

    fun getParty(uuid: UUID): PartyStore {
        for (factoryList in factories) {
            for (provider in factoryList) {
                provider.getPlayerParty(uuid)?.run { return this }
            }
        }

        throw NoPokemonStoreException("No factory was able to provide a party for $uuid - this should not be possible unless someone has removed the default provider!")
    }

    fun getParties(uuid: UUID): Iterable<PartyStore> {
        val parties = mutableListOf<PartyStore>()
        for (factoryList in factories) {
            for (provider in factoryList) {
                provider.getPlayerParty(uuid)?.let { parties.add(it) }
            }
        }
        return parties.asIterable()
    }

    fun <E : StorePosition, T : PokemonStore<E>> getCustomStore(uuid: UUID): T? {
        for (factoryList in factories) {
            for (provider in factoryList) {
                provider.getCustomStore<E, T>(uuid)?.run { return this }
            }
        }

        return null
    }

    @SubscribeEvent
    fun on(event: PlayerEvent.PlayerLoggedInEvent) {
        val player = event.player
        if (player is ServerPlayer) {
            val parties = getParties(player.uuid)
            parties.forEach { party -> party.sendTo(player) }
            player.sendPacket(SetPartyReferencePacket(parties.first().uuid))
        }
    }
}