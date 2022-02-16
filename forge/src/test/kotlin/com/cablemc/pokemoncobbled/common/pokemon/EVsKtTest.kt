package com.cablemc.pokemoncobbled.common.pokemon

import com.cablemc.pokemoncobbled.forge.common.pokemon.EVs
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

internal class EVsKtTest {
    @Test
    fun `should create a empty set of EVs`() {
        val evs = EVs.createEmpty()
        assertFalse(evs.any { (_, value) -> value > 0 })
    }
}