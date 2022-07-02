package com.cablemc.pokemoncobbled.common.api.spawning

import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
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

    override fun accepts(biome: Biome, registry: Registry<Biome>) = registry.getId(biome) == this.requiredValue

}