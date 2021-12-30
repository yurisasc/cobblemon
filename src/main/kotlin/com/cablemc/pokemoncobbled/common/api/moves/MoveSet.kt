package com.cablemc.pokemoncobbled.common.api.moves

import com.cablemc.pokemoncobbled.common.util.DataKeys
import net.minecraft.nbt.CompoundTag

class MoveSet {

    val moves = arrayOfNulls<Move>(4)

    /**
     * So no Pokémon can end up with no Moves assigned...
     */
    init {
        if(moves.filterNotNull().isEmpty()) {
            moves[0] = Moves.TACKLE.create()
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
        println("Set Move on $pos to ${move.name}")
    }

    /**
     * Swaps the position of the two given Moves indices
     */
    fun swapMove(pos1: Int, pos2: Int) {
        moves[pos1] = moves[pos2].also {
            moves[pos2] = moves[pos1]
        }
    }

    fun saveToNBT(nbt: CompoundTag): CompoundTag {
        moves.forEachIndexed { index, move ->  move?.run { nbt.put(index.toString(), this.saveToNBT(CompoundTag())) } }
        return nbt
    }

    companion object {
        fun loadFromNBT(nbt: CompoundTag): MoveSet {
            val moveSetComp = nbt.getCompound(DataKeys.POKEMON_MOVESET)
            val moveSet = MoveSet()
            for(i in 0..3) {
                val moveComp = moveSetComp.getCompound(i.toString())
                moveComp.run {
                    moveSet.setMove(
                        pos = i,
                        move = Move.loadFromNBT(this))
                }
            }
            println("Passing MoveSet consisting of ${moveSet.moves[1]?.name} and ${moveSet.moves[2]?.name}")
            return moveSet
        }
    }
}