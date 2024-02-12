package com.cobblemon.mod.common.item

import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies.species
import com.cobblemon.mod.common.api.pokemon.breeding.Egg
import com.cobblemon.mod.common.api.pokemon.breeding.EggPokemon
import com.cobblemon.mod.common.util.DataKeys
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
            val eggNbt = stack.nbt?.get(DataKeys.EGG) as? NbtCompound ?: return 0xFFFFFF
            val egg = Egg.fromNbt(eggNbt)
            return if (tintIndex == 0) {
                val color = Color.decode("#${egg.baseColor}")
                Argb.getArgb(255, color.red, color.green, color.blue)
            } else {
                val overlayColor = egg.overlayColor ?: "FFFFFF"
                val color = Color.decode("#${overlayColor}")
                Argb.getArgb(255, color.red, color.green, color.blue)
            }
        }
    }

}