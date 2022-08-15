package com.cablemc.pokemoncobbled.common.pokemon.evolution.requirements

import com.cablemc.pokemoncobbled.common.api.conditional.RegistryLikeCondition
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.pokemon.evolution.requirements.template.EntityQueryRequirement
import com.cablemc.pokemoncobbled.common.registry.BiomeIdentifierCondition
import net.minecraft.entity.LivingEntity
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.world.biome.Biome
/**
 * A [EntityQueryRequirement] for when a [Pokemon] is expected to be in a certain [Biome].
 *
 * @property biomeCondition The [Identifier] of the [Biome] the queried entity is expected to be in.
 * @author Licious
 * @since March 21st, 2022
 */
class BiomeRequirement : EntityQueryRequirement {
    val biomeCondition: RegistryLikeCondition<Biome> = BiomeIdentifierCondition(Identifier("plains"))
    override fun check(pokemon: Pokemon, queriedEntity: LivingEntity) = biomeCondition.fits(
        queriedEntity.world.getBiome(queriedEntity.blockPos).comp_349(), // Previously value()
        queriedEntity.world.registryManager.get(Registry.BIOME_KEY)
    )

    companion object {
        const val ADAPTER_VARIANT = "biome"
    }
}