/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.advancement.criterion

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.predicate.entity.EntityPredicate
import net.minecraft.predicate.entity.LootContextPredicate
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.dynamic.Codecs
import java.util.Optional

/**
 * A context that is used when you require a [CountableContext] along with some type string.
 *
 * @author Hiroku
 * @since November 4th, 2022
 */
open class CountablePokemonTypeContext(times: Int, var type: String) : CountableContext(times)

class CaughtPokemonCriterion(
    playerCtx: Optional<LootContextPredicate>,
    val type: String,
    count: Int
): CountableCriterion<CountablePokemonTypeContext>(playerCtx, count) {

    companion object {
        val CODEC: Codec<CaughtPokemonCriterion> = RecordCodecBuilder.create { it.group(
            Codecs.createStrictOptionalFieldCodec(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC, "player").forGetter(CaughtPokemonCriterion::playerCtx),
            Codecs.createStrictOptionalFieldCodec(Codec.STRING, "type", "any").forGetter(CaughtPokemonCriterion::type),
            Codecs.createStrictOptionalFieldCodec(Codec.INT, "count", 0).forGetter(CaughtPokemonCriterion::count)
        ).apply(it, ::CaughtPokemonCriterion) }
    }

    override fun matches(player: ServerPlayerEntity, context: CountablePokemonTypeContext): Boolean {
        return super.matches(player, context) && (context.type == type || type == "any")
    }
}

//class CaughtPokemonCriterionCondition(id: Identifier, predicate: LootContextPredicate) : CountableCriterionCondition<CountablePokemonTypeContext>(id, predicate) {
//    var type = "any"
//    override fun toJson(json: JsonObject) {
//        super.toJson(json)
//        json.addProperty("type", type)
//    }
//
//    override fun fromJson(json: JsonObject) {
//        super.fromJson(json)
//        type = json.get("type")?.asString ?: "any"
//    }
//
//    override fun matches(player: ServerPlayerEntity, context: CountablePokemonTypeContext): Boolean {
//        return super.matches(player, context) && (context.type == type || type == "any")
//    }
//}