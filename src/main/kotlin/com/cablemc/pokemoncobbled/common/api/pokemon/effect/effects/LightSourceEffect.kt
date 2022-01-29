package com.cablemc.pokemoncobbled.common.api.pokemon.effect.effects

import com.cablemc.pokemoncobbled.common.api.pokemon.effect.ShoulderEffect
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.google.gson.JsonObject
import net.minecraft.server.level.ServerPlayer

object LightSourceEffect: ShoulderEffect {
    override val name = "light_source"

    private var test = "AAA"

    override fun applyEffect(pokemonEntity: PokemonEntity?, player: ServerPlayer, isLeft: Boolean) {
        println("Applying effect... $test")
    }

    override fun removeEffect(pokemonEntity: PokemonEntity?, player: ServerPlayer, isLeft: Boolean) {
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