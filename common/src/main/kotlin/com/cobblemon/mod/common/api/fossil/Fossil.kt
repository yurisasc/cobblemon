package com.cobblemon.mod.common.api.fossil

import com.cobblemon.mod.common.api.pokemon.PokemonProperties
import com.cobblemon.mod.common.pokemon.evolution.predicate.NbtItemPredicate
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.StringIdentifiable

class Fossil(
    identifier: Identifier,
    val result: PokemonProperties,
    val fossils: List<NbtItemPredicate>,
    val babyModel: Identifier,
    val babyTexture: Identifier,
): StringIdentifiable {

    @Transient
    var identifier: Identifier = identifier
        internal set

    override fun asString(): String {
        return identifier.toString()
    }

    /**
     * Gets the name of the fossil.
     * @return The name of the fossil.
     */
    fun getName(): MutableText = Text.translatable("${identifier.namespace}.fossil.${identifier.path}.name")

    /**
     * Whether the fossil ingredients match this fossil.
     * @param ingredients The ingredients to check.
     * @return True if the ingredients match, false otherwise.
     */
    fun matchesIngredients(ingredients: List<ItemStack>): Boolean {
        if (this.fossils.size != ingredients.size) {
            return false
        }

        return this.fossils.all { fossil ->
            ingredients.any { fossil.item.fits(it.item, Registries.ITEM) && fossil.nbt.test(it) }
        }
    }

    /**
     * Whether the [ItemStack] is an ingredient for this fossil.
     * @param itemStack The [ItemStack] to check.
     * @return True if the [ItemStack] is an ingredient, false otherwise.
     */
    fun isIngredient(itemStack: ItemStack): Boolean {
        return this.fossils.any { it.item.fits(itemStack.item, Registries.ITEM) && it.nbt.test(itemStack) }
    }

}