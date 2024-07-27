package com.cobblemon.mod.common.pokemon

import com.cobblemon.mod.common.api.pokemon.Characteristics
import com.cobblemon.mod.common.api.pokemon.stats.Stats
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

internal class CharacteristicKtTest {

    @Test
    fun `Should generate correct characteristic in normal case`() {
        val iVs = IVs()
        iVs[Stats.HP] = 24
        iVs[Stats.ATTACK] = 18
        iVs[Stats.DEFENCE] = 17
        iVs[Stats.SPEED] = 29
        iVs[Stats.SPECIAL_ATTACK] = 24
        iVs[Stats.SPECIAL_DEFENCE] = 27

        assertEquals(Characteristic.calculateCharacteristic(iVs, UUID.randomUUID()), Characteristics.QUICK_TO_FLEE)
    }

    @Test
    fun `Should generate correct characteristic in tie case`() {
        val iVs = IVs()
        iVs[Stats.HP] = 8
        iVs[Stats.ATTACK] = 0
        iVs[Stats.DEFENCE] = 0
        iVs[Stats.SPEED] = 17
        iVs[Stats.SPECIAL_ATTACK] = 17
        iVs[Stats.SPECIAL_DEFENCE] = 10
        // This hashes to an int that satisfies <int> mod 6 = 5
        // Ideally this would be replaced with a mock on uUID.hashCode(),
        // but that doesn't work currently
        val uUID = UUID.fromString("765acb4b-3893-4a9a-971f-c32dfbaf04bd")
        assertEquals(Characteristic.calculateCharacteristic(iVs, uUID), Characteristics.IMPETUOUS_AND_SILLY)
    }
}