/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.advancement.criterion

import com.cobblemon.mod.common.util.asIdentifierDefaultingNamespace
import com.google.gson.JsonObject
import net.minecraft.predicate.entity.LootContextPredicate
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

/**
 * A context that is used when you require a [CountableContext] along with some type string.
 *
 * @author Hiroku
 * @since November 4th, 2022
 */
open class CountablePokemonTypeContext(times: Int, var type: String) : CountableContext(times)

class CaughtPokemonCriterionCondition(id: Identifier, predicate: LootContextPredicate) : CountableCriterionCondition<CountablePokemonTypeContext>(id, predicate) {
    var type = "any"
    override fun toJson(json: JsonObject) {
        super.toJson(json)
        json.addProperty("type", type)
    }

    override fun fromJson(json: JsonObject) {
        super.fromJson(json)
        var stored = json.get("type")?.asString
        if (stored != null && stored != "any") {
            stored = stored.asIdentifierDefaultingNamespace().toString()
        }
        type = stored ?: "any"
    }

    override fun matches(player: ServerPlayerEntity, context: CountablePokemonTypeContext): Boolean {
        return super.matches(player, context) && (context.type == type || type == "any")
    }
}