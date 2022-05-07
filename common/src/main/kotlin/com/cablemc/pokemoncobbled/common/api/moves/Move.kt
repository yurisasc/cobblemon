package com.cablemc.pokemoncobbled.common.api.moves

import com.cablemc.pokemoncobbled.common.api.moves.categories.DamageCategory
import com.cablemc.pokemoncobbled.common.api.types.ElementalType
import com.cablemc.pokemoncobbled.common.net.IntSize
import com.cablemc.pokemoncobbled.common.util.DataKeys
import com.cablemc.pokemoncobbled.common.util.readSizedInt
import com.cablemc.pokemoncobbled.common.util.writeSizedInt
import com.google.gson.JsonObject
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.PacketByteBuf
import net.minecraft.text.Text

/**
 * Representing a Move based on some template and with current PP and the number of raised PP stages.
 */
open class Move(
    var currentPp: Int,
    var raisedPpStages: Int = 0,
    val template: MoveTemplate
) {
    val name: String
        get() = template.name

    val displayName: Text
        get() = template.displayName

    val description: Text
        get() = template.description

    val type: ElementalType
        get() = template.elementalType

    val damageCategory: DamageCategory
        get() = template.damageCategory

    val power: Double
        get() = template.power

    val accuracy: Double
        get() = template.accuracy

    val effectChance: Double
        get() = template.effectChance

    val maxPp: Int
        get() = template.pp + raisedPpStages * template.pp / 5

    fun saveToNBT(nbt: NbtCompound): NbtCompound {
        nbt.putString(DataKeys.POKEMON_MOVESET_MOVENAME, name)
        nbt.putInt(DataKeys.POKEMON_MOVESET_MOVEPP, currentPp)
        nbt.putInt(DataKeys.POKEMON_MOVESET_RAISED_PP_STAGES, raisedPpStages)
        return nbt
    }

    fun saveToJSON(json: JsonObject): JsonObject {
        json.addProperty(DataKeys.POKEMON_MOVESET_MOVENAME, name)
        json.addProperty(DataKeys.POKEMON_MOVESET_MOVEPP, currentPp)
        json.addProperty(DataKeys.POKEMON_MOVESET_RAISED_PP_STAGES, raisedPpStages)
        return json
    }

    fun saveToBuffer(buffer: PacketByteBuf) {
        buffer.writeString(name)
        buffer.writeSizedInt(IntSize.U_BYTE, currentPp)
        buffer.writeSizedInt(IntSize.U_BYTE, raisedPpStages)
    }

    companion object {
        fun loadFromNBT(nbt: NbtCompound): Move {
            val moveName = nbt.getString(DataKeys.POKEMON_MOVESET_MOVENAME)
            val template = Moves.getByNameOrDummy(moveName)
            return template.create(nbt.getInt(DataKeys.POKEMON_MOVESET_MOVEPP), nbt.getInt(DataKeys.POKEMON_MOVESET_RAISED_PP_STAGES))
        }

        fun loadFromJSON(json: JsonObject): Move {
            val moveName = json.get(DataKeys.POKEMON_MOVESET_MOVENAME).asString
            val template = Moves.getByNameOrDummy(moveName)
            val currentPp = json.get(DataKeys.POKEMON_MOVESET_MOVEPP).asInt
            val raisedPpStages = json.get(DataKeys.POKEMON_MOVESET_RAISED_PP_STAGES)?.asInt ?: 0
            return Move(currentPp, raisedPpStages, template)
        }

        fun loadFromBuffer(buffer: PacketByteBuf): Move {
            val moveName = buffer.readString()
            val currentPp = buffer.readSizedInt(IntSize.U_BYTE)
            val raisedPpStages = buffer.readSizedInt(IntSize.U_BYTE)
            val template = Moves.getByNameOrDummy(moveName)
            return template.create(currentPp, raisedPpStages)
        }
    }
}