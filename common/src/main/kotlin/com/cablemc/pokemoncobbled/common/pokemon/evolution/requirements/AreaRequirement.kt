package com.cablemc.pokemoncobbled.common.pokemon.evolution.requirements

import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.pokemon.evolution.requirements.template.PositionQueryRequirement
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.phys.AABB

/**
 * A [PositionQueryRequirement] for when a [Pokemon] is expected to be in a certain area.
 *
 * @property bounds The [AABB] expected to be in.
 * @author Licious
 * @since March 21st, 2022
 */
class AreaRequirement(val bounds: AABB) : PositionQueryRequirement() {

    override fun check(pokemon: Pokemon, queriedEntity: LivingEntity): Boolean {
        return this.bounds.contains(queriedEntity.position())
    }

    companion object {

        internal const val ADAPTER_VARIANT = "area"

    }

}