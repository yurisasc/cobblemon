package com.cablemc.pokemoncobbled.common.api.moves

import com.cablemc.pokemoncobbled.common.api.moves.categories.DamageCategory
import com.cablemc.pokemoncobbled.common.api.types.ElementalType
import com.cablemc.pokemoncobbled.common.net.IntSize
import com.cablemc.pokemoncobbled.common.util.DataKeys
import com.cablemc.pokemoncobbled.common.util.readSizedInt
import com.cablemc.pokemoncobbled.common.util.writeSizedInt
import com.google.gson.JsonObject
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component

/**
 * Representing a Move based on some template and with current and maximum PP. Maximum PP is
 * the amount it should be set to when healed, not the maximum this move could have after using
 * PP Ups - that value is [MoveTemplate.maxPp] from [template].
 */
open class Move(
    var currentPp: Int,
    val maxPp: Int,
    val template: MoveTemplate
) {
    val name: String
        get() = template.name

    val displayName: Component
        get() = template.displayName

    val description: Component
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

    fun saveToNBT(nbt: CompoundTag): CompoundTag {
        nbt.putString(DataKeys.POKEMON_MOVESET_MOVENAME, name)
        nbt.putInt(DataKeys.POKEMON_MOVESET_MOVEPP, currentPp)
        nbt.putInt(DataKeys.POKEMON_MOVESET_MAXPP, maxPp)
        return nbt
    }

    fun saveToJSON(json: JsonObject): JsonObject {
        json.addProperty(DataKeys.POKEMON_MOVESET_MOVENAME, name)
        json.addProperty(DataKeys.POKEMON_MOVESET_MOVEPP, currentPp)
        json.addProperty(DataKeys.POKEMON_MOVESET_MAXPP, maxPp)
        return json
    }

    fun saveToBuffer(buffer: FriendlyByteBuf) {
        buffer.writeUtf(name)
        buffer.writeSizedInt(IntSize.U_BYTE, currentPp)
        buffer.writeSizedInt(IntSize.U_BYTE, maxPp)
    }

    companion object {
        fun loadFromNBT(nbt: CompoundTag): Move {
            val moveName = nbt.getString(DataKeys.POKEMON_MOVESET_MOVENAME)
            val template = Moves.getByNameOrDummy(moveName)
            return template.create(nbt.getInt(DataKeys.POKEMON_MOVESET_MOVEPP), nbt.getInt(DataKeys.POKEMON_MOVESET_MAXPP))
        }

        fun loadFromJSON(json: JsonObject): Move {
            val moveName = json.get(DataKeys.POKEMON_MOVESET_MOVENAME).asString
            val template = Moves.getByNameOrDummy(moveName)
            val currentPp = json.get(DataKeys.POKEMON_MOVESET_MOVEPP).asInt
            val maxPp = json.get(DataKeys.POKEMON_MOVESET_MAXPP).asInt
            return Move(currentPp, maxPp, template)
        }

        fun loadFromBuffer(buffer: FriendlyByteBuf): Move {
            val moveName = buffer.readUtf()
            val currentPp = buffer.readSizedInt(IntSize.U_BYTE)
            val maxPp = buffer.readSizedInt(IntSize.U_BYTE)
            val template = Moves.getByNameOrDummy(moveName)
            return template.create(currentPp, maxPp)
        }
    }
}