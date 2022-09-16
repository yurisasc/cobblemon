/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.pokemon.effects

import com.cablemc.pokemoncobbled.common.api.pokemon.effect.ShoulderEffect
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.google.gson.JsonObject
import net.minecraft.server.network.ServerPlayerEntity

class LightSourceEffect: ShoulderEffect {
    private var test = "AAA"

    override fun applyEffect(pokemon: Pokemon, player: ServerPlayerEntity, isLeft: Boolean) {
        println("Applying effect... $test")
    }

    override fun removeEffect(pokemon: Pokemon, player: ServerPlayerEntity, isLeft: Boolean) {
        println("Removing effect...")
    }

    override fun serialize(json: JsonObject): JsonObject {
        json.addProperty("Test", test)
        return json
    }

    override fun deserialize(json: JsonObject): ShoulderEffect {
        test = json.get("Test").asString
        return this
    }
}