package com.cablemc.pokemoncobbled.common.battles.pokemon

import com.cablemc.pokemoncobbled.common.api.moves.Move

/**
 * Wrapper for [Move] containing battle only variables
 *
 * @since January 16th, 2022
 * @author Deltric
 */
class BattleMove(val move : Move) {
    var disabled : Boolean = false
}