package com.cobblemon.mod.common.api.pokemon.breeding

import com.cobblemon.mod.common.pokemon.Pokemon

data class Egg (
    val hatchedPokemon: Pokemon,
    val pattern: EggPattern,
    val primaryColor: String,
    val secondaryColor: String
) {

}