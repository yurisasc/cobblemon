package com.cablemc.pokemoncobbled.common.api.moves

import com.cablemc.pokemoncobbled.common.api.moves.MoveLoader.loadFromAssets

object Moves {
    private val allMoves = mutableListOf<MoveTemplate>()

    // START - Normal Moves
    val TACKLE = register(loadFromAssets("tackle"))
    // END - Normal Moves

    fun register(moveTemplate: MoveTemplate): MoveTemplate {
        allMoves.add(moveTemplate)
        return moveTemplate
    }

    fun getByName(name: String): MoveTemplate? {
        return allMoves.firstOrNull { moveTemplate -> moveTemplate.name.equals(name, ignoreCase = true) }
    }

    fun count() = allMoves.size
}