package com.cablemc.pokemoncobbled.common.api.spawning

import net.minecraft.tag.TagKey
import net.minecraft.util.registry.Registry
import net.minecraft.world.biome.Biome

/**
 * A [BiomeLikeCondition] that expects a [TagKey] attached to the [Biome] registry.
 *
 * @property requiredValue The [TagKey] the context [Biome] must be in.
 *
 * @author Licious
 * @since July 1st, 2022
 */
class BiomeTagCondition(override val requiredValue: TagKey<Biome>) : BiomeLikeCondition<TagKey<Biome>> {

    override fun accepts(biome: Biome, registry: Registry<Biome>): Boolean {
        val optKey = registry.getKey(biome)
        if (!optKey.isPresent) {
            return false
        }
        val optEntry = registry.getEntry(optKey.get())
        if (!optEntry.isPresent) {
            return false
        }
        return optEntry.get().isIn(this.requiredValue)
    }

    companion object {

        const val PREFIX = '#'

    }

}