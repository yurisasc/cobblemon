package com.cablemc.pokemoncobbled.client.gui.summary.mock

data class PokemonMove(
    val name: String,
    val type: ElementalType,
    val category: DamageCategory,
    val desc: String,
    val accuracy: Double,
    val power: Double,
    val effect: Double,
    val curPp: Int,
    val maxPp: Int,
) {

}
