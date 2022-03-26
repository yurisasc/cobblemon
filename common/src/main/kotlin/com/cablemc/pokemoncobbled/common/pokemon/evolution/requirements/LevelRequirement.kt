package com.cablemc.pokemoncobbled.common.pokemon.evolution.requirements

import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.pokemon.evolution.requirements.template.EntityQueryRequirement
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.Level

/**
 * A [EntityQueryRequirement] for when a [Pokemon] is expected to be in a [Level].
 *
 * @property resourceLocation The [ResourceLocation] of the [Level] the queried entity is expected to be in.
 * @author Licious
 * @since March 21st, 2022
 */
class LevelRequirement(val resourceLocation: ResourceLocation) : EntityQueryRequirement() {

    override fun check(pokemon: Pokemon, queriedEntity: LivingEntity): Boolean {
        return queriedEntity.level.dimension().location() == this.resourceLocation
    }

    companion object {

        internal const val ADAPTER_VARIANT = "world"

    }

}