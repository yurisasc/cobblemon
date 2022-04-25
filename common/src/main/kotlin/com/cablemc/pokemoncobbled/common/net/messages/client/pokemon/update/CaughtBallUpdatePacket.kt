package com.cablemc.pokemoncobbled.common.net.messages.client.pokemon.update

import com.cablemc.pokemoncobbled.common.api.pokeball.PokeBalls
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.resources.ResourceLocation

class CaughtBallUpdatePacket() : StringUpdatePacket() {
    constructor(pokemon: Pokemon, value: String): this() {
        this.setTarget(pokemon)
        this.value = value
    }

    override fun set(pokemon: Pokemon, value: String) {
        val pokeBall = PokeBalls.getPokeBall(ResourceLocation(value))
        if (pokeBall != null) {
            pokemon.caughtBall = pokeBall
        }
    }
}