/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.advancement.criterion

import com.cobblemon.mod.common.util.asIdentifierDefaultingNamespace
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.predicate.entity.EntityPredicate
import net.minecraft.predicate.entity.LootContextPredicate
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.dynamic.Codecs
import java.util.Optional

class PokemonInteractContext(val type: Identifier, val item: Identifier)

class PokemonInteractCriterion(
    playerCtx: Optional<LootContextPredicate>,
    val type: Optional<String>,
    val item: Optional<String>
): SimpleCriterionCondition<PokemonInteractContext>(playerCtx) {
    companion object {
        val CODEC: Codec<PokemonInteractCriterion> = RecordCodecBuilder.create { it.group(
            Codecs.createStrictOptionalFieldCodec(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC, "player").forGetter(PokemonInteractCriterion::playerCtx),
            Codecs.createStrictOptionalFieldCodec(Codec.STRING, "type").forGetter(PokemonInteractCriterion::type),
            Codecs.createStrictOptionalFieldCodec(Codec.STRING, "item").forGetter(PokemonInteractCriterion::item)
        ).apply(it, ::PokemonInteractCriterion) }
    }

    override fun matches(player: ServerPlayerEntity, context: PokemonInteractContext): Boolean {
        val otherType = this.type.orElse("any")
        val otherItem = this.item.orElse("any")
        return (context.type == otherType.asIdentifierDefaultingNamespace() || otherType == "any") && (context.type == otherItem.asIdentifierDefaultingNamespace() || otherItem == "any")
    }
}