package com.cablemc.pokemoncobbled.common.api.moves

import com.cablemc.pokemoncobbled.common.api.moves.categories.DamageCategory
import com.cablemc.pokemoncobbled.common.api.types.ElementalType
import com.cablemc.pokemoncobbled.common.util.DataKeys
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component

/**
 * Representing a Move with all its attributes
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

    fun saveToBuffer(buffer: FriendlyByteBuf): FriendlyByteBuf {
        buffer.writeUtf(name)
        buffer.writeInt(currentPp)
        buffer.writeInt(maxPp)
        return buffer
    }

    companion object {
        fun loadFromNBT(nbt: CompoundTag): Move {
            val template = Moves.getByName(nbt.getString(DataKeys.POKEMON_MOVESET_MOVENAME))
                ?: throw IllegalStateException("Tried to get non-existent MoveTemplate ${nbt.getString(DataKeys.POKEMON_MOVESET_MOVENAME)}")
            return template.create(nbt.getInt(DataKeys.POKEMON_MOVESET_MOVEPP), nbt.getInt(DataKeys.POKEMON_MOVESET_MAXPP))
        }

        fun loadFromBuffer(buffer: FriendlyByteBuf): Move {
            val template = Moves.getByName(buffer.readUtf())
                ?: throw IllegalStateException("Tried to get non-existent MoveTemplate ${buffer.readUtf()}")
            return template.create(buffer.readInt(), buffer.readInt())
        }
    }
}