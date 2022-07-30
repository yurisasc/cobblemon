package com.cablemc.pokemoncobbled.common.util.adapters

import com.cablemc.pokemoncobbled.common.api.conditional.RegistryLikeAdapter
import com.cablemc.pokemoncobbled.common.api.conditional.RegistryLikeIdentifierCondition
import com.cablemc.pokemoncobbled.common.api.conditional.RegistryLikeTagCondition
import com.cablemc.pokemoncobbled.common.registry.BiomeIdentifierCondition
import com.cablemc.pokemoncobbled.common.registry.BiomeTagCondition
import net.minecraft.util.registry.Registry
import net.minecraft.world.biome.Biome

/**
 * A type adapter for [BiomeLikeCondition]s.
 *
 * @author Hiroku, Licious
 * @since July 2nd, 2022
 */
object BiomeLikeConditionAdapter : RegistryLikeAdapter<Biome> {
    override val registryLikeConditions = mutableListOf(
        RegistryLikeTagCondition.resolver(Registry.BIOME_KEY, ::BiomeTagCondition),
        RegistryLikeIdentifierCondition.resolver(::BiomeIdentifierCondition)
    )
}