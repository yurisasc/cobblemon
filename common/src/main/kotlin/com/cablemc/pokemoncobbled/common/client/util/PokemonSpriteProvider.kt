package com.cablemc.pokemoncobbled.common.client.util

import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.util.Identifier

object PokemonSpriteProvider {
    fun getSprite(pokemon: Pokemon): Identifier {
        // Check for shiny
        val shinySprite = cobbledResource("sprites/pokemon/${pokemon.species.name}-shiny.png")
        if (pokemon.shiny && shinySprite.exists()) {
            return shinySprite
        }
        // If nothing else return base species sprite
        return cobbledResource("sprites/pokemon/${pokemon.species.name}-base.png")
    }
}