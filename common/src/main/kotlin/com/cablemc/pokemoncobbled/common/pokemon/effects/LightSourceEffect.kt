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
}