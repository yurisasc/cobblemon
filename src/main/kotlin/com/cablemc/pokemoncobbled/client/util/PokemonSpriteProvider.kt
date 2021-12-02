package com.cablemc.pokemoncobbled.client.util

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.resources.ResourceLocation

object PokemonSpriteProvider {
    fun getSprite(pokemon: Pokemon): ResourceLocation {
        return ResourceLocation(PokemonCobbled.MODID, "sprites/pokemon/${pokemon.species.name}-base.png")
    }
}