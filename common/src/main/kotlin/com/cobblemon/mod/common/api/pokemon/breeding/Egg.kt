/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.pokemon.breeding

import com.cobblemon.mod.common.CobblemonBlockEntities
import com.cobblemon.mod.common.CobblemonBlocks
import com.cobblemon.mod.common.block.entity.NestBlockEntity
import com.cobblemon.mod.common.util.DataKeys
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.Identifier

data class Egg(
    val hatchedPokemon: EggPokemon,
    val patternId: Identifier,
    val baseColor: String,
    val overlayColor: String?
) {
    fun asItemStack(blockEntityNbt: NbtCompound): ItemStack {
        val stack = CobblemonBlocks.EGG.asItem().defaultStack
        BlockItem.setBlockEntityNbt(stack, CobblemonBlockEntities.EGG, blockEntityNbt)
        return stack
    }
    fun toNbt(): NbtCompound {
        val result = NbtCompound()
        result.put(DataKeys.HATCHED_POKEMON, hatchedPokemon.toNbt())
        result.putString(DataKeys.EGG_PATTERN, patternId.toString())
        result.putString(DataKeys.PRIMARY_COLOR, baseColor)
        overlayColor?.let { result.putString(DataKeys.SECONDARY_COLOR, it) }
        return result
    }

    fun getPattern(): EggPattern? {
        return EggPatterns.patternMap[patternId]
    }

    companion object {
        fun fromBlockNbt(blockNbt: NbtCompound): Egg {
            val eggNbt = blockNbt.get(DataKeys.EGG) as NbtCompound
            return fromNbt(eggNbt)
        }
        fun fromNbt(nbt: NbtCompound): Egg {
            return Egg(
                EggPokemon.fromNBT(nbt.get(DataKeys.HATCHED_POKEMON) as NbtCompound),
                Identifier.tryParse(nbt.getString(DataKeys.EGG_PATTERN))!!,
                nbt.getString(DataKeys.PRIMARY_COLOR),
                if (nbt.contains(DataKeys.SECONDARY_COLOR)) nbt.getString(DataKeys.SECONDARY_COLOR) else null
            )
        }

        //Less expensive than deserializing the whole thing, used for the color provider since it gets
        //called every frame
        fun getColorsFromNbt(nbt: NbtCompound): Pair<String, String> {
            return Pair(
                nbt.getString(DataKeys.PRIMARY_COLOR),
                nbt.getString(DataKeys.SECONDARY_COLOR)
            )
        }
    }
}