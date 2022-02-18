package com.cablemc.pokemoncobbled.common.api.storage

import com.cablemc.pokemoncobbled.common.api.storage.party.PartyPosition
import com.cablemc.pokemoncobbled.common.api.storage.pc.PCPosition
import com.cablemc.pokemoncobbled.common.api.storage.PokemonStore
import com.cablemc.pokemoncobbled.common.api.storage.StoreCoordinates
import com.cablemc.pokemoncobbled.common.entity.pokemon.Pokemon
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class PokemonStoreTest {

    @Test
    fun `removing pokemon that is in another storage with different type returns false`() {
        val pokemonStore = mockk<PokemonStore<PartyPosition>>()
        every { pokemonStore.remove(any<Pokemon>()) }.answers{ callOriginal() }
        val pcStore = mockk<PokemonStore<PCPosition>>()
        val storeCoordinates = StoreCoordinates(
            store = pcStore,
            position = PCPosition(1, 1)
        )
        val pokemon = Pokemon().apply {
            this.storeCoordinates.set(storeCoordinates)
        }
        val result = pokemonStore.remove(pokemon)
        assertFalse(result)
    }

}