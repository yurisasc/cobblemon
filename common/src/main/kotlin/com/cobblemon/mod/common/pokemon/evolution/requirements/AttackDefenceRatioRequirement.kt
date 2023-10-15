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
import com.cobblemon.mod.common.util.cobblemonResource
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.util.StringIdentifiable

class AttackDefenceRatioRequirement(val ratio: AttackDefenceRatio) : EvolutionRequirement {

    override fun check(pokemon: Pokemon): Boolean {
        return when (ratio) {
            AttackDefenceRatio.ATTACK_HIGHER -> pokemon.attack > pokemon.defence
            AttackDefenceRatio.DEFENCE_HIGHER -> pokemon.defence > pokemon.attack
            AttackDefenceRatio.EQUAL -> pokemon.attack == pokemon.defence
        }
    }

    override val variant: Variant<EvolutionRequirement> = VARIANT

    enum class AttackDefenceRatio : StringIdentifiable {
        ATTACK_HIGHER,
        DEFENCE_HIGHER,
        EQUAL;

        override fun asString(): String = this.name
    }

    companion object {

        val CODEC: Codec<AttackDefenceRatioRequirement> = RecordCodecBuilder.create { builder ->
            builder.group(
                StringIdentifiable.createCodec(AttackDefenceRatio::values).fieldOf("ratio").forGetter(AttackDefenceRatioRequirement::ratio)
            ).apply(builder, ::AttackDefenceRatioRequirement)
        }

        internal val VARIANT: Variant<EvolutionRequirement> = Variant(cobblemonResource("attack_defence_ratio"), CODEC)

    }

}