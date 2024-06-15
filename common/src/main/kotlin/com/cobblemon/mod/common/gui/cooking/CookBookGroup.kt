package com.cobblemon.mod.common.gui.cooking

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.block.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.recipe.book.RecipeBookCategory

@Environment(EnvType.CLIENT)
enum class CookBookGroup(vararg entries: ItemStack) {
    CRAFTING_SEARCH(ItemStack(Items.COMPASS)),
    CRAFTING_BUILDING_BLOCKS(ItemStack(Blocks.BRICKS)),
    CRAFTING_REDSTONE(ItemStack(Items.REDSTONE)),
    CRAFTING_EQUIPMENT(ItemStack(Items.IRON_AXE), ItemStack(Items.GOLDEN_SWORD)),
    CRAFTING_MISC(ItemStack(Items.LAVA_BUCKET), ItemStack(Items.APPLE)),
    FURNACE_SEARCH(ItemStack(Items.COMPASS)),
    FURNACE_FOOD(ItemStack(Items.PORKCHOP)),
    FURNACE_BLOCKS(ItemStack(Blocks.STONE)),
    FURNACE_MISC(ItemStack(Items.LAVA_BUCKET), ItemStack(Items.EMERALD)),
    BLAST_FURNACE_SEARCH(ItemStack(Items.COMPASS)),
    BLAST_FURNACE_BLOCKS(ItemStack(Blocks.REDSTONE_ORE)),
    BLAST_FURNACE_MISC(ItemStack(Items.IRON_SHOVEL), ItemStack(Items.GOLDEN_LEGGINGS)),
    SMOKER_SEARCH(ItemStack(Items.COMPASS)),
    SMOKER_FOOD(ItemStack(Items.PORKCHOP)),
    STONECUTTER(ItemStack(Items.CHISELED_STONE_BRICKS)),
    SMITHING(ItemStack(Items.NETHERITE_CHESTPLATE)),
    CAMPFIRE(ItemStack(Items.PORKCHOP)),
    UNKNOWN(ItemStack(Items.BARRIER));

    val icons: List<ItemStack> = ImmutableList.copyOf(entries)

    companion object {
        val SMOKER: List<CookBookGroup> = ImmutableList.of(SMOKER_SEARCH, SMOKER_FOOD)
        val BLAST_FURNACE: List<CookBookGroup> = ImmutableList.of(BLAST_FURNACE_SEARCH, BLAST_FURNACE_BLOCKS, BLAST_FURNACE_MISC)
        val FURNACE: List<CookBookGroup> = ImmutableList.of(FURNACE_SEARCH, FURNACE_FOOD, FURNACE_BLOCKS, FURNACE_MISC)
        val CRAFTING: List<CookBookGroup> = ImmutableList.of(CRAFTING_SEARCH, CRAFTING_EQUIPMENT, CRAFTING_BUILDING_BLOCKS, CRAFTING_MISC, CRAFTING_REDSTONE)
        val SEARCH_MAP: Map<CookBookGroup, List<CookBookGroup>> = ImmutableMap.of(
            CRAFTING_SEARCH, ImmutableList.of(CRAFTING_EQUIPMENT, CRAFTING_BUILDING_BLOCKS, CRAFTING_MISC, CRAFTING_REDSTONE),
            FURNACE_SEARCH, ImmutableList.of(FURNACE_FOOD, FURNACE_BLOCKS, FURNACE_MISC),
            BLAST_FURNACE_SEARCH, ImmutableList.of(BLAST_FURNACE_BLOCKS, BLAST_FURNACE_MISC),
            SMOKER_SEARCH, ImmutableList.of(SMOKER_FOOD)
        )

        fun getGroups(category: RecipeBookCategory): List<CookBookGroup> {
            return when (category) {
                RecipeBookCategory.CRAFTING -> CRAFTING
                RecipeBookCategory.FURNACE -> FURNACE
                RecipeBookCategory.BLAST_FURNACE -> BLAST_FURNACE
                RecipeBookCategory.SMOKER -> SMOKER
                else -> throw IncompatibleClassChangeError()
            }
        }
    }

    fun getIconsValue(): List<ItemStack> {
        return this.icons
    }
}
