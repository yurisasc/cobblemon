/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.advancement.criterion

import com.cobblemon.mod.common.pokemon.Pokemon
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.predicate.entity.EntityPredicate
import net.minecraft.predicate.entity.LootContextPredicate
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.dynamic.Codecs
import java.util.Optional

class LevelUpContext(val level: Int, val pokemon: Pokemon)

class LevelUpCriterion(
    playerCtx: Optional<LootContextPredicate>,
    val level: Int,
    val evolved: Boolean
): SimpleCriterionCondition<LevelUpContext>(playerCtx) {

    companion object {
        val CODEC: Codec<LevelUpCriterion> = RecordCodecBuilder.create { it.group(
            Codecs.createStrictOptionalFieldCodec(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC, "player").forGetter(LevelUpCriterion::playerCtx),
            Codecs.createStrictOptionalFieldCodec(Codec.INT, "level", 0).forGetter(LevelUpCriterion::level),
            Codecs.createStrictOptionalFieldCodec(Codec.BOOL, "evolved", true).forGetter(LevelUpCriterion::evolved)
        ).apply(it, ::LevelUpCriterion) }
    }

    override fun matches(player: ServerPlayerEntity, context: LevelUpContext): Boolean {
        val preEvo = context.pokemon.preEvolution == null
        val hasEvolution = !context.pokemon.evolutions.none()
        var evolutionCheck = true
        if (preEvo || hasEvolution) {
            evolutionCheck = preEvo != hasEvolution
        }
        return level == context.level && evolutionCheck == evolved
    }

}

/*class LevelUpCriterionCondition(id: Identifier, entity: LootContextPredicate) : SimpleCriterionCondition<LevelUpContext>(id, entity) {
    var level = 0
    var evolved = true
    override fun toJson(json: JsonObject) {
        json.addProperty("level", level)
        json.addProperty("has_evolved", evolved)
    }

    override fun fromJson(json: JsonObject) {
        level = json.get("level")?.asInt ?: 0
        evolved = json.get("has_evolved")?.asBoolean ?: true
    }

    override fun matches(player: ServerPlayerEntity, context: LevelUpContext): Boolean {
        val preEvo = context.pokemon.preEvolution == null
        val hasEvolution = !context.pokemon.evolutions.none()
        var evolutionCheck = true
        if (preEvo || hasEvolution) {
            evolutionCheck = !(preEvo == hasEvolution)
        }
        return level == context.level && evolutionCheck == evolved
    }
}*/