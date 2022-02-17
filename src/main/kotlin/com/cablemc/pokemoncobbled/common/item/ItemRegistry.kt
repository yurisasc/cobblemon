package com.cablemc.pokemoncobbled.common.item

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.pokeball.PokeBalls
import com.cablemc.pokemoncobbled.common.blocks.BlockRegistry
import com.cablemc.pokemoncobbled.common.item.evo.DawnStone
import com.cablemc.pokemoncobbled.common.item.evo.DuskStone
import com.cablemc.pokemoncobbled.common.item.evo.FireStone
import com.cablemc.pokemoncobbled.common.item.evo.IceStone
import com.cablemc.pokemoncobbled.common.item.evo.LeafStone
import com.cablemc.pokemoncobbled.common.item.evo.MoonStone
import com.cablemc.pokemoncobbled.common.item.evo.ShinyStone
import com.cablemc.pokemoncobbled.common.item.evo.SunStone
import com.cablemc.pokemoncobbled.common.item.evo.ThunderStone
import com.cablemc.pokemoncobbled.common.item.evo.WaterStone
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

/**
 * Registry for cobbled items
 */
object ItemRegistry {

    private val ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, PokemonCobbled.MODID)

    val POKE_BALL = registerItem("poke_ball", PokeBallItem(PokeBalls.POKE_BALL))

    /**
     * Evolution Ores and Stones
     */

    val DAWN_STONE_ORE = ITEMS.register("dawn_stone_ore") {
        BlockItem(BlockRegistry.DAWN_STONE_ORE, Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS))
    }
    val DUSK_STONE_ORE = ITEMS.register("dusk_stone_ore") {
        BlockItem(BlockRegistry.DUSK_STONE_ORE, Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS))
    }
    val FIRE_STONE_ORE = ITEMS.register("fire_stone_ore") {
        BlockItem(BlockRegistry.FIRE_STONE_ORE, Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS))
    }
    val ICE_STONE_ORE = ITEMS.register("ice_stone_ore") {
        BlockItem(BlockRegistry.ICE_STONE_ORE, Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS))
    }
    val LEAF_STONE_ORE = ITEMS.register("leaf_stone_ore") {
        BlockItem(BlockRegistry.LEAF_STONE_ORE, Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS))
    }
    val MOON_STONE_ORE = ITEMS.register("moon_stone_ore") {
        BlockItem(BlockRegistry.MOON_STONE_ORE, Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS))
    }
    val SHINY_STONE_ORE = ITEMS.register("shiny_stone_ore") {
        BlockItem(BlockRegistry.SHINY_STONE_ORE, Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS))
    }
    val SUN_STONE_ORE = ITEMS.register("sun_stone_ore") {
        BlockItem(BlockRegistry.SUN_STONE_ORE, Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS))
    }
    val THUNDER_STONE_ORE = ITEMS.register("thunder_stone_ore") {
        BlockItem(BlockRegistry.THUNDER_STONE_ORE, Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS))
    }
    val WATER_STONE_ORE = ITEMS.register("water_stone_ore") {
        BlockItem(BlockRegistry.WATER_STONE_ORE, Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS))
    }
    val DEEPSLATE_DAWN_STONE_ORE = ITEMS.register("deepslate_dawn_stone_ore") {
        BlockItem(BlockRegistry.DEEPSLATE_DAWN_STONE_ORE, Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS))
    }
    val DEEPSLATE_DUSK_STONE_ORE = ITEMS.register("deepslate_dusk_stone_ore") {
        BlockItem(BlockRegistry.DEEPSLATE_DUSK_STONE_ORE, Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS))
    }
    val DEEPSLATE_FIRE_STONE_ORE = ITEMS.register("deepslate_fire_stone_ore") {
        BlockItem(BlockRegistry.DEEPSLATE_FIRE_STONE_ORE, Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS))
    }
    val DEEPSLATE_ICE_STONE_ORE = ITEMS.register("deepslate_ice_stone_ore") {
        BlockItem(BlockRegistry.DEEPSLATE_ICE_STONE_ORE, Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS))
    }
    val DEEPSLATE_LEAF_STONE_ORE = ITEMS.register("deepslate_leaf_stone_ore") {
        BlockItem(BlockRegistry.DEEPSLATE_LEAF_STONE_ORE, Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS))
    }
    val DEEPSLATE_MOON_STONE_ORE = ITEMS.register("deepslate_moon_stone_ore") {
        BlockItem(BlockRegistry.DEEPSLATE_MOON_STONE_ORE, Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS))
    }
    val DEEPSLATE_SHINY_STONE_ORE = ITEMS.register("deepslate_shiny_stone_ore") {
        BlockItem(BlockRegistry.DEEPSLATE_SHINY_STONE_ORE, Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS))
    }
    val DEEPSLATE_SUN_STONE_ORE = ITEMS.register("deepslate_sun_stone_ore") {
        BlockItem(BlockRegistry.DEEPSLATE_SUN_STONE_ORE, Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS))
    }
    val DEEPSLATE_THUNDER_STONE_ORE = ITEMS.register("deepslate_thunder_stone_ore") {
        BlockItem(BlockRegistry.DEEPSLATE_THUNDER_STONE_ORE, Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS))
    }
    val DEEPSLATE_WATER_STONE_ORE = ITEMS.register("deepslate_water_stone_ore") {
        BlockItem(BlockRegistry.DEEPSLATE_WATER_STONE_ORE, Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS))
    }
    val DAWN_STONE = registerItem("dawn_stone", DawnStone())
    val DUSK_STONE = registerItem("dusk_stone", DuskStone())
    val FIRE_STONE = registerItem("fire_stone", FireStone())
    val ICE_STONE = registerItem("ice_stone", IceStone())
    val LEAF_STONE = registerItem("leaf_stone", LeafStone())
    val MOON_STONE = registerItem("moon_stone", MoonStone())
    val SHINY_STONE = registerItem("shiny_stone", ShinyStone())
    val SUN_STONE = registerItem("sun_stone", SunStone())
    val THUNDER_STONE = registerItem("thunder_stone", ThunderStone())
    val WATER_STONE = registerItem("water_stone", WaterStone())

    private fun registerItem(
        name: String,
        item: Item
    ): RegistryObject<Item> {
        return ITEMS.register(name) { item }
    }

    fun register(bus: IEventBus) {
        ITEMS.register(bus)
    }
}