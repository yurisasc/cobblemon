package com.cablemc.pokemoncobbled.common.api.battles.model

class BattlePlayer {
    var name: String = ""
    val team: MutableList<BattlePokemon> = mutableListOf()
}