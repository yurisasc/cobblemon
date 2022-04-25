package com.cablemc.pokemoncobbled.common.pokemon

import com.cablemc.pokemoncobbled.common.api.moves.MoveTemplate

/**
 * The result of adding experience to a [Pokemon]. This contains information
 * about any level changes and any new moves that were learned.
 *
 * @author Hiroku
 * @since April 18th, 2022
 */
data class AddExperienceResult(
    val oldLevel: Int,
    val newLevel: Int,
    val newMoves: Set<MoveTemplate>
)