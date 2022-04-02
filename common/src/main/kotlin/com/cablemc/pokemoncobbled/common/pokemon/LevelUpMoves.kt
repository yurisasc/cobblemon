package com.cablemc.pokemoncobbled.common.pokemon

import com.cablemc.pokemoncobbled.common.api.moves.MoveTemplate

class LevelUpMoves : HashMap<Int, MutableList<MoveTemplate>>() {
    fun getMovesUpTo(level: Int) = entries.filter { it.key <= level }.sortedBy { it.key }.flatMap { it.value }.distinct()
}