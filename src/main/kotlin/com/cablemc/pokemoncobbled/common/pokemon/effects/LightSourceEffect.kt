package com.cablemc.pokemoncobbled.common.pokemon.effects

import com.cablemc.pokemoncobbled.common.api.pokemon.effect.ShoulderEffect
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.google.gson.JsonObject
import net.minecraft.server.level.ServerPlayer

class LightSourceEffect: ShoulderEffect {
    private var test = "AAA"

    override fun applyEffect(pokemon: Pokemon, player: ServerPlayer, isLeft: Boolean) {
        println("Applying effect... $test")
    }

    override fun removeEffect(pokemon: Pokemon, player: ServerPlayer, isLeft: Boolean) {
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