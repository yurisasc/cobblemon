package com.cablemc.pokemoncobbled.common.api.moves

import com.cablemc.pokemoncobbled.common.utils.DataKeys
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.network.FriendlyByteBuf

class MoveSet {

    val moves = arrayOfNulls<Move>(4)

    /**
     * So no Pokémon can end up with no Moves assigned...
     */
    init {
        if (moves.filterNotNull().isEmpty()) {
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
        if (pos < 0 || pos > 3)
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

    /**
     * Returns a ListTag containing all the Moves
     */
    fun getNBT(): ListTag {
        val listTag = ListTag()
        listTag.addAll(getMoves().map { it.saveToNBT(CompoundTag()) })
        return listTag
    }

    /**
     * Writes the MoveSet to Buffer
     */
    fun saveToBuffer(buffer: FriendlyByteBuf) {
        buffer.writeInt(getMoves().size)
        getMoves().forEach {
            it.saveToBuffer(buffer)
        }
    }

    companion object {
        /**
         * Returns a MoveSet built from given NBT
         */
        fun loadFromNBT(nbt: CompoundTag): MoveSet {
            val moveSet = MoveSet()
            nbt.getList(DataKeys.POKEMON_MOVESET, Tag.TAG_COMPOUND.toInt()).forEachIndexed { index, tag ->
                moveSet.setMove(index, Move.loadFromNBT(tag as CompoundTag))
            }
            return moveSet
        }

        /**
         * Returns a MoveSet build from given Buffer
         */
        fun loadFromBuffer(buffer: FriendlyByteBuf): MoveSet {
            val amountMoves = buffer.readInt()
            val moveSet = MoveSet()
            for (i in 0 until amountMoves) {
                moveSet.setMove(i, Move.loadFromBuffer(buffer))
            }
            return moveSet
        }
    }
}