/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokemon.evolution.requirements

import com.cobblemon.mod.common.api.pokemon.evolution.adapters.Variant
import com.cobblemon.mod.common.api.pokemon.evolution.requirement.EvolutionRequirement
import com.cobblemon.mod.common.api.spawning.TimeRange
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.pokemon.evolution.requirements.template.EntityQueryRequirement
import com.cobblemon.mod.common.util.cobblemonResource
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.entity.LivingEntity

/**
 * An [EvolutionRequirement] for when the current time must be in the provided [TimeRange].
 *
 * @property range The required [TimeRange],
 * @author Licious
 * @since March 26th, 2022
 */
class TimeRangeRequirement(val range: TimeRange) : EntityQueryRequirement {

    override fun check(pokemon: Pokemon, queriedEntity: LivingEntity) = this.range.contains((queriedEntity.world.timeOfDay % DAY_DURATION).toInt())

    override val variant: Variant<EvolutionRequirement> = VARIANT

    companion object {

        val CODEC: Codec<TimeRangeRequirement> = RecordCodecBuilder.create { builder ->
            builder.group(
                TimeRange.CODEC.fieldOf("range").forGetter(TimeRangeRequirement::range)
            ).apply(builder, ::TimeRangeRequirement)
        }

        internal val VARIANT: Variant<EvolutionRequirement> = Variant(cobblemonResource("time_range"), CODEC)

        private const val DAY_DURATION = 24000

    }
}