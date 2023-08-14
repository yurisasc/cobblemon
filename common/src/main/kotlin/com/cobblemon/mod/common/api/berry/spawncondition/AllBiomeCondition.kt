package com.cobblemon.mod.common.api.berry.spawncondition

import com.cobblemon.mod.common.api.berry.Berry
import com.cobblemon.mod.common.util.cobblemonResource
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.util.math.random.Random
import net.minecraft.world.biome.Biome

class AllBiomeCondition(val minGroveSize: Int, val maxGroveSize: Int) : BerrySpawnCondition{
    override fun canSpawn(berry: Berry, biome: RegistryEntry<Biome>, random: Random): Int {
        return random.nextBetween(minGroveSize, maxGroveSize)
    }
    companion object {
        val ID = cobblemonResource("all_biome")
    }
}
