package com.cablemc.pokemoncobbled.common.pokemon.evolution.requirements

import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.pokemon.evolution.requirements.template.EntityQueryRequirement
import net.minecraft.entity.LivingEntity
import net.minecraft.util.math.Box

/**
 * A [EntityQueryRequirement] for when a [Pokemon] is expected to be in a certain area.
 *
 * @property box The [Box] expected to be in.
 * @author Licious
 * @since March 21st, 2022
 */
class AreaRequirement(val box: Box) : EntityQueryRequirement() {

    override fun check(pokemon: Pokemon, queriedEntity: LivingEntity): Boolean {
        return this.box.contains(queriedEntity.pos)
    }

    companion object {

        internal const val ADAPTER_VARIANT = "area"

    }

}