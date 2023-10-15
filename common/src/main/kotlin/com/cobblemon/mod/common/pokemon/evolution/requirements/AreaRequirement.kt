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
import com.cobblemon.mod.common.util.codec.ExtraCodecs
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.entity.LivingEntity
import net.minecraft.util.math.Box

/**
 * A [EntityQueryRequirement] for when a [Pokemon] is expected to be in a certain area.
 *
 * @property box The [Box] expected to be in.
 * @author Licious
 * @since March 21st, 2022
 */
class AreaRequirement(val box: Box) : EntityQueryRequirement {

    override fun check(pokemon: Pokemon, queriedEntity: LivingEntity) = this.box.contains(queriedEntity.pos)

    override val variant: Variant<EvolutionRequirement> = VARIANT

    companion object {

        val CODEC: Codec<AreaRequirement> = RecordCodecBuilder.create { builder ->
            builder.group(
                ExtraCodecs.BOX_CODEC.fieldOf("box").forGetter(AreaRequirement::box)
            ).apply(builder, ::AreaRequirement)
        }

        internal val VARIANT: Variant<EvolutionRequirement> = Variant(cobblemonResource("area"), CODEC)

    }

}