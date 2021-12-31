package com.cablemc.pokemoncobbled.client.util

import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.resources.ResourceLocation

object PokemonSpriteProvider {
    fun getSprite(pokemon: Pokemon): ResourceLocation {
        return cobbledResource("sprites/pokemon/${pokemon.species.name}-base.png")
    }
}