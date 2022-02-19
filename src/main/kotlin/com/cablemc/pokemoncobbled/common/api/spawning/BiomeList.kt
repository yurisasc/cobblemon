package com.cablemc.pokemoncobbled.common.api.spawning

import net.minecraft.world.level.biome.Biome

/**
 * A dummy type so that it can be detected as its own type during JSON deserialization and be given a unique adapter.
 *
 * @author Hiroku
 * @since January 24th, 2022
 */
class BiomeList : ArrayList<Biome>()