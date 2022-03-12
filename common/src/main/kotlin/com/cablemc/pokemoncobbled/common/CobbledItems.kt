package com.cablemc.pokemoncobbled.common

import com.cablemc.pokemoncobbled.common.api.pokeball.PokeBalls
import com.cablemc.pokemoncobbled.common.item.PokeBallItem
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
import dev.architectury.registry.registries.DeferredRegister
import net.minecraft.core.Registry
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item

object CobbledItems {
    private val itemRegister = DeferredRegister.create(PokemonCobbled.MODID, Registry.ITEM_REGISTRY)
    private fun <T : Item> queue(name: String, item: T) = itemRegister.register(name) { item }

    val POKE_BALL = queue("poke_ball", PokeBallItem(PokeBalls.POKE_BALL))
    val POKE_BALL_TYPE: PokeBallItem
        get() = POKE_BALL.get()

    /**
     * Evolution Ores and Stones
     */
    val DAWN_STONE_ORE = queue("dawn_stone_ore", BlockItem(CobbledBlocks.DAWN_STONE_ORE.get(), Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)))
    val DUSK_STONE_ORE = queue("dusk_stone_ore", BlockItem(CobbledBlocks.DUSK_STONE_ORE.get(), Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)))
    val FIRE_STONE_ORE = queue("fire_stone_ore", BlockItem(CobbledBlocks.FIRE_STONE_ORE.get(), Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)))
    val ICE_STONE_ORE = queue("ice_stone_ore", BlockItem(CobbledBlocks.ICE_STONE_ORE.get(), Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)))
    val LEAF_STONE_ORE = queue("leaf_stone_ore", BlockItem(CobbledBlocks.LEAF_STONE_ORE.get(), Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)))
    val MOON_STONE_ORE = queue("moon_stone_ore", BlockItem(CobbledBlocks.MOON_STONE_ORE.get(), Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)))
    val SHINY_STONE_ORE = queue("shiny_stone_ore", BlockItem(CobbledBlocks.SHINY_STONE_ORE.get(), Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)))
    val SUN_STONE_ORE = queue("sun_stone_ore", BlockItem(CobbledBlocks.SUN_STONE_ORE.get(), Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)))
    val THUNDER_STONE_ORE = queue("thunder_stone_ore", BlockItem(CobbledBlocks.THUNDER_STONE_ORE.get(), Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)))
    val WATER_STONE_ORE = queue("water_stone_ore", BlockItem(CobbledBlocks.WATER_STONE_ORE.get(), Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)))
    val DEEPSLATE_DAWN_STONE_ORE = queue("deepslate_dawn_stone_ore", BlockItem(CobbledBlocks.DEEPSLATE_DAWN_STONE_ORE.get(), Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)))
    val DEEPSLATE_DUSK_STONE_ORE = queue("deepslate_dusk_stone_ore", BlockItem(CobbledBlocks.DEEPSLATE_DUSK_STONE_ORE.get(), Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)))
    val DEEPSLATE_FIRE_STONE_ORE = queue("deepslate_fire_stone_ore", BlockItem(CobbledBlocks.DEEPSLATE_FIRE_STONE_ORE.get(), Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)))
    val DEEPSLATE_ICE_STONE_ORE = queue("deepslate_ice_stone_ore", BlockItem(CobbledBlocks.DEEPSLATE_ICE_STONE_ORE.get(), Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)))
    val DEEPSLATE_LEAF_STONE_ORE = queue("deepslate_leaf_stone_ore", BlockItem(CobbledBlocks.DEEPSLATE_LEAF_STONE_ORE.get(), Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)))
    val DEEPSLATE_MOON_STONE_ORE = queue("deepslate_moon_stone_ore", BlockItem(CobbledBlocks.DEEPSLATE_MOON_STONE_ORE.get(), Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)))
    val DEEPSLATE_SHINY_STONE_ORE = queue("deepslate_shiny_stone_ore", BlockItem(CobbledBlocks.DEEPSLATE_SHINY_STONE_ORE.get(), Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)))
    val DEEPSLATE_SUN_STONE_ORE = queue("deepslate_sun_stone_ore", BlockItem(CobbledBlocks.DEEPSLATE_SUN_STONE_ORE.get(), Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)))
    val DEEPSLATE_THUNDER_STONE_ORE = queue("deepslate_thunder_stone_ore", BlockItem(CobbledBlocks.DEEPSLATE_THUNDER_STONE_ORE.get(), Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)))
    val DEEPSLATE_WATER_STONE_ORE = queue("deepslate_water_stone_ore", BlockItem(CobbledBlocks.DEEPSLATE_WATER_STONE_ORE.get(), Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)))
    val DAWN_STONE = queue("dawn_stone", DawnStone())
    val DUSK_STONE = queue("dusk_stone", DuskStone())
    val FIRE_STONE = queue("fire_stone", FireStone())
    val ICE_STONE = queue("ice_stone", IceStone())
    val LEAF_STONE = queue("leaf_stone", LeafStone())
    val MOON_STONE = queue("moon_stone", MoonStone())
    val SHINY_STONE = queue("shiny_stone", ShinyStone())
    val SUN_STONE = queue("sun_stone", SunStone())
    val THUNDER_STONE = queue("thunder_stone", ThunderStone())
    val WATER_STONE = queue("water_stone", WaterStone())


    fun register() {
        itemRegister.register()
    }
}