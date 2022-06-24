package com.cablemc.pokemoncobbled.common.config.starter

import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonProperties
import net.minecraft.text.Text

data class StarterCategory(
    val name: String,
    val displayName: Text,
    val pokemon: List<PokemonProperties>
)
