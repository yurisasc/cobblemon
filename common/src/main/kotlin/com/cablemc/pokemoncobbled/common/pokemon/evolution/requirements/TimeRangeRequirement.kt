package com.cablemc.pokemoncobbled.common.pokemon.evolution.requirements

import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonProperties
import com.cablemc.pokemoncobbled.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cablemc.pokemoncobbled.common.api.spawning.condition.TimeRange
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.pokemon.evolution.requirements.template.EntityQueryRequirement
import net.minecraft.world.entity.LivingEntity

/**
 * An [EvolutionRequirement] for when the current time must be in the provided [TimeRange].
 *
 * @property range The required [TimeRange],
 * @author Licious
 * @since March 26th, 2022
 */
class TimeRangeRequirement(val range: TimeRange) : EntityQueryRequirement() {

    override fun check(pokemon: Pokemon, queriedEntity: LivingEntity) = this.range.contains((queriedEntity.level.dayTime() % 24000).toInt())

    companion object {

        internal const val ADAPTER_VARIANT = "time_range"
        private const val DAY_DURATION = 24000

    }

}