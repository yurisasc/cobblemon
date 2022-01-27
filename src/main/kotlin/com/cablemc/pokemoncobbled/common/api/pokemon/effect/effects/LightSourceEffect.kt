package com.cablemc.pokemoncobbled.common.api.pokemon.effect.effects

import com.cablemc.pokemoncobbled.common.api.pokemon.effect.ShoulderEffect
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import net.minecraft.server.level.ServerPlayer

object LightSourceEffect: ShoulderEffect() {
    override val name: String
        get() = "light_source"

    override fun applyEffect(pokemonEntity: PokemonEntity?, player: ServerPlayer) {
        println("Applying effect...")
    }

    override fun removeEffect(pokemonEntity: PokemonEntity?, player: ServerPlayer) {
        println("Removing effect...")
    }
}