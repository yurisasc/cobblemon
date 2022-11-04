/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.advancement.criterion

import com.cablemc.pokemod.common.api.pokemon.PokemonProperties
import com.cablemc.pokemod.common.pokemon.Pokemon
import com.google.gson.JsonObject
import net.minecraft.predicate.entity.EntityPredicate.Extended
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

/**
 * A criteria that is triggered when a player picks a starter.
 *
 * @author Licious, Hiroku
 * @since October 26th, 2022
 */
class PickStarterCriterionCondition(id: Identifier, entity: Extended) : SimpleCriterionCondition<Pokemon>(id, entity) {
    var properties = PokemonProperties()
    override fun toJson(json: JsonObject) {
        json.addProperty("properties", properties.originalString)
    }

    override fun fromJson(json: JsonObject) {
        properties = PokemonProperties.parse(json.get("properties")?.asString ?: "")
    }

    override fun matches(player: ServerPlayerEntity, context: Pokemon) = properties.matches(context)
}