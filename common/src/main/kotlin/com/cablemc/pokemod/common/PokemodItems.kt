/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common

import com.cablemc.pokemod.common.api.pokeball.PokeBalls
import com.cablemc.pokemod.common.item.ApricornItem
import com.cablemc.pokemod.common.item.PokeBallItem
import com.cablemc.pokemod.common.item.evo.DawnStone
import com.cablemc.pokemod.common.item.evo.DuskStone
import com.cablemc.pokemod.common.item.evo.FireStone
import com.cablemc.pokemod.common.item.evo.IceStone
import com.cablemc.pokemod.common.item.evo.LeafStone
import com.cablemc.pokemod.common.item.evo.MoonStone
import com.cablemc.pokemod.common.item.evo.ShinyStone
import com.cablemc.pokemod.common.item.evo.SunStone
import com.cablemc.pokemod.common.item.evo.ThunderStone
import com.cablemc.pokemod.common.item.evo.WaterStone
import com.cablemc.pokemod.common.item.interactive.CandyItem
import com.cablemc.pokemod.common.item.interactive.EvolutionItem
import com.cablemc.pokemod.common.pokeball.PokeBall
import com.cablemc.pokemod.common.registry.CompletableRegistry
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.block.Block
import net.minecraft.item.AliasedBlockItem
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.util.registry.Registry

object PokemodItems : CompletableRegistry<Item>(Registry.ITEM_KEY) {

    val POKE_BALL = queue("poke_ball") { PokeBallItem(PokeBalls.POKE_BALL) }
    val GREAT_BALL = queue("great_ball") { PokeBallItem(PokeBalls.GREAT_BALL) }
    val ULTRA_BALL = queue("ultra_ball") { PokeBallItem(PokeBalls.ULTRA_BALL) }
    val MASTER_BALL = queue("master_ball") { PokeBallItem(PokeBalls.MASTER_BALL) }

    val ballMap = mutableMapOf<PokeBall, RegistrySupplier<PokeBallItem>>()

    val BLACK_APRICORN = queue("black_apricorn") { ApricornItem() }
    val BLUE_APRICORN = queue("blue_apricorn") { ApricornItem() }
    val GREEN_APRICORN = queue("green_apricorn") { ApricornItem() }
    val PINK_APRICORN = queue("pink_apricorn") { ApricornItem() }
    val RED_APRICORN = queue("red_apricorn") { ApricornItem() }
    val WHITE_APRICORN = queue("white_apricorn") { ApricornItem() }
    val YELLOW_APRICORN = queue("yellow_apricorn") { ApricornItem() }

    val BLACK_APRICORN_SEED = queue("black_apricorn_seed") { itemNameBlockItem(PokemodBlocks.BLACK_APRICORN_SAPLING.get(), ItemGroup.MISC) }
    val BLUE_APRICORN_SEED = queue("blue_apricorn_seed") { itemNameBlockItem(PokemodBlocks.BLUE_APRICORN_SAPLING.get(), ItemGroup.MISC) }
    val GREEN_APRICORN_SEED = queue("green_apricorn_seed") { itemNameBlockItem(PokemodBlocks.GREEN_APRICORN_SAPLING.get(), ItemGroup.MISC) }
    val PINK_APRICORN_SEED = queue("pink_apricorn_seed") { itemNameBlockItem(PokemodBlocks.PINK_APRICORN_SAPLING.get(), ItemGroup.MISC) }
    val RED_APRICORN_SEED = queue("red_apricorn_seed") { itemNameBlockItem(PokemodBlocks.RED_APRICORN_SAPLING.get(), ItemGroup.MISC) }
    val WHITE_APRICORN_SEED = queue("white_apricorn_seed") { itemNameBlockItem(PokemodBlocks.WHITE_APRICORN_SAPLING.get(), ItemGroup.MISC) }
    val YELLOW_APRICORN_SEED = queue("yellow_apricorn_seed") { itemNameBlockItem(PokemodBlocks.YELLOW_APRICORN_SAPLING.get(), ItemGroup.MISC) }

    val APRICORN_LOG = queue("apricorn_log") { blockItem(PokemodBlocks.APRICORN_LOG.get(), ItemGroup.BUILDING_BLOCKS) }
    val STRIPPED_APRICORN_LOG = queue("stripped_apricorn_log") { blockItem(PokemodBlocks.STRIPPED_APRICORN_LOG.get(), ItemGroup.BUILDING_BLOCKS) }
    val APRICORN_WOOD = queue("apricorn_wood") { blockItem(PokemodBlocks.APRICORN_WOOD.get(), ItemGroup.BUILDING_BLOCKS) }
    val STRIPPED_APRICORN_WOOD = queue("stripped_apricorn_wood") { blockItem(PokemodBlocks.STRIPPED_APRICORN_WOOD.get(), ItemGroup.BUILDING_BLOCKS) }
    val APRICORN_PLANKS = queue("apricorn_planks") { blockItem(PokemodBlocks.APRICORN_PLANKS.get(), ItemGroup.BUILDING_BLOCKS) }
    val APRICORN_LEAVES = queue("apricorn_leaves") { blockItem(PokemodBlocks.APRICORN_LEAVES.get(), ItemGroup.BUILDING_BLOCKS) }

    val APRICORN_DOOR = queue("apricorn_door") { blockItem(PokemodBlocks.APRICORN_DOOR.get(), ItemGroup.REDSTONE) }
    val APRICORN_TRAPDOOR = queue("apricorn_trapdoor") { blockItem(PokemodBlocks.APRICORN_TRAPDOOR.get(), ItemGroup.REDSTONE) }
    val APRICORN_FENCE = queue("apricorn_fence") { blockItem(PokemodBlocks.APRICORN_FENCE.get(), ItemGroup.DECORATIONS) }
    val APRICORN_FENCE_GATE = queue("apricorn_fence_gate") { blockItem(PokemodBlocks.APRICORN_FENCE_GATE.get(), ItemGroup.REDSTONE) }
    val APRICORN_BUTTON = queue("apricorn_button") { blockItem(PokemodBlocks.APRICORN_BUTTON.get(), ItemGroup.REDSTONE) }
    val APRICORN_PRESSURE_PLATE = queue("apricorn_pressure_plate") { blockItem(PokemodBlocks.APRICORN_PRESSURE_PLATE.get(), ItemGroup.REDSTONE) }
    //val APRICORN_SIGN = queue("apricorn_sign", SignItem(Item.Properties().stacksTo(16).tab(CreativeModeTab.TAB_DECORATIONS), CobbledBlocks.APRICORN_SIGN, CobbledBlocks.APRICORN_WALL_SIGN))
    val APRICORN_SLAB = queue("apricorn_slab") { blockItem(PokemodBlocks.APRICORN_SLAB.get(), ItemGroup.BUILDING_BLOCKS) }
    val APRICORN_STAIRS = queue("apricorn_stairs") { blockItem(PokemodBlocks.APRICORN_STAIRS.get(), ItemGroup.BUILDING_BLOCKS) }

    val HEALING_MACHINE = queue("healing_machine") { blockItem(PokemodBlocks.HEALING_MACHINE.get(), ItemGroup.REDSTONE) }
    val PC = queue("pc") { blockItem(PokemodBlocks.PC.get(), ItemGroup.REDSTONE) }

    // Evolution items
    val LINK_CABLE = queue("link_cable") { EvolutionItem() }
    val KINGS_ROCK = queue("kings_rock") { EvolutionItem() }
    val METAL_COAT = queue("metal_coat") { EvolutionItem() }
    val BLACK_AUGURITE = queue("black_augurite") { EvolutionItem() }
    val PROTECTOR = queue("protector") { EvolutionItem() }
    val OVAL_STONE = queue("oval_stone") { EvolutionItem() }
    val DRAGON_SCALE = queue("dragon_scale") { EvolutionItem() }
    val ELECTIRIZER = queue("electirizer") { EvolutionItem() }
    val MAGMARIZER = queue("magmarizer") { EvolutionItem() }
    val UPGRADE = queue("upgrade") { EvolutionItem() }
    val DUBIOUS_DISC = queue("dubious_disc") { EvolutionItem() }

    // Medicine
    val RARE_CANDY = queue("rare_candy") { CandyItem { _, pokemon -> pokemon.getExperienceToNextLevel() } }
    val EXPERIENCE_CANDY_XS = queue("exp_candy_xs") { CandyItem { _, _ -> CandyItem.DEFAULT_XS_CANDY_YIELD } }
    val EXPERIENCE_CANDY_S = queue("exp_candy_s") { CandyItem { _, _ -> CandyItem.DEFAULT_S_CANDY_YIELD } }
    val EXPERIENCE_CANDY_M = queue("exp_candy_m") { CandyItem { _, _ -> CandyItem.DEFAULT_M_CANDY_YIELD } }
    val EXPERIENCE_CANDY_L = queue("exp_candy_l") { CandyItem { _, _ -> CandyItem.DEFAULT_L_CANDY_YIELD } }
    val EXPERIENCE_CANDY_XL = queue("exp_candy_xl") { CandyItem { _, _ -> CandyItem.DEFAULT_XL_CANDY_YIELD } }

    private fun blockItem(block: Block, tab: ItemGroup) : BlockItem {
        return BlockItem(block, Item.Settings().group(tab))
    }

    private fun itemNameBlockItem(block: Block, tab: ItemGroup) : BlockItem {
        return AliasedBlockItem(block, Item.Settings().group(tab))
    }

    /**
     * Evolution Ores and Stones
     */
    val DAWN_STONE_ORE = queue("dawn_stone_ore") { blockItem(PokemodBlocks.DAWN_STONE_ORE.get(), ItemGroup.BUILDING_BLOCKS) }
    val DUSK_STONE_ORE = queue("dusk_stone_ore") { blockItem(PokemodBlocks.DUSK_STONE_ORE.get(), ItemGroup.BUILDING_BLOCKS) }
    val FIRE_STONE_ORE = queue("fire_stone_ore") { blockItem(PokemodBlocks.FIRE_STONE_ORE.get(), ItemGroup.BUILDING_BLOCKS) }
    val ICE_STONE_ORE = queue("ice_stone_ore") { blockItem(PokemodBlocks.ICE_STONE_ORE.get(), ItemGroup.BUILDING_BLOCKS) }
    val LEAF_STONE_ORE = queue("leaf_stone_ore") { blockItem(PokemodBlocks.LEAF_STONE_ORE.get(), ItemGroup.BUILDING_BLOCKS) }
    val MOON_STONE_ORE = queue("moon_stone_ore") { blockItem(PokemodBlocks.MOON_STONE_ORE.get(), ItemGroup.BUILDING_BLOCKS) }
    val SHINY_STONE_ORE = queue("shiny_stone_ore") { blockItem(PokemodBlocks.SHINY_STONE_ORE.get(), ItemGroup.BUILDING_BLOCKS) }
    val SUN_STONE_ORE = queue("sun_stone_ore") { blockItem(PokemodBlocks.SUN_STONE_ORE.get(), ItemGroup.BUILDING_BLOCKS) }
    val THUNDER_STONE_ORE = queue("thunder_stone_ore") { blockItem(PokemodBlocks.THUNDER_STONE_ORE.get(), ItemGroup.BUILDING_BLOCKS) }
    val WATER_STONE_ORE = queue("water_stone_ore") { blockItem(PokemodBlocks.WATER_STONE_ORE.get(), ItemGroup.BUILDING_BLOCKS) }
    val DEEPSLATE_DAWN_STONE_ORE = queue("deepslate_dawn_stone_ore") { blockItem(PokemodBlocks.DEEPSLATE_DAWN_STONE_ORE.get(), ItemGroup.BUILDING_BLOCKS) }
    val DEEPSLATE_DUSK_STONE_ORE = queue("deepslate_dusk_stone_ore") { blockItem(PokemodBlocks.DEEPSLATE_DUSK_STONE_ORE.get(), ItemGroup.BUILDING_BLOCKS) }
    val DEEPSLATE_FIRE_STONE_ORE = queue("deepslate_fire_stone_ore") { blockItem(PokemodBlocks.DEEPSLATE_FIRE_STONE_ORE.get(), ItemGroup.BUILDING_BLOCKS) }
    val DEEPSLATE_ICE_STONE_ORE = queue("deepslate_ice_stone_ore") { blockItem(PokemodBlocks.DEEPSLATE_ICE_STONE_ORE.get(), ItemGroup.BUILDING_BLOCKS) }
    val DEEPSLATE_LEAF_STONE_ORE = queue("deepslate_leaf_stone_ore") { blockItem(PokemodBlocks.DEEPSLATE_LEAF_STONE_ORE.get(), ItemGroup.BUILDING_BLOCKS) }
    val DEEPSLATE_MOON_STONE_ORE = queue("deepslate_moon_stone_ore") { blockItem(PokemodBlocks.DEEPSLATE_MOON_STONE_ORE.get(), ItemGroup.BUILDING_BLOCKS) }
    val DEEPSLATE_SHINY_STONE_ORE = queue("deepslate_shiny_stone_ore") { blockItem(PokemodBlocks.DEEPSLATE_SHINY_STONE_ORE.get(), ItemGroup.BUILDING_BLOCKS) }
    val DEEPSLATE_SUN_STONE_ORE = queue("deepslate_sun_stone_ore") { blockItem(PokemodBlocks.DEEPSLATE_SUN_STONE_ORE.get(), ItemGroup.BUILDING_BLOCKS) }
    val DEEPSLATE_THUNDER_STONE_ORE = queue("deepslate_thunder_stone_ore") { blockItem(PokemodBlocks.DEEPSLATE_THUNDER_STONE_ORE.get(), ItemGroup.BUILDING_BLOCKS) }
    val DEEPSLATE_WATER_STONE_ORE = queue("deepslate_water_stone_ore") { blockItem(PokemodBlocks.DEEPSLATE_WATER_STONE_ORE.get(), ItemGroup.BUILDING_BLOCKS) }
    val DRIPSTONE_MOON_STONE_ORE = queue("dripstone_moon_stone_ore") { blockItem(PokemodBlocks.DRIPSTONE_MOON_STONE_ORE.get(), ItemGroup.BUILDING_BLOCKS) }
    val DAWN_STONE = queue("dawn_stone") { DawnStone() }
    val DUSK_STONE = queue("dusk_stone") { DuskStone() }
    val FIRE_STONE = queue("fire_stone") { FireStone() }
    val ICE_STONE = queue("ice_stone") { IceStone() }
    val LEAF_STONE = queue("leaf_stone") { LeafStone() }
    val MOON_STONE = queue("moon_stone") { MoonStone() }
    val SHINY_STONE = queue("shiny_stone") { ShinyStone() }
    val SUN_STONE = queue("sun_stone") { SunStone() }
    val THUNDER_STONE = queue("thunder_stone") { ThunderStone() }
    val WATER_STONE = queue("water_stone") { WaterStone() }

    override fun register() {
        super.register()
        ballMap[PokeBalls.POKE_BALL] = POKE_BALL
        ballMap[PokeBalls.GREAT_BALL] = GREAT_BALL
        ballMap[PokeBalls.ULTRA_BALL] = ULTRA_BALL
        ballMap[PokeBalls.MASTER_BALL] = MASTER_BALL
    }
}