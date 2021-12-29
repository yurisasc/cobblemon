package com.cablemc.pokemoncobbled.common.api.moves

class MoveSet {

    val moves = arrayOfNulls<Move>(4)

    /**
     * So no Pokémon can end up with no Moves assigned...
     */
    init {
        if(moves.isEmpty()) {
            moves[0] = Moves.TACKLE.move()
        }
    }

    /**
     * Gets all Moves from the Pokémon but skips null Moves
     */
    fun getMoves(): List<Move> {
        return moves.filterNotNull()
    }

    /**
     * Sets the given Move to given position
     */
    fun setMove(pos: Int, move: Move) {
        if(pos < 0 || pos > 3)
            return
        moves[pos] = move
    }

    /**
     * Swaps the position of the two given Moves indices
     */
    fun swapMove(pos1: Int, pos2: Int) {
        moves[pos1] = moves[pos2].also {
            moves[pos2] = moves[pos1]
        }
    }

}