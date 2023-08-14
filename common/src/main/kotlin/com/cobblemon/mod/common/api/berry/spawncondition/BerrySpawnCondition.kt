package com.cobblemon.mod.common.api.berry.spawncondition

import com.cobblemon.mod.common.api.berry.Berry
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.util.math.random.Random
import net.minecraft.world.biome.Biome

interface BerrySpawnCondition {
    fun canSpawn(berry: Berry, biome: RegistryEntry<Biome>, random: Random): Int
}
