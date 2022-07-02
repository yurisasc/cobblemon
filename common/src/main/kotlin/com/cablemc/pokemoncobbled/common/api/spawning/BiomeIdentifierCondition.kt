package com.cablemc.pokemoncobbled.common.api.spawning

import net.minecraft.util.Identifier
import net.minecraft.world.biome.Biome

/**
 *  A [BiomeLikeCondition] that expects an [Identifier] to match.
 *
 * @property requiredValue The expected [Identifier] for the context [Biome].
 *
 * @author Licious
 * @since July 1st, 2022
 */
class BiomeIdentifierCondition(override val requiredValue: Identifier) : BiomeLikeCondition<Identifier> {

    override fun accepts(biome: Biome): Boolean {
        val biomeId = this.registry().getId(biome)
        println("Checking $biomeId")
        if (biomeId == this.requiredValue) {
            return true
        }
        println("$biomeId does not match $requiredValue")
        return false
    }

    companion object {

        const val ID = "identifiers"

    }

}