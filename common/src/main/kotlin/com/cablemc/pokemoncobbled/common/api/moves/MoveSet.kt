package com.cablemc.pokemoncobbled.common.api.moves

import com.cablemc.pokemoncobbled.common.util.DataKeys
import com.google.gson.JsonObject
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.network.FriendlyByteBuf

class MoveSet {
    val moves = arrayOfNulls<Move>(MOVE_COUNT)

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
        if (pos < 0 || pos > MOVE_COUNT - 1)
            return
        moves[pos] = move
    }

    /**
     * Swaps the position of the two given Moves indices
     */
    fun swapMove(pos1: Int, pos2: Int) {
        // The fact that this works should be a fuckin crime wth
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

    fun saveToJSON(json: JsonObject): JsonObject {
        for ((i, move) in moves.filterNotNull().withIndex()) {
            val moveJSON = move.saveToJSON(JsonObject())
            json.add(DataKeys.POKEMON_MOVESET + i, moveJSON)
        }
        return json
    }

    fun add(move: Move) {
        for (i in 0 until MOVE_COUNT) {
            if (moves[i] == null) {
                moves[i] = move
                return
            }
        }
    }

    companion object {
        const val MOVE_COUNT = 4

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

        fun loadFromJSON(json: JsonObject): MoveSet {
            val moveSet = MoveSet()
            for (i in 0 until 4) {
                val moveJSON = json.get(DataKeys.POKEMON_MOVESET + i) ?: continue
                val move = Move.loadFromJSON(moveJSON.asJsonObject)
                moveSet.add(move)
            }
            return moveSet
        }
    }
}