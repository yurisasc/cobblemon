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
import com.cobblemon.mod.common.api.spawning.condition.MoonPhase
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.pokemon.evolution.requirements.template.EntityQueryRequirement
import com.cobblemon.mod.common.util.cobblemonResource
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.entity.LivingEntity
import net.minecraft.util.StringIdentifiable

/**
 * An [EntityQueryRequirement] meant to check if the world is currently on the given [moonPhase].
 * This does not check if the time of day is the intended one for the moon to appear for that effect use in conjunction with [TimeRangeRequirement].
 *
 * @param moonPhase The [MoonPhase] being targeted.
 *
 * @author Licious
 * @since January 25th, 2023
 */
class MoonPhaseRequirement(val moonPhase: MoonPhase) : EntityQueryRequirement {

    override fun check(pokemon: Pokemon, queriedEntity: LivingEntity): Boolean {
        return try {
            val moonPhase = MoonPhase.ofWorld(queriedEntity.world)
            this.moonPhase == moonPhase
        } catch (e: IndexOutOfBoundsException) {
            false
        }
    }

    override val variant: Variant<EvolutionRequirement> = VARIANT

    companion object {

        val CODEC: Codec<MoonPhaseRequirement> = RecordCodecBuilder.create { builder ->
            builder.group(
                StringIdentifiable.createCodec(MoonPhase::values).fieldOf("moonPhase").forGetter(MoonPhaseRequirement::moonPhase)
            ).apply(builder, ::MoonPhaseRequirement)
        }

        internal val VARIANT: Variant<EvolutionRequirement> = Variant(cobblemonResource("moon_phase"), CODEC)

    }

}