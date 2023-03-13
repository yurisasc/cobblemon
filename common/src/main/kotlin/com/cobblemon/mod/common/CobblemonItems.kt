/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common

import com.cobblemon.mod.common.api.pokeball.PokeBalls
import com.cobblemon.mod.common.api.pokemon.helditem.HeldItemProvider
import com.cobblemon.mod.common.api.pokemon.stats.Stats
import com.cobblemon.mod.common.item.*
import com.cobblemon.mod.common.item.ApricornItem
import com.cobblemon.mod.common.item.ApricornSeedItem
import com.cobblemon.mod.common.item.CobblemonItem
import com.cobblemon.mod.common.item.PokeBallItem
import com.cobblemon.mod.common.item.PokemonItem
import com.cobblemon.mod.common.item.group.CobblemonItemGroups
import com.cobblemon.mod.common.item.interactive.CandyItem
import com.cobblemon.mod.common.item.interactive.LinkCableItem
import com.cobblemon.mod.common.item.interactive.VitaminItem
import com.cobblemon.mod.common.platform.PlatformRegistry
import com.cobblemon.mod.common.pokeball.PokeBall
import com.cobblemon.mod.common.pokemon.helditem.CobblemonHeldItemManager
import com.cobblemon.mod.common.registry.CompletableRegistry
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.block.Block
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys

object CobblemonItems : PlatformRegistry<Registry<Item>, RegistryKey<Registry<Item>>, Item>() {

    override val registry: Registry<Item> = Registries.ITEM
    override val registryKey: RegistryKey<Registry<Item>> = RegistryKeys.ITEM

    @JvmField
    val POKE_BALL = pokeballItem(PokeBalls.POKE_BALL)
    @JvmField
    val CITRINE_BALL = pokeballItem(PokeBalls.CITRINE_BALL)
    @JvmField
    val VERDANT_BALL = pokeballItem(PokeBalls.VERDANT_BALL)
    @JvmField
    val AZURE_BALL = pokeballItem(PokeBalls.AZURE_BALL)
    @JvmField
    val ROSEATE_BALL = pokeballItem(PokeBalls.ROSEATE_BALL)
    @JvmField
    val SLATE_BALL = pokeballItem(PokeBalls.SLATE_BALL)
    @JvmField
    val PREMIER_BALL = pokeballItem(PokeBalls.PREMIER_BALL)
    @JvmField
    val GREAT_BALL = pokeballItem(PokeBalls.GREAT_BALL)
    @JvmField
    val ULTRA_BALL = pokeballItem(PokeBalls.ULTRA_BALL)
    @JvmField
    val SAFARI_BALL = pokeballItem(PokeBalls.SAFARI_BALL)
    @JvmField
    val FAST_BALL = pokeballItem(PokeBalls.FAST_BALL)
    @JvmField
    val LEVEL_BALL = pokeballItem(PokeBalls.LEVEL_BALL)
    @JvmField
    val LURE_BALL = pokeballItem(PokeBalls.LURE_BALL)
    @JvmField
    val HEAVY_BALL = pokeballItem(PokeBalls.HEAVY_BALL)
    @JvmField
    val LOVE_BALL = pokeballItem(PokeBalls.LOVE_BALL)
    @JvmField
    val FRIEND_BALL = pokeballItem(PokeBalls.FRIEND_BALL)
    @JvmField
    val MOON_BALL = pokeballItem(PokeBalls.MOON_BALL)
    @JvmField
    val SPORT_BALL = pokeballItem(PokeBalls.SPORT_BALL)
    @JvmField
    val PARK_BALL = pokeballItem(PokeBalls.PARK_BALL)
    @JvmField
    val NET_BALL = pokeballItem(PokeBalls.NET_BALL)
    @JvmField
    val DIVE_BALL = pokeballItem(PokeBalls.DIVE_BALL)
    @JvmField
    val NEST_BALL = pokeballItem(PokeBalls.NEST_BALL)
    @JvmField
    val REPEAT_BALL = pokeballItem(PokeBalls.REPEAT_BALL)
    @JvmField
    val TIMER_BALL = pokeballItem(PokeBalls.TIMER_BALL)
    @JvmField
    val LUXURY_BALL = pokeballItem(PokeBalls.LUXURY_BALL)
    @JvmField
    val DUSK_BALL = pokeballItem(PokeBalls.DUSK_BALL)
    @JvmField
    val HEAL_BALL = pokeballItem(PokeBalls.HEAL_BALL)
    @JvmField
    val QUICK_BALL = pokeballItem(PokeBalls.QUICK_BALL)
    @JvmField
    val DREAM_BALL = pokeballItem(PokeBalls.DREAM_BALL)
    @JvmField
    val BEAST_BALL = pokeballItem(PokeBalls.BEAST_BALL)
    @JvmField
    val MASTER_BALL = pokeballItem(PokeBalls.MASTER_BALL)
    @JvmField
    val CHERISH_BALL = pokeballItem(PokeBalls.CHERISH_BALL)
    private val pokeBalls = arrayListOf<PokeBallItem>()

    @JvmField
    val RED_APRICORN = queue("red_apricorn") { ApricornItem(CobblemonBlocks.RED_APRICORN.get()) }
    @JvmField
    val YELLOW_APRICORN = queue("yellow_apricorn") { ApricornItem(CobblemonBlocks.YELLOW_APRICORN.get()) }
    @JvmField
    val GREEN_APRICORN = queue("green_apricorn") { ApricornItem(CobblemonBlocks.GREEN_APRICORN.get()) }
    @JvmField
    val BLUE_APRICORN = queue("blue_apricorn") { ApricornItem(CobblemonBlocks.BLUE_APRICORN.get()) }
    @JvmField
    val PINK_APRICORN = queue("pink_apricorn") { ApricornItem(CobblemonBlocks.PINK_APRICORN.get()) }
    @JvmField
    val BLACK_APRICORN = queue("black_apricorn") { ApricornItem(CobblemonBlocks.BLACK_APRICORN.get()) }
    @JvmField
    val WHITE_APRICORN = queue("white_apricorn") { ApricornItem(CobblemonBlocks.WHITE_APRICORN.get()) }

    @JvmField
    val RED_APRICORN_SEED = queue("red_apricorn_seed") { ApricornSeedItem(CobblemonBlocks.RED_APRICORN_SAPLING.get(), ItemGroup.MISC) }
    @JvmField
    val YELLOW_APRICORN_SEED = queue("yellow_apricorn_seed") { ApricornSeedItem(CobblemonBlocks.YELLOW_APRICORN_SAPLING.get(), ItemGroup.MISC) }
    @JvmField
    val GREEN_APRICORN_SEED = queue("green_apricorn_seed") { ApricornSeedItem(CobblemonBlocks.GREEN_APRICORN_SAPLING.get(), ItemGroup.MISC) }
    @JvmField
    val BLUE_APRICORN_SEED = queue("blue_apricorn_seed") { ApricornSeedItem(CobblemonBlocks.BLUE_APRICORN_SAPLING.get(), ItemGroup.MISC) }
    @JvmField
    val PINK_APRICORN_SEED = queue("pink_apricorn_seed") { ApricornSeedItem(CobblemonBlocks.PINK_APRICORN_SAPLING.get(), ItemGroup.MISC) }
    @JvmField
    val BLACK_APRICORN_SEED = queue("black_apricorn_seed") { ApricornSeedItem(CobblemonBlocks.BLACK_APRICORN_SAPLING.get(), ItemGroup.MISC) }
    @JvmField
    val WHITE_APRICORN_SEED = queue("white_apricorn_seed") { ApricornSeedItem(CobblemonBlocks.WHITE_APRICORN_SAPLING.get(), ItemGroup.MISC) }

    @JvmField
    val APRICORN_LOG = queue("apricorn_log") { blockItem(CobblemonBlocks.APRICORN_LOG.get(), ItemGroup.BUILDING_BLOCKS) }
    @JvmField
    val STRIPPED_APRICORN_LOG = queue("stripped_apricorn_log") { blockItem(CobblemonBlocks.STRIPPED_APRICORN_LOG.get(), ItemGroup.BUILDING_BLOCKS) }
    @JvmField
    val APRICORN_WOOD = queue("apricorn_wood") { blockItem(CobblemonBlocks.APRICORN_WOOD.get(), ItemGroup.BUILDING_BLOCKS) }
    @JvmField
    val STRIPPED_APRICORN_WOOD = queue("stripped_apricorn_wood") { blockItem(CobblemonBlocks.STRIPPED_APRICORN_WOOD.get(), ItemGroup.BUILDING_BLOCKS) }
    @JvmField
    val APRICORN_PLANKS = queue("apricorn_planks") { blockItem(CobblemonBlocks.APRICORN_PLANKS.get(), ItemGroup.BUILDING_BLOCKS) }
    @JvmField
    val APRICORN_LEAVES = queue("apricorn_leaves") { blockItem(CobblemonBlocks.APRICORN_LEAVES.get(), ItemGroup.BUILDING_BLOCKS) }

    @JvmField
    val APRICORN_DOOR = queue("apricorn_door") { blockItem(CobblemonBlocks.APRICORN_DOOR.get(), ItemGroup.REDSTONE) }
    @JvmField
    val APRICORN_TRAPDOOR = queue("apricorn_trapdoor") { blockItem(CobblemonBlocks.APRICORN_TRAPDOOR.get(), ItemGroup.REDSTONE) }
    @JvmField
    val APRICORN_FENCE = queue("apricorn_fence") { blockItem(CobblemonBlocks.APRICORN_FENCE.get(), ItemGroup.DECORATIONS) }
    @JvmField
    val APRICORN_FENCE_GATE = queue("apricorn_fence_gate") { blockItem(CobblemonBlocks.APRICORN_FENCE_GATE.get(), ItemGroup.REDSTONE) }
    @JvmField
    val APRICORN_BUTTON = queue("apricorn_button") { blockItem(CobblemonBlocks.APRICORN_BUTTON.get(), ItemGroup.REDSTONE) }
    @JvmField
    val APRICORN_PRESSURE_PLATE = queue("apricorn_pressure_plate") { blockItem(CobblemonBlocks.APRICORN_PRESSURE_PLATE.get(), ItemGroup.REDSTONE) }
    //@JvmField
//    val APRICORN_SIGN = queue("apricorn_sign", SignItem(Item.Properties().stacksTo(16).tab(CreativeModeTab.TAB_DECORATIONS), CobblemonBlocks.APRICORN_SIGN, CobblemonBlocks.APRICORN_WALL_SIGN))
    @JvmField
    val APRICORN_SLAB = queue("apricorn_slab") { blockItem(CobblemonBlocks.APRICORN_SLAB.get(), ItemGroup.BUILDING_BLOCKS) }
    @JvmField
    val APRICORN_STAIRS = queue("apricorn_stairs") { blockItem(CobblemonBlocks.APRICORN_STAIRS.get(), ItemGroup.BUILDING_BLOCKS) }

    @JvmField
    val HEALING_MACHINE = queue("healing_machine") { blockItem(CobblemonBlocks.HEALING_MACHINE.get(), ItemGroup.REDSTONE) }
    @JvmField
    val PC = queue("pc") { blockItem(CobblemonBlocks.PC.get(), ItemGroup.REDSTONE) }

    // Evolution items
    @JvmField val LINK_CABLE = this.queue("link_cable") { LinkCableItem() }
    @JvmField val DRAGON_SCALE = this.evolutionItem("dragon_scale")
    @JvmField val KINGS_ROCK = this.evolutionItem("kings_rock")
    @JvmField val METAL_COAT = this.evolutionItem("metal_coat")
    @JvmField val UPGRADE = this.evolutionItem("upgrade")
    @JvmField val DUBIOUS_DISC = this.evolutionItem("dubious_disc")
    @JvmField val DEEP_SEA_SCALE = this.evolutionItem("deep_sea_scale")
    @JvmField val DEEP_SEA_TOOTH = this.evolutionItem("deep_sea_tooth")
    @JvmField val ELECTIRIZER = this.evolutionItem("electirizer")
    @JvmField val MAGMARIZER = this.evolutionItem("magmarizer")
    @JvmField val OVAL_STONE = this.evolutionItem("oval_stone")
    @JvmField val PROTECTOR = this.evolutionItem("protector")
    @JvmField val REAPER_CLOTH = this.evolutionItem("reaper_cloth")
    @JvmField val PRISM_SCALE = this.evolutionItem("prism_scale")
    @JvmField val SACHET = this.evolutionItem("sachet")
    @JvmField val WHIPPED_DREAM = this.evolutionItem("whipped_dream")
    @JvmField val STRAWBERRY_SWEET = this.evolutionItem("strawberry_sweet")
    @JvmField val LOVE_SWEET = this.evolutionItem("love_sweet")
    @JvmField val BERRY_SWEET = this.evolutionItem("berry_sweet")
    @JvmField val CLOVER_SWEET = this.evolutionItem("clover_sweet")
    @JvmField val FLOWER_SWEET = this.evolutionItem("flower_sweet")
    @JvmField val STAR_SWEET = this.evolutionItem("star_sweet")
    @JvmField val RIBBON_SWEET = this.evolutionItem("ribbon_sweet")
    @JvmField val CHIPPED_POT = this.evolutionItem("chipped_pot")
    @JvmField val CRACKED_POT = this.evolutionItem("cracked_pot")
    @JvmField val SWEET_APPLE = this.evolutionItem("sweet_apple")
    @JvmField val TART_APPLE = this.evolutionItem("tart_apple")
    @JvmField val GALARICA_CUFF = this.evolutionItem("galarica_cuff")
    @JvmField val GALARICA_WREATH = this.evolutionItem("galarica_wreath")
    @JvmField val BLACK_AUGURITE = this.evolutionItem("black_augurite")
    @JvmField val PEAT_BLOCK = this.evolutionItem("peat_block")
    @JvmField val RAZOR_CLAW = this.evolutionItem("razor_claw")
    @JvmField val RAZOR_FANG = this.evolutionItem("razor_fang")
    // ToDo enable me when malicious armor goes in the game
    //@JvmField val AUSPICIOUS_ARMOR = this.heldItem("auspicious_armor")

    // Medicine
    @JvmField
    val RARE_CANDY = queue("rare_candy") { CandyItem { _, pokemon -> pokemon.getExperienceToNextLevel() } }
    @JvmField
    val EXPERIENCE_CANDY_XS = queue("exp_candy_xs") { CandyItem { _, _ -> CandyItem.DEFAULT_XS_CANDY_YIELD } }
    @JvmField
    val EXPERIENCE_CANDY_S = queue("exp_candy_s") { CandyItem { _, _ -> CandyItem.DEFAULT_S_CANDY_YIELD } }
    @JvmField
    val EXPERIENCE_CANDY_M = queue("exp_candy_m") { CandyItem { _, _ -> CandyItem.DEFAULT_M_CANDY_YIELD } }
    @JvmField
    val EXPERIENCE_CANDY_L = queue("exp_candy_l") { CandyItem { _, _ -> CandyItem.DEFAULT_L_CANDY_YIELD } }
    @JvmField
    val EXPERIENCE_CANDY_XL = queue("exp_candy_xl") { CandyItem { _, _ -> CandyItem.DEFAULT_XL_CANDY_YIELD } }
    @JvmField
    val CALCIUM = queue("calcium") { VitaminItem(Stats.SPECIAL_ATTACK) }
    @JvmField
    val CARBOS = queue("carbos") { VitaminItem(Stats.SPEED) }
    @JvmField
    val HP_UP = queue("hp_up") { VitaminItem(Stats.HP) }
    @JvmField
    val IRON = queue("iron") { VitaminItem(Stats.DEFENCE) }
    @JvmField
    val PROTEIN = queue("protein") { VitaminItem(Stats.ATTACK) }
    @JvmField
    val ZINC = queue("zinc") { VitaminItem(Stats.SPECIAL_DEFENCE) }

    /**
     * Evolution Ores and Stones
     */
    val DAWN_STONE_ORE = blockItem("dawn_stone_ore", CobblemonBlocks.DAWN_STONE_ORE)
    val DUSK_STONE_ORE = blockItem("dusk_stone_ore", CobblemonBlocks.DUSK_STONE_ORE)
    val FIRE_STONE_ORE = blockItem("fire_stone_ore", CobblemonBlocks.FIRE_STONE_ORE)
    val ICE_STONE_ORE = blockItem("ice_stone_ore", CobblemonBlocks.ICE_STONE_ORE)
    val LEAF_STONE_ORE = blockItem("leaf_stone_ore", CobblemonBlocks.LEAF_STONE_ORE)
    val MOON_STONE_ORE = blockItem("moon_stone_ore", CobblemonBlocks.MOON_STONE_ORE)
    val SHINY_STONE_ORE = blockItem("shiny_stone_ore", CobblemonBlocks.SHINY_STONE_ORE)
    val SUN_STONE_ORE = blockItem("sun_stone_ore", CobblemonBlocks.SUN_STONE_ORE)
    val THUNDER_STONE_ORE = blockItem("thunder_stone_ore", CobblemonBlocks.THUNDER_STONE_ORE)
    val WATER_STONE_ORE = blockItem("water_stone_ore", CobblemonBlocks.WATER_STONE_ORE)
    val DEEPSLATE_DAWN_STONE_ORE = blockItem("deepslate_dawn_stone_ore", CobblemonBlocks.DEEPSLATE_DAWN_STONE_ORE)
    val DEEPSLATE_DUSK_STONE_ORE = blockItem("deepslate_dusk_stone_ore", CobblemonBlocks.DEEPSLATE_DUSK_STONE_ORE)
    val DEEPSLATE_FIRE_STONE_ORE = blockItem("deepslate_fire_stone_ore", CobblemonBlocks.DEEPSLATE_FIRE_STONE_ORE)
    val DEEPSLATE_ICE_STONE_ORE = blockItem("deepslate_ice_stone_ore", CobblemonBlocks.DEEPSLATE_ICE_STONE_ORE)
    val DEEPSLATE_LEAF_STONE_ORE = blockItem("deepslate_leaf_stone_ore", CobblemonBlocks.DEEPSLATE_LEAF_STONE_ORE)
    val DEEPSLATE_MOON_STONE_ORE = blockItem("deepslate_moon_stone_ore", CobblemonBlocks.DEEPSLATE_MOON_STONE_ORE)
    val DEEPSLATE_SHINY_STONE_ORE = blockItem("deepslate_shiny_stone_ore", CobblemonBlocks.DEEPSLATE_SHINY_STONE_ORE)
    val DEEPSLATE_SUN_STONE_ORE = blockItem("deepslate_sun_stone_ore", CobblemonBlocks.DEEPSLATE_SUN_STONE_ORE)
    val DEEPSLATE_THUNDER_STONE_ORE = blockItem("deepslate_thunder_stone_ore", CobblemonBlocks.DEEPSLATE_THUNDER_STONE_ORE)
    val DEEPSLATE_WATER_STONE_ORE = blockItem("deepslate_water_stone_ore", CobblemonBlocks.DEEPSLATE_FIRE_STONE_ORE)
    val DRIPSTONE_MOON_STONE_ORE = blockItem("dripstone_moon_stone_ore", CobblemonBlocks.DRIPSTONE_MOON_STONE_ORE)
    val DAWN_STONE = evolutionItem("dawn_stone")
    val DUSK_STONE = evolutionItem("dusk_stone")
    val FIRE_STONE = evolutionItem("fire_stone")
    val ICE_STONE = evolutionItem("ice_stone")
    val LEAF_STONE = evolutionItem("leaf_stone")
    val MOON_STONE = evolutionItem("moon_stone")
    val SHINY_STONE = evolutionItem("shiny_stone")
    val SUN_STONE = evolutionItem("sun_stone")
    val THUNDER_STONE = evolutionItem("thunder_stone")
    val WATER_STONE = evolutionItem("water_stone")
    @JvmField
    val DAWN_STONE_ORE = queue("dawn_stone_ore") { blockItem(CobblemonBlocks.DAWN_STONE_ORE.get(), ItemGroup.BUILDING_BLOCKS) }
    @JvmField
    val DUSK_STONE_ORE = queue("dusk_stone_ore") { blockItem(CobblemonBlocks.DUSK_STONE_ORE.get(), ItemGroup.BUILDING_BLOCKS) }
    @JvmField
    val FIRE_STONE_ORE = queue("fire_stone_ore") { blockItem(CobblemonBlocks.FIRE_STONE_ORE.get(), ItemGroup.BUILDING_BLOCKS) }
    @JvmField
    val ICE_STONE_ORE = queue("ice_stone_ore") { blockItem(CobblemonBlocks.ICE_STONE_ORE.get(), ItemGroup.BUILDING_BLOCKS) }
    @JvmField
    val LEAF_STONE_ORE = queue("leaf_stone_ore") { blockItem(CobblemonBlocks.LEAF_STONE_ORE.get(), ItemGroup.BUILDING_BLOCKS) }
    @JvmField
    val MOON_STONE_ORE = queue("moon_stone_ore") { blockItem(CobblemonBlocks.MOON_STONE_ORE.get(), ItemGroup.BUILDING_BLOCKS) }
    @JvmField
    val SHINY_STONE_ORE = queue("shiny_stone_ore") { blockItem(CobblemonBlocks.SHINY_STONE_ORE.get(), ItemGroup.BUILDING_BLOCKS) }
    @JvmField
    val SUN_STONE_ORE = queue("sun_stone_ore") { blockItem(CobblemonBlocks.SUN_STONE_ORE.get(), ItemGroup.BUILDING_BLOCKS) }
    @JvmField
    val THUNDER_STONE_ORE = queue("thunder_stone_ore") { blockItem(CobblemonBlocks.THUNDER_STONE_ORE.get(), ItemGroup.BUILDING_BLOCKS) }
    @JvmField
    val WATER_STONE_ORE = queue("water_stone_ore") { blockItem(CobblemonBlocks.WATER_STONE_ORE.get(), ItemGroup.BUILDING_BLOCKS) }
    @JvmField
    val DEEPSLATE_DAWN_STONE_ORE = queue("deepslate_dawn_stone_ore") { blockItem(CobblemonBlocks.DEEPSLATE_DAWN_STONE_ORE.get(), ItemGroup.BUILDING_BLOCKS) }
    @JvmField
    val DEEPSLATE_DUSK_STONE_ORE = queue("deepslate_dusk_stone_ore") { blockItem(CobblemonBlocks.DEEPSLATE_DUSK_STONE_ORE.get(), ItemGroup.BUILDING_BLOCKS) }
    @JvmField
    val DEEPSLATE_FIRE_STONE_ORE = queue("deepslate_fire_stone_ore") { blockItem(CobblemonBlocks.DEEPSLATE_FIRE_STONE_ORE.get(), ItemGroup.BUILDING_BLOCKS) }
    @JvmField
    val DEEPSLATE_ICE_STONE_ORE = queue("deepslate_ice_stone_ore") { blockItem(CobblemonBlocks.DEEPSLATE_ICE_STONE_ORE.get(), ItemGroup.BUILDING_BLOCKS) }
    @JvmField
    val DEEPSLATE_LEAF_STONE_ORE = queue("deepslate_leaf_stone_ore") { blockItem(CobblemonBlocks.DEEPSLATE_LEAF_STONE_ORE.get(), ItemGroup.BUILDING_BLOCKS) }
    @JvmField
    val DEEPSLATE_MOON_STONE_ORE = queue("deepslate_moon_stone_ore") { blockItem(CobblemonBlocks.DEEPSLATE_MOON_STONE_ORE.get(), ItemGroup.BUILDING_BLOCKS) }
    @JvmField
    val DEEPSLATE_SHINY_STONE_ORE = queue("deepslate_shiny_stone_ore") { blockItem(CobblemonBlocks.DEEPSLATE_SHINY_STONE_ORE.get(), ItemGroup.BUILDING_BLOCKS) }
    @JvmField
    val DEEPSLATE_SUN_STONE_ORE = queue("deepslate_sun_stone_ore") { blockItem(CobblemonBlocks.DEEPSLATE_SUN_STONE_ORE.get(), ItemGroup.BUILDING_BLOCKS) }
    @JvmField
    val DEEPSLATE_THUNDER_STONE_ORE = queue("deepslate_thunder_stone_ore") { blockItem(CobblemonBlocks.DEEPSLATE_THUNDER_STONE_ORE.get(), ItemGroup.BUILDING_BLOCKS) }
    @JvmField
    val DEEPSLATE_WATER_STONE_ORE = queue("deepslate_water_stone_ore") { blockItem(CobblemonBlocks.DEEPSLATE_WATER_STONE_ORE.get(), ItemGroup.BUILDING_BLOCKS) }
    @JvmField
    val DRIPSTONE_MOON_STONE_ORE = queue("dripstone_moon_stone_ore") { blockItem(CobblemonBlocks.DRIPSTONE_MOON_STONE_ORE.get(), ItemGroup.BUILDING_BLOCKS) }
    @JvmField
    val DAWN_STONE = this.evolutionItem("dawn_stone")
    @JvmField
    val DUSK_STONE = this.evolutionItem("dusk_stone")
    @JvmField
    val FIRE_STONE = this.evolutionItem("fire_stone")
    @JvmField
    val ICE_STONE = this.evolutionItem("ice_stone")
    @JvmField
    val LEAF_STONE = this.evolutionItem("leaf_stone")
    @JvmField
    val MOON_STONE = this.evolutionItem("moon_stone")
    @JvmField
    val SHINY_STONE = this.evolutionItem("shiny_stone")
    @JvmField
    val SUN_STONE = this.evolutionItem("sun_stone")
    @JvmField
    val THUNDER_STONE = this.evolutionItem("thunder_stone")
    @JvmField
    val WATER_STONE = this.evolutionItem("water_stone")

    // Held Items
    @JvmField
    val ASSAULT_VEST = heldItem("assault_vest")
    @JvmField
    val BIG_ROOT = this.heldItem("big_root")
    @JvmField
    val BLACK_BELT = this.heldItem("black_belt")
    @JvmField
    val BLACK_GLASSES = heldItem("black_glasses")
    @JvmField
    val BLACK_SLUDGE = this.heldItem("black_sludge")
    @JvmField
    val CHARCOAL = this.heldItem("charcoal_stick", remappedName = "charcoal")
    @JvmField
    val CHOICE_BAND = heldItem("choice_band")
    @JvmField
    val CHOICE_SCARF = this.heldItem("choice_scarf")
    @JvmField
    val CHOICE_SPECS = heldItem("choice_specs")
    @JvmField
    val DRAGON_FANG = this.heldItem("dragon_fang")
    @JvmField
    val EXP_SHARE = heldItem("exp_share")
    @JvmField
    val FOCUS_BAND = heldItem("focus_band")
    @JvmField
    val HARD_STONE = heldItem("hard_stone")
    @JvmField
    val HEAVY_DUTY_BOOTS = heldItem("heavy_duty_boots")
    @JvmField
    val LEFTOVERS = this.heldItem("leftovers")
    @JvmField
    val LIGHT_CLAY = this.heldItem("light_clay")
    @JvmField
    val LUCKY_EGG = heldItem("lucky_egg")
    @JvmField
    val MAGNET = this.heldItem("magnet")
    @JvmField
    val MIRACLE_SEED = this.heldItem("miracle_seed")
    @JvmField
    val MUSCLE_BAND = heldItem("muscle_band")
    @JvmField
    val MYSTIC_WATER = this.heldItem("mystic_water")
    @JvmField
    val NEVER_MELT_ICE = this.heldItem("never_melt_ice")
    @JvmField
    val POISON_BARB = this.heldItem("poison_barb")
    @JvmField
    val QUICK_CLAW = this.heldItem("quick_claw")
    @JvmField
    val ROCKY_HELMET = heldItem("rocky_helmet")
    @JvmField
    val SAFETY_GOGGLES = heldItem("safety_goggles")
    @JvmField
    val SHARP_BEAK = this.heldItem("sharp_beak")
    @JvmField
    val SILK_SCARF = this.heldItem("silk_scarf")
    @JvmField
    val SILVER_POWDER = this.heldItem("silver_powder")
    @JvmField
    val SOFT_SAND = this.heldItem("soft_sand")
    @JvmField
    val SPELL_TAG = this.heldItem("spell_tag")
    @JvmField
    val TWISTED_SPOON = this.heldItem("twisted_spoon")
    @JvmField
    val WISE_GLASSES = heldItem("wise_glasses")

    // Misc
    @JvmField
    val POKEMON_MODEL = this.create("pokemon_model", PokemonItem())

    fun pokeBalls(): List<PokeBallItem> = pokeBalls.toList()

    private fun blockItem(name: String, block: Block): BlockItem = this.create(name, BlockItem(block, Item.Settings()))

    private fun evolutionItem(name: String): CobblemonItem = this.create(name, CobblemonItem(Item.Settings()))

    private fun pokeBallItem(pokeBall: PokeBall): PokeBallItem {
        val item = this.create(pokeBall.name.path, PokeBallItem(pokeBall))
        pokeBall.item = item
        pokeBalls.add(item)
        return item
    }

    private fun heldItem(name: String): CobblemonItem = this.create(name, CobblemonItem(Item.Settings()))

    private fun candyItem(name: String, calculator: CandyItem.Calculator): CandyItem  = this.create(name, CandyItem(calculator))

    private fun itemNameBlockItem(block: Block, tab: ItemGroup) : BlockItem {
        return AliasedBlockItem(block, Item.Settings().group(tab))
    }

    private fun evolutionItem(name: String) = queue(name) { CobblemonItem(Item.Settings().group(CobblemonItemGroups.EVOLUTION_ITEM_GROUP)) }

    private fun pokeballItem(pokeBall: PokeBall): RegistrySupplier<PokeBallItem> {
        val supplier = this.queue(pokeBall.name.path) { PokeBallItem(pokeBall) }
        pokeBall.itemSupplier = supplier
        this.pokeballs.add(supplier)
        return supplier
    }

    private fun heldItem(name: String, remappedName: String? = null): RegistrySupplier<CobblemonItem> = queue(name) {
        val item = CobblemonItem(Item.Settings().group(CobblemonItemGroups.HELD_ITEM_GROUP))
        if (remappedName != null) {
            CobblemonHeldItemManager.registerRemap(item, remappedName)
        }
        return@queue item
    }

    // ToDo
    fun registerToItemGroups(consumer: (ItemGroup, Item) -> Unit) {
        pokeBalls.forEach { item -> consumer(CobblemonItemGroups.POKE_BALLS.group(), item) }
        CobblemonItemGroups.MACHINES.group().let { group ->
            consumer(group, HEALING_MACHINE)
            consumer(group, PC)
        }
        CobblemonItemGroups.EVOLUTION_ITEMS.group().let { group ->
            consumer(group, LINK_CABLE)
            consumer(group, KINGS_ROCK)
            consumer(group, BLACK_AUGURITE)
            consumer(group, PROTECTOR)
            consumer(group, OVAL_STONE)
            consumer(group, DRAGON_SCALE)
            consumer(group, ELECTIRIZER)
            consumer(group, MAGMARIZER)
            consumer(group, UPGRADE)
            consumer(group, KINGS_ROCK)
            consumer(group, DUBIOUS_DISC)
            consumer(group, DAWN_STONE)
            consumer(group, DUSK_STONE)
            consumer(group, FIRE_STONE)
            consumer(group, ICE_STONE)
            consumer(group, LEAF_STONE)
            consumer(group, MOON_STONE)
            consumer(group, SHINY_STONE)
            consumer(group, SUN_STONE)
            consumer(group, THUNDER_STONE)
            consumer(group, WATER_STONE)
        }
        CobblemonItemGroups.HELD_ITEMS.group().let { group ->
            consumer(group, ASSAULT_VEST)
            consumer(group, BLACK_GLASSES)
            consumer(group, CHOICE_BAND)
            consumer(group, CHOICE_SPECS)
            consumer(group, EXP_SHARE)
            consumer(group, FOCUS_BAND)
            consumer(group, HARD_STONE)
            consumer(group, HEAVY_DUTY_BOOTS)
            consumer(group, LUCKY_EGG)
            consumer(group, MUSCLE_BAND)
            consumer(group, ROCKY_HELMET)
            consumer(group, SAFETY_GOGGLES)
            consumer(group, WISE_GLASSES)
        }
        CobblemonItemGroups.MEDICINE.group().let { group ->
            consumer(group, RARE_CANDY)
            consumer(group, EXPERIENCE_CANDY_XS)
            consumer(group, EXPERIENCE_CANDY_S)
            consumer(group, EXPERIENCE_CANDY_M)
            consumer(group, EXPERIENCE_CANDY_L)
            consumer(group, EXPERIENCE_CANDY_XL)
            consumer(group, HP_UP)
            consumer(group, PROTEIN)
            consumer(group, IRON)
            consumer(group, CALCIUM)
            consumer(group, ZINC)
            consumer(group, CARBOS)
        }
        CobblemonItemGroups.PLANTS.group().let { group ->
            consumer(group, RED_APRICORN)
            consumer(group, YELLOW_APRICORN)
            consumer(group, GREEN_APRICORN)
            consumer(group, BLUE_APRICORN)
            consumer(group, PINK_APRICORN)
            consumer(group, BLACK_APRICORN)
            consumer(group, WHITE_APRICORN)
            consumer(group, RED_APRICORN_SEED)
            consumer(group, YELLOW_APRICORN_SEED)
            consumer(group, GREEN_APRICORN_SEED)
            consumer(group, BLUE_APRICORN_SEED)
            consumer(group, PINK_APRICORN_SEED)
            consumer(group, BLACK_APRICORN_SEED)
            consumer(group, WHITE_APRICORN_SEED)
        }
        CobblemonItemGroups.BUILDING_BLOCKS.group().let { group ->
            consumer(group, APRICORN_LOG)
            consumer(group, STRIPPED_APRICORN_LOG)
            consumer(group, APRICORN_WOOD)
            consumer(group, STRIPPED_APRICORN_WOOD)
            consumer(group, APRICORN_PLANKS)
            consumer(group, APRICORN_LEAVES)
            consumer(group, APRICORN_DOOR)
            consumer(group, APRICORN_TRAPDOOR)
            consumer(group, APRICORN_FENCE)
            consumer(group, APRICORN_FENCE_GATE)
            consumer(group, APRICORN_BUTTON)
            consumer(group, APRICORN_PRESSURE_PLATE)
            // consumer(group, APRICORN_SIGN)
            consumer(group, APRICORN_SLAB)
            consumer(group, APRICORN_STAIRS)
            consumer(group, DAWN_STONE_ORE)
            consumer(group, DEEPSLATE_DAWN_STONE_ORE)
            consumer(group, DUSK_STONE_ORE)
            consumer(group, DEEPSLATE_DUSK_STONE_ORE)
            consumer(group, FIRE_STONE_ORE)
            consumer(group, DEEPSLATE_FIRE_STONE_ORE)
            consumer(group, ICE_STONE_ORE)
            consumer(group, DEEPSLATE_ICE_STONE_ORE)
            consumer(group, LEAF_STONE_ORE)
            consumer(group, DEEPSLATE_LEAF_STONE_ORE)
            consumer(group, MOON_STONE_ORE)
            consumer(group, DEEPSLATE_MOON_STONE_ORE)
            consumer(group, DRIPSTONE_MOON_STONE_ORE)
            consumer(group, SHINY_STONE_ORE)
            consumer(group, DEEPSLATE_SHINY_STONE_ORE)
            consumer(group, SUN_STONE_ORE)
            consumer(group, DEEPSLATE_SUN_STONE_ORE)
            consumer(group, THUNDER_STONE_ORE)
            consumer(group, DEEPSLATE_THUNDER_STONE_ORE)
            consumer(group, WATER_STONE_ORE)
            consumer(group, DEEPSLATE_WATER_STONE_ORE)
        }
    }

}