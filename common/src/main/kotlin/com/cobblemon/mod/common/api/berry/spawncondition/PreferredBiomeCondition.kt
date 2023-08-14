package com.cobblemon.mod.common.api.berry.spawncondition

import com.cobblemon.mod.common.api.berry.Berry
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.util.math.random.Random
import net.minecraft.world.biome.Biome

class PreferredBiomeCondition(val minGroveSize: Int, val maxGroveSize: Int) : BerrySpawnCondition{
    override fun canSpawn(berry: Berry, biome: RegistryEntry<Biome>, random: Random): Int {
        val preferredBiomeTags = berry.preferredBiomeTags
        return if (preferredBiomeTags.any { biome.isIn(it)}) random.nextBetween(minGroveSize, maxGroveSize) else 0
    }

    companion object {
        val ID = cobblemonResource("preferred_biome")
    }
}
