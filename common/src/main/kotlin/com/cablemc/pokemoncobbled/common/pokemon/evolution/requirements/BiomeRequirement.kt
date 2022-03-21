package com.cablemc.pokemoncobbled.common.pokemon.evolution.requirements

import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.pokemon.evolution.requirements.template.PositionQueryRequirement
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.biome.Biome

/**
 * A [PositionQueryRequirement] for when a [Pokemon] is expected to be in a certain [Biome].
 *
 * @property resourceLocation The [ResourceLocation] of the [Biome] the queried entity is expected to be in.
 * @author Licious
 * @since March 21st, 2022
 */
class BiomeRequirement(val resourceLocation: ResourceLocation) : PositionQueryRequirement() {

    override fun check(pokemon: Pokemon, queriedEntity: LivingEntity): Boolean {
        val biome = queriedEntity.level.getBiome(queriedEntity.onPos)
        return biome.unwrapKey().map { key -> key.location() == this.resourceLocation }.orElse(false)
    }

    companion object {

        internal const val ADAPTER_VARIANT = "biome"

    }

}