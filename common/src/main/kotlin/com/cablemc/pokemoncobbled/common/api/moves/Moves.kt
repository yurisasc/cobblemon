package com.cablemc.pokemoncobbled.common.api.moves

import com.cablemc.pokemoncobbled.common.api.moves.MoveLoader.loadFromAssets
import com.cablemc.pokemoncobbled.common.api.moves.categories.DamageCategories
import com.cablemc.pokemoncobbled.common.api.moves.categories.DamageCategory
import com.cablemc.pokemoncobbled.common.api.types.ElementalTypes

/**
 * Registry for all known Moves
 */
object Moves {

    private val allMoves = hashMapOf<String, MoveTemplate>()

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
    val SPLASH = register(MoveTemplate("splash", ElementalTypes.NORMAL, DamageCategories.SPECIAL, 0.0, 0.0, 0.0, 10))

    fun register(moveTemplate: MoveTemplate): MoveTemplate {
        this.allMoves[moveTemplate.name.lowercase()] = moveTemplate
        return moveTemplate
    }

    fun getByName(name: String) = this.allMoves[name.lowercase()]

    fun count() = this.allMoves.size
}