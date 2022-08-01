package com.cablemc.pokemoncobbled.common.config.starter

import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonProperties
import com.cablemc.pokemoncobbled.common.pokemon.RenderablePokemon
import com.cablemc.pokemoncobbled.common.util.asTranslated

data class StarterCategory(
    val name: String,
    val displayName: String,
    val pokemon: List<PokemonProperties>
) {
    fun asRenderableStarterCategory() = RenderableStarterCategory(name, displayName, pokemon.map { it.asRenderablePokemon() })
}

data class RenderableStarterCategory(
    val name: String,
    val displayName: String,
    val pokemon: List<RenderablePokemon>
) {
    val displayNameText = displayName.asTranslated()
}
