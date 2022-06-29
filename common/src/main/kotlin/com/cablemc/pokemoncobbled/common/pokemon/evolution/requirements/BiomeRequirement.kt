package com.cablemc.pokemoncobbled.common.pokemon.evolution.requirements

import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.pokemon.evolution.requirements.template.EntityQueryRequirement
import net.minecraft.entity.LivingEntity
import net.minecraft.util.Identifier
import net.minecraft.world.biome.Biome
/**
 * A [EntityQueryRequirement] for when a [Pokemon] is expected to be in a certain [Biome].
 *
 * @property identifier The [Identifier] of the [Biome] the queried entity is expected to be in.
 * @author Licious
 * @since March 21st, 2022
 */
class BiomeRequirement(val identifier: Identifier) : EntityQueryRequirement() {

    override fun check(pokemon: Pokemon, queriedEntity: LivingEntity): Boolean {
        val biome = queriedEntity.world.getBiome(queriedEntity.blockPos)
        return biome.key.map { key -> key.value == this.identifier }.orElse(false)
    }

    companion object {

        internal const val ADAPTER_VARIANT = "biome"

    }

}