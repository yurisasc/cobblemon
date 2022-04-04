package com.cablemc.pokemoncobbled.common.api.moves

import com.cablemc.pokemoncobbled.common.api.reactive.SimpleObservable
import com.cablemc.pokemoncobbled.common.util.DataKeys
import com.google.gson.JsonObject
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.network.FriendlyByteBuf

class MoveSet : Iterable<Move> {
    val observable = SimpleObservable<MoveSet>()
    private var emit = true

    private val moves = arrayOfNulls<Move>(MOVE_COUNT)


    override fun iterator() = moves.filterNotNull().iterator()

    operator fun get(index: Int) = index.takeIf { it in 0 until MOVE_COUNT }?.let { moves[it] }

    /**
     * Gets all Moves from the Pokémon but skips null Moves
     */
    fun getMoves(): List<Move> {
        return moves.filterNotNull()
    }

    /**
     * Sets the given Move to given position
     */
    fun setMove(pos: Int, move: Move?) {
        if (pos < 0 || pos > MOVE_COUNT - 1)
            return
        moves[pos] = move
        update()
    }

    fun copyFrom(other: MoveSet) {
        doWithoutEmitting {
            clear()
            other.getMoves().forEach { add(it) }
        }
        update()
    }

    fun clear() {
        doWithoutEmitting {
            for (i in 0 until MOVE_COUNT){
                setMove(i, null)
            }
        }
        update()
    }

    /**
     * Swaps the position of the two given Moves indices
     */
    fun swapMove(pos1: Int, pos2: Int) {
        // The fact that this works should be a fuckin crime wth
        moves[pos1] = moves[pos2].also {
            moves[pos2] = moves[pos1]
        }
        update()
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
                update()
                return
            }
        }
    }

    fun update() {
        if (emit) {
            observable.emit(this)
        }
    }
    fun doWithoutEmitting(action: () -> Unit) {
        emit = false
        action()
        emit = true
    }


    /**
     * Returns a MoveSet built from given NBT
     */
    fun loadFromNBT(nbt: CompoundTag): MoveSet {
        doWithoutEmitting {
            clear()
            nbt.getList(DataKeys.POKEMON_MOVESET, Tag.TAG_COMPOUND.toInt()).forEachIndexed { index, tag ->
                setMove(index, Move.loadFromNBT(tag as CompoundTag))
            }
        }
        update()
        return this
    }

    /**
     * Returns a MoveSet build from given Buffer
     */
    fun loadFromBuffer(buffer: FriendlyByteBuf): MoveSet {
        doWithoutEmitting {
            clear()
            val amountMoves = buffer.readInt()
            for (i in 0 until amountMoves) {
                setMove(i, Move.loadFromBuffer(buffer))
            }
        }
        update()
        return this
    }

    fun loadFromJSON(json: JsonObject): MoveSet {
        doWithoutEmitting {
            clear()
            for (i in 0 until MOVE_COUNT) {
                val moveJSON = json.get(DataKeys.POKEMON_MOVESET + i) ?: continue
                val move = Move.loadFromJSON(moveJSON.asJsonObject)
                add(move)
            }
        }
        update()
        return this
    }

    companion object {
        const val MOVE_COUNT = 4
    }
}