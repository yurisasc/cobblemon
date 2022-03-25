package com.cablemc.pokemoncobbled.common

import com.cablemc.pokemoncobbled.common.api.pokeball.PokeBalls
import com.cablemc.pokemoncobbled.common.item.ApricornItem
import com.cablemc.pokemoncobbled.common.item.PokeBallItem
import com.cablemc.pokemoncobbled.common.pokeball.PokeBall
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.core.Registry
import net.minecraft.world.entity.projectile.ItemSupplier
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemNameBlockItem
import net.minecraft.world.level.block.Block

object CobbledItems {
    private val itemRegister = DeferredRegister.create(PokemonCobbled.MODID, Registry.ITEM_REGISTRY)
    private fun <T : Item> queue(name: String, item: T) = itemRegister.register(name) { item }

    val POKE_BALL = queue("poke_ball", PokeBallItem(PokeBalls.POKE_BALL))
    val GREAT_BALL = queue("great_ball", PokeBallItem(PokeBalls.GREAT_BALL))
    val ULTRA_BALL = queue("ultra_ball", PokeBallItem(PokeBalls.ULTRA_BALL))
    val MASTER_BALL = queue("master_ball", PokeBallItem(PokeBalls.MASTER_BALL))

    val ballMap = mutableMapOf<PokeBall, RegistrySupplier<PokeBallItem>>()

    val BLACK_APRICORN = queue("black_apricorn", ApricornItem())
    val BLUE_APRICORN = queue("blue_apricorn", ApricornItem())
    val GREEN_APRICORN = queue("green_apricorn", ApricornItem())
    val PINK_APRICORN = queue("pink_apricorn", ApricornItem())
    val RED_APRICORN = queue("red_apricorn", ApricornItem())
    val WHITE_APRICORN = queue("white_apricorn", ApricornItem())
    val YELLOW_APRICORN = queue("yellow_apricorn", ApricornItem())

    val BLACK_APRICORN_SEED = queue("black_apricorn_seed", itemNameBlockItem(CobbledBlocks.BLACK_APRICORN_SAPLING, CreativeModeTab.TAB_MISC))
    val BLUE_APRICORN_SEED = queue("blue_apricorn_seed", itemNameBlockItem(CobbledBlocks.BLUE_APRICORN_SAPLING, CreativeModeTab.TAB_MISC))
    val GREEN_APRICORN_SEED = queue("green_apricorn_seed", itemNameBlockItem(CobbledBlocks.GREEN_APRICORN_SAPLING, CreativeModeTab.TAB_MISC))
    val PINK_APRICORN_SEED = queue("pink_apricorn_seed", itemNameBlockItem(CobbledBlocks.PINK_APRICORN_SAPLING, CreativeModeTab.TAB_MISC))
    val RED_APRICORN_SEED = queue("red_apricorn_seed", itemNameBlockItem(CobbledBlocks.RED_APRICORN_SAPLING, CreativeModeTab.TAB_MISC))
    val WHITE_APRICORN_SEED = queue("white_apricorn_seed", itemNameBlockItem(CobbledBlocks.WHITE_APRICORN_SAPLING, CreativeModeTab.TAB_MISC))
    val YELLOW_APRICORN_SEED = queue("yellow_apricorn_seed", itemNameBlockItem(CobbledBlocks.YELLOW_APRICORN_SAPLING, CreativeModeTab.TAB_MISC))

    val APRICORN_LOG = queue("apricorn_log", blockItem(CobbledBlocks.APRICORN_LOG, CreativeModeTab.TAB_BUILDING_BLOCKS))
    val STRIPPED_APRICORN_LOG = queue("stripped_apricorn_log", blockItem(CobbledBlocks.STRIPPED_APRICORN_LOG, CreativeModeTab.TAB_BUILDING_BLOCKS))
    val APRICORN_WOOD = queue("apricorn_wood", blockItem(CobbledBlocks.APRICORN_WOOD, CreativeModeTab.TAB_BUILDING_BLOCKS))
    val STRIPPED_APRICORN_WOOD = queue("stripped_apricorn_wood", blockItem(CobbledBlocks.STRIPPED_APRICORN_WOOD, CreativeModeTab.TAB_BUILDING_BLOCKS))
    val APRICORN_PLANKS = queue("apricorn_planks", blockItem(CobbledBlocks.APRICORN_PLANKS, CreativeModeTab.TAB_BUILDING_BLOCKS))
    val APRICORN_LEAVES = queue("apricorn_leaves", blockItem(CobbledBlocks.APRICORN_LEAVES, CreativeModeTab.TAB_BUILDING_BLOCKS))

    private fun <T : Block> blockItem(supplier: RegistrySupplier<T>, tab: CreativeModeTab) : BlockItem {
        return BlockItem(supplier.get(), Item.Properties().tab(tab))
    }

    private fun <T : Block> itemNameBlockItem(supplier: RegistrySupplier<T>, tab: CreativeModeTab) : BlockItem {
        return ItemNameBlockItem(supplier.get(), Item.Properties().tab(tab))
    }

    fun register() {
        itemRegister.register()
        ballMap[PokeBalls.POKE_BALL] = POKE_BALL
        ballMap[PokeBalls.GREAT_BALL] = GREAT_BALL
        ballMap[PokeBalls.ULTRA_BALL] = ULTRA_BALL
        ballMap[PokeBalls.MASTER_BALL] = MASTER_BALL
    }
}