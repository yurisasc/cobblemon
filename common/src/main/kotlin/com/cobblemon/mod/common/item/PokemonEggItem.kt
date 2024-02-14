/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.item

import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies.species
import com.cobblemon.mod.common.api.pokemon.breeding.Egg
import com.cobblemon.mod.common.api.pokemon.breeding.EggPokemon
import com.cobblemon.mod.common.util.DataKeys
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.nbt.NbtString
import net.minecraft.util.math.ColorHelper.Argb
import java.awt.Color

class PokemonEggItem() : Item(Settings().maxCount(1)) {

    companion object {
        fun from(egg: EggPokemon): ItemStack {
            val stack = ItemStack(CobblemonItems.POKEMON_EGG, 1)
            stack.orCreateNbt.apply {
                put(DataKeys.EGG, egg.toNbt())
            }
            return stack
        }

        fun asEggPokemon(stack: ItemStack) : EggPokemon? {
            return (stack.nbt?.get(DataKeys.EGG))?.let { EggPokemon.fromNBT(it as NbtCompound) }
        }

        fun getColor(stack: ItemStack, tintIndex: Int): Int {
            val nbt = BlockItem.getBlockEntityNbt(stack)  ?: return Integer.valueOf("FFFFFFFF", 16)
            val eggNbt = nbt.getCompound(DataKeys.EGG) ?:  return Integer.valueOf("FFFFFFFF", 16)
            val colors = Egg.getColorsFromNbt(eggNbt)
            return if (tintIndex == 0) {
                val color = Color.decode("#${colors.first}")
                Argb.getArgb(255, color.red, color.green, color.blue)
            } else {
                val overlayColor = colors.second ?: "FFFFFF"
                val color = Color.decode("#${overlayColor}")
                Argb.getArgb(255, color.red, color.green, color.blue)
            }
        }
    }

}