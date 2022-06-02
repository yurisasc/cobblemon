package com.cablemc.pokemoncobbled.common.api.moves

import com.cablemc.pokemoncobbled.common.api.reactive.SimpleObservable
import com.cablemc.pokemoncobbled.common.util.DataKeys
import com.google.gson.JsonObject
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.network.PacketByteBuf

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

    fun hasSpace() = moves.any { it == null }

    /**
     * Sets the given Move to given position
     */
    fun setMove(pos: Int, move: Move?) {
        if (pos !in 0 until MOVE_COUNT) {
            return
        }
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

    fun heal() {
        getMoves().forEach { it.currentPp = it.maxPp }
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
     * Returns a NbtList containing all the Moves
     */
    fun getNBT(): NbtList {
        val listTag = NbtList()
        listTag.addAll(getMoves().map { it.saveToNBT(NbtCompound()) })
        return listTag
    }

    /**
     * Writes the MoveSet to Buffer
     */
    fun saveToBuffer(buffer: PacketByteBuf) {
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
        if (any { it.template == move.template }) {
            return
        }
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
        val previousEmit = emit
        emit = false
        action()
        emit = previousEmit
    }


    /**
     * Returns a MoveSet built from given NBT
     */
    fun loadFromNBT(nbt: NbtCompound): MoveSet {
        doWithoutEmitting {
            clear()
            nbt.getList(DataKeys.POKEMON_MOVESET, NbtElement.COMPOUND_TYPE.toInt()).forEachIndexed { index, tag ->
                setMove(index, Move.loadFromNBT(tag as NbtCompound))
            }
        }
        update()
        return this
    }

    /**
     * Returns a MoveSet build from given Buffer
     */
    fun loadFromBuffer(buffer: PacketByteBuf): MoveSet {
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