package com.cablemc.pokemoncobbled.common.api.moves

import com.cablemc.pokemoncobbled.common.api.reactive.SimpleObservable
import com.cablemc.pokemoncobbled.common.net.IntSize
import com.cablemc.pokemoncobbled.common.util.DataKeys
import com.cablemc.pokemoncobbled.common.util.readSizedInt
import com.cablemc.pokemoncobbled.common.util.writeSizedInt
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.network.FriendlyByteBuf

class BenchedMoves : Iterable<BenchedMove> {
    val observable = SimpleObservable<BenchedMoves>()
    private var emit = true
    private val benchedMoves = mutableListOf<BenchedMove>()

    fun doWithoutEmitting(action: () -> Unit) {
        emit = false
        action()
        emit = true
    }

    fun doThenEmit(action: () -> Unit) {
        doWithoutEmitting(action)
        update()
    }

    fun update() {
        if (emit) {
            observable.emit(this)
        }
    }

    fun add(benchedMove: BenchedMove) = doThenEmit { benchedMoves.add(benchedMove) }
    fun addAll(benchedMoves: Iterable<BenchedMove>) = doThenEmit { this.benchedMoves.addAll(benchedMoves) }
    fun clear() = doThenEmit { benchedMoves.clear() }
    fun remove(benchedMove: BenchedMove) = doThenEmit { benchedMoves.remove(benchedMove) }
    override fun iterator() = benchedMoves.iterator()

    fun saveToNBT(nbt: ListTag): ListTag {
        nbt.addAll(benchedMoves.map { it.saveToNBT(CompoundTag()) })
        return nbt
    }

    fun saveToJSON(json: JsonArray): JsonArray {
        val jsons = benchedMoves.map { it.saveToJSON(JsonObject()) }
        jsons.forEach { json.add(it) }
        return json
    }

    fun saveToBuffer(buffer: FriendlyByteBuf) {
        buffer.writeShort(benchedMoves.size)
        benchedMoves.forEach { it.saveToBuffer(buffer) }
    }

    fun loadFromNBT(nbt: ListTag): BenchedMoves {
        doThenEmit {
            clear()
            nbt.forEach { benchedMoves.add(BenchedMove.loadFromNBT(it as CompoundTag)) }
        }

        return this
    }

    fun loadFromJSON(json: JsonArray): BenchedMoves {
        doThenEmit {
            clear()
            json.forEach { benchedMoves.add(BenchedMove.loadFromJSON(it.asJsonObject)) }
        }
        return this
    }

    fun loadFromBuffer(buffer: FriendlyByteBuf): BenchedMoves {
        doThenEmit {
            clear()
            repeat(times = buffer.readShort().toInt()) {
                benchedMoves.add(BenchedMove.loadFromBuffer(buffer))
            }
        }
        return this
    }
}

data class BenchedMove(val moveTemplate: MoveTemplate, val ppRaisedStages: Int) {
    fun saveToNBT(nbt: CompoundTag): CompoundTag {
        nbt.putString(DataKeys.POKEMON_MOVESET_MOVENAME, moveTemplate.name)
        nbt.putByte(DataKeys.POKEMON_MOVESET_RAISED_PP_STAGES, ppRaisedStages.toByte())
        return nbt
    }

    fun saveToJSON(json: JsonObject): JsonObject {
        json.addProperty(DataKeys.POKEMON_MOVESET_MOVENAME, moveTemplate.name)
        json.addProperty(DataKeys.POKEMON_MOVESET_RAISED_PP_STAGES, ppRaisedStages)
        return json
    }

    fun saveToBuffer(buffer: FriendlyByteBuf) {
        buffer.writeUtf(moveTemplate.name)
        buffer.writeSizedInt(IntSize.U_BYTE, ppRaisedStages)
    }

    companion object {
        fun loadFromNBT(nbt: CompoundTag): BenchedMove {
            val name = nbt.getString(DataKeys.POKEMON_MOVESET_MOVENAME)
            return BenchedMove(
                Moves.getByName(name) ?: MoveTemplate.dummy(name),
                nbt.getByte(DataKeys.POKEMON_MOVESET_RAISED_PP_STAGES).toInt()
            )
        }

        fun loadFromJSON(json: JsonObject): BenchedMove {
            val name = json.get(DataKeys.POKEMON_MOVESET_MOVENAME).asString
            return BenchedMove(
                Moves.getByName(name) ?: MoveTemplate.dummy(name),
                json.get(DataKeys.POKEMON_MOVESET_RAISED_PP_STAGES).asInt
            )
        }

        fun loadFromBuffer(buffer: FriendlyByteBuf): BenchedMove {
            val name = buffer.readUtf()
            return BenchedMove(
                Moves.getByName(name) ?: MoveTemplate.dummy(name),
                buffer.readSizedInt(IntSize.U_BYTE)
            )
        }
    }
}