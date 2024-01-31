package com.cobblemon.mod.common.item

import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies.species
import com.cobblemon.mod.common.api.pokemon.breeding.EggPokemon
import com.cobblemon.mod.common.util.DataKeys
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.nbt.NbtString

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
            return 0xFFFFFF
        }
    }

}