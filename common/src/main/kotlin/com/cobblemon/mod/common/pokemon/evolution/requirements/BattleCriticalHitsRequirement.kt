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
import com.cobblemon.mod.common.battles.BattleRegistry
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.cobblemonResource
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import kotlin.jvm.optionals.getOrNull

/**
 * An [EvolutionRequirement] for a certain amount of critical hits in a single battle.
 *
 * @param amount The amount of critical hits required.
 *
 * @author Licious
 * @since October 2nd, 2022
 */
class BattleCriticalHitsRequirement(val amount: Int) : EvolutionRequirement {

    override fun check(pokemon: Pokemon): Boolean {
        val pokemonEntity = pokemon.entity ?: return false
        val battleId = pokemonEntity.battleId.get().getOrNull() ?: return false
        val battle = BattleRegistry.getBattle(battleId) ?: return false
        battle.actors.forEach { actor ->
            actor.pokemonList.forEach { battlePokemon ->
                if (battlePokemon.effectedPokemon.uuid == pokemon.uuid) {
                    return battlePokemon.criticalHits >= this.amount
                }
            }
        }
        return false
    }

    override val variant: Variant<EvolutionRequirement> = VARIANT

    companion object {

        val CODEC: Codec<BattleCriticalHitsRequirement> = RecordCodecBuilder.create { builder ->
            builder.group(
                Codec.INT.fieldOf("amount").forGetter(BattleCriticalHitsRequirement::amount)
            ).apply(builder, ::BattleCriticalHitsRequirement)
        }

        internal val VARIANT: Variant<EvolutionRequirement> = Variant(cobblemonResource("battle_critical_hits"), CODEC)

    }

}