package com.cablemc.pokemoncobbled.common.config.starter

import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonProperties
import net.minecraft.text.MutableText
import net.minecraft.text.Text

data class StarterCategory(
    val name: String,
    val displayName: MutableText,
    val pokemon: List<PokemonProperties>
)
