package com.cablemc.pokemoncobbled.common.api.moves

import com.cablemc.pokemoncobbled.common.api.moves.MoveLoader.loadFromAssets

/**
 * Registry for all known Moves
 */
object Moves {
    private val allMoves = mutableListOf<MoveTemplate>()

    // START - Normal Moves
    val TACKLE = register(loadFromAssets("tackle"))
    // END - Normal Moves

    // START - Flying Moves
    val AERIAL_ACE = register(loadFromAssets("aerial_ace"))
    val AIR_SLASH = register(loadFromAssets("air_slash"))
    // END - Flying Moves

    // START - Fighting Moves
    val AURA_SPHERE = register(loadFromAssets("aura_sphere"))
    // END - Fighting Moves

    fun register(moveTemplate: MoveTemplate): MoveTemplate {
        allMoves.add(moveTemplate)
        return moveTemplate
    }

    fun getByName(name: String): MoveTemplate? {
        return allMoves.firstOrNull { moveTemplate -> moveTemplate.name.equals(name, ignoreCase = true) }
    }

    fun count() = allMoves.size
}