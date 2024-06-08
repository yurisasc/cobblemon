/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.advancement.criterion

import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.pokemon.Pokemon
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.predicate.entity.EntityPredicate
import net.minecraft.predicate.entity.LootContextPredicate
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.dynamic.Codecs
import java.util.Optional

class PokemonCriterion(
    playerCtx: Optional<LootContextPredicate>,
    val properties: PokemonProperties
): SimpleCriterionCondition<Pokemon>(playerCtx) {

    companion object {
        val CODEC: Codec<PokemonCriterion> = RecordCodecBuilder.create { it.group(
            EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(PokemonCriterion::playerCtx),
            PokemonProperties.CODEC.optionalFieldOf("properties", PokemonProperties()).forGetter(PokemonCriterion::properties)
        ).apply(it, ::PokemonCriterion) }
    }

    override fun matches(player: ServerPlayerEntity, context: Pokemon): Boolean {
        return properties.matches(context)
    }
}