package com.cablemc.pokemoncobbled.common.api.storage.factory

import com.cablemc.pokemoncobbled.common.api.storage.PokemonStore
import com.cablemc.pokemoncobbled.common.api.storage.PokemonStoreManager
import com.cablemc.pokemoncobbled.common.api.storage.StorePosition
import com.cablemc.pokemoncobbled.common.api.storage.party.PlayerPartyStore
import java.util.UUID

/**
 * A provider for [PokemonStore]'s when given a player UUID. An implementation of this interface
 * does not need to always provide a value. In some cases you may only want to provide a store for
 * specific players, or under specific conditions, or only for parties, etc.
 *
 * If you have an implementation of this interface and want it to be used by Pokémon Cobbled, register
 * the factory using [PokemonStoreManager.registerFactory].
 *
 * @author Hiroku
 * @since November 29th, 2021
 */
interface PokemonStoreFactory {
    fun getPlayerParty(uuid: UUID): PlayerPartyStore?
//    fun providePCForPlayerUUID(uuid: UUID): PCStore?
    fun <E : StorePosition, T : PokemonStore<E>> getCustomStore(storeClass: Class<T>, uuid: UUID): T?
    fun shutdown()
}