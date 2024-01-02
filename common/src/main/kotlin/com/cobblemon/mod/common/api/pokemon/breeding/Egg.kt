package com.cobblemon.mod.common.api.pokemon.breeding

import com.cobblemon.mod.common.util.DataKeys
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.Identifier

data class Egg (
    val hatchedPokemon: EggPokemon,
    val patternId: Identifier,
    val primaryColor: String,
    val secondaryColor: String
) {
    fun toNbt(): NbtCompound {
        val result = NbtCompound()
        result.put(DataKeys.HATCHED_POKEMON, hatchedPokemon.toNbt())
        result.putString(DataKeys.EGG_PATTERN, patternId.toString())
        result.putString(DataKeys.PRIMARY_COLOR, primaryColor)
        result.putString(DataKeys.SECONDARY_COLOR, secondaryColor)
        return result
    }

    companion object {
        fun fromNbt(nbt: NbtCompound): Egg {
            return Egg(
                EggPokemon.fromNBT(nbt.get(DataKeys.HATCHED_POKEMON) as NbtCompound),
                Identifier.tryParse(nbt.getString(DataKeys.EGG_PATTERN))!!,
                nbt.getString(DataKeys.PRIMARY_COLOR),
                nbt.getString(DataKeys.SECONDARY_COLOR),
            )
        }
    }
}