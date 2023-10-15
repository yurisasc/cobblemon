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
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.pokemon.evolution.requirements.template.EntityQueryRequirement
import com.cobblemon.mod.common.util.cobblemonResource
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.entity.LivingEntity

class WeatherRequirement(val isRaining: Boolean, val isThundering: Boolean) : EntityQueryRequirement {

    override fun check(pokemon: Pokemon, queriedEntity: LivingEntity): Boolean {
        val world = queriedEntity.world
        return this.isRaining == world.isRaining && this.isThundering == world.isThundering
    }

    override val variant: Variant<EvolutionRequirement> = VARIANT

    companion object {

        val CODEC: Codec<WeatherRequirement> = RecordCodecBuilder.create { builder ->
            builder.group(
                Codec.BOOL.fieldOf("isRaining").forGetter(WeatherRequirement::isRaining),
                Codec.BOOL.fieldOf("isThundering").forGetter(WeatherRequirement::isThundering)
            ).apply(builder, ::WeatherRequirement)
        }

        internal val VARIANT: Variant<EvolutionRequirement> = Variant(cobblemonResource("weather"), CODEC)

    }

}