/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common

import com.cobblemon.mod.common.api.pokeball.PokeBalls
import com.cobblemon.mod.common.api.pokemon.stats.Stats
import com.cobblemon.mod.common.item.*
import com.cobblemon.mod.common.item.group.CobblemonItemGroups
import com.cobblemon.mod.common.item.interactive.CandyItem
import com.cobblemon.mod.common.item.interactive.LinkCableItem
import com.cobblemon.mod.common.item.interactive.VitaminItem
import com.cobblemon.mod.common.pokeball.PokeBall
import com.cobblemon.mod.common.registry.PlatformRegistry
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

    private val pokeBalls = arrayListOf<PokeBallItem>()

    val POKE_BALL = pokeBallItem(PokeBalls.POKE_BALL)
    val CITRINE_BALL = pokeBallItem(PokeBalls.CITRINE_BALL)
    val VERDANT_BALL = pokeBallItem(PokeBalls.VERDANT_BALL)
    val AZURE_BALL = pokeBallItem(PokeBalls.AZURE_BALL)
    val ROSEATE_BALL = pokeBallItem(PokeBalls.ROSEATE_BALL)
    val SLATE_BALL = pokeBallItem(PokeBalls.SLATE_BALL)
    val PREMIER_BALL = pokeBallItem(PokeBalls.PREMIER_BALL)
    val GREAT_BALL = pokeBallItem(PokeBalls.GREAT_BALL)
    val ULTRA_BALL = pokeBallItem(PokeBalls.ULTRA_BALL)
    val SAFARI_BALL = pokeBallItem(PokeBalls.SAFARI_BALL)
    val FAST_BALL = pokeBallItem(PokeBalls.FAST_BALL)
    val LEVEL_BALL = pokeBallItem(PokeBalls.LEVEL_BALL)
    val LURE_BALL = pokeBallItem(PokeBalls.LURE_BALL)
    val HEAVY_BALL = pokeBallItem(PokeBalls.HEAVY_BALL)
    val LOVE_BALL = pokeBallItem(PokeBalls.LOVE_BALL)
    val FRIEND_BALL = pokeBallItem(PokeBalls.FRIEND_BALL)
    val MOON_BALL = pokeBallItem(PokeBalls.MOON_BALL)
    val SPORT_BALL = pokeBallItem(PokeBalls.SPORT_BALL)
    val PARK_BALL = pokeBallItem(PokeBalls.PARK_BALL)
    val NET_BALL = pokeBallItem(PokeBalls.NET_BALL)
    val DIVE_BALL = pokeBallItem(PokeBalls.DIVE_BALL)
    val NEST_BALL = pokeBallItem(PokeBalls.NEST_BALL)
    val REPEAT_BALL = pokeBallItem(PokeBalls.REPEAT_BALL)
    val TIMER_BALL = pokeBallItem(PokeBalls.TIMER_BALL)
    val LUXURY_BALL = pokeBallItem(PokeBalls.LUXURY_BALL)
    val DUSK_BALL = pokeBallItem(PokeBalls.DUSK_BALL)
    val HEAL_BALL = pokeBallItem(PokeBalls.HEAL_BALL)
    val QUICK_BALL = pokeBallItem(PokeBalls.QUICK_BALL)
    val DREAM_BALL = pokeBallItem(PokeBalls.DREAM_BALL)
    val BEAST_BALL = pokeBallItem(PokeBalls.BEAST_BALL)
    val MASTER_BALL = pokeBallItem(PokeBalls.MASTER_BALL)
    val CHERISH_BALL = pokeBallItem(PokeBalls.CHERISH_BALL)

    val RED_APRICORN = this.create("red_apricorn", ApricornItem(CobblemonBlocks.RED_APRICORN))
    val YELLOW_APRICORN = this.create("yellow_apricorn", ApricornItem(CobblemonBlocks.YELLOW_APRICORN))
    val GREEN_APRICORN = this.create("green_apricorn", ApricornItem(CobblemonBlocks.GREEN_APRICORN))
    val BLUE_APRICORN = this.create("blue_apricorn", ApricornItem(CobblemonBlocks.BLUE_APRICORN))
    val PINK_APRICORN = this.create("pink_apricorn", ApricornItem(CobblemonBlocks.PINK_APRICORN))
    val BLACK_APRICORN = this.create("black_apricorn", ApricornItem(CobblemonBlocks.BLACK_APRICORN))
    val WHITE_APRICORN = this.create("white_apricorn", ApricornItem(CobblemonBlocks.WHITE_APRICORN))

    val RED_APRICORN_SEED = this.create("red_apricorn_seed", ApricornSeedItem(CobblemonBlocks.RED_APRICORN_SAPLING))
    val YELLOW_APRICORN_SEED = this.create("yellow_apricorn_seed", ApricornSeedItem(CobblemonBlocks.YELLOW_APRICORN_SAPLING))
    val GREEN_APRICORN_SEED = this.create("green_apricorn_seed", ApricornSeedItem(CobblemonBlocks.GREEN_APRICORN_SAPLING))
    val BLUE_APRICORN_SEED = this.create("blue_apricorn_seed", ApricornSeedItem(CobblemonBlocks.BLUE_APRICORN_SAPLING))
    val PINK_APRICORN_SEED = this.create("pink_apricorn_seed", ApricornSeedItem(CobblemonBlocks.PINK_APRICORN_SAPLING))
    val BLACK_APRICORN_SEED = this.create("black_apricorn_seed", ApricornSeedItem(CobblemonBlocks.BLACK_APRICORN_SAPLING))
    val WHITE_APRICORN_SEED = this.create("white_apricorn_seed", ApricornSeedItem(CobblemonBlocks.WHITE_APRICORN_SAPLING))

    val APRICORN_LOG = blockItem("apricorn_log", CobblemonBlocks.APRICORN_LOG)
    val STRIPPED_APRICORN_LOG = blockItem("stripped_apricorn_log", CobblemonBlocks.STRIPPED_APRICORN_LOG)
    val APRICORN_WOOD = blockItem("apricorn_wood", CobblemonBlocks.APRICORN_WOOD)
    val STRIPPED_APRICORN_WOOD = blockItem("stripped_apricorn_wood", CobblemonBlocks.STRIPPED_APRICORN_WOOD)
    val APRICORN_PLANKS = blockItem("apricorn_planks", CobblemonBlocks.APRICORN_PLANKS)
    val APRICORN_LEAVES = blockItem("apricorn_leaves", CobblemonBlocks.APRICORN_LEAVES)

    val APRICORN_DOOR = blockItem("apricorn_door", CobblemonBlocks.APRICORN_DOOR)
    val APRICORN_TRAPDOOR = blockItem("apricorn_trapdoor", CobblemonBlocks.APRICORN_TRAPDOOR)
    val APRICORN_FENCE = blockItem("apricorn_fence", CobblemonBlocks.APRICORN_FENCE)
    val APRICORN_FENCE_GATE = blockItem("apricorn_fence_gate", CobblemonBlocks.APRICORN_FENCE_GATE)
    val APRICORN_BUTTON = blockItem("apricorn_button", CobblemonBlocks.APRICORN_BUTTON)
    val APRICORN_PRESSURE_PLATE = blockItem("apricorn_pressure_plate", CobblemonBlocks.APRICORN_PRESSURE_PLATE)
    //val APRICORN_SIGN = queue("apricorn_sign", SignItem(Item.Properties().stacksTo(16).tab(CreativeModeTab.TAB_DECORATIONS), CobblemonBlocks.APRICORN_SIGN, CobblemonBlocks.APRICORN_WALL_SIGN))
    val APRICORN_SLAB = blockItem("apricorn_slab", CobblemonBlocks.APRICORN_SLAB)
    val APRICORN_STAIRS = blockItem("apricorn_stairs", CobblemonBlocks.APRICORN_STAIRS)

    val HEALING_MACHINE = blockItem("healing_machine", CobblemonBlocks.HEALING_MACHINE)
    val PC = blockItem("pc", CobblemonBlocks.PC)

    // Evolution items
    val LINK_CABLE = this.create("link_cable", LinkCableItem())
    val KINGS_ROCK = evolutionItem("kings_rock")
    val METAL_COAT = evolutionItem("metal_coat")
    val BLACK_AUGURITE = evolutionItem("black_augurite")
    val PROTECTOR = evolutionItem("protector")
    val OVAL_STONE = evolutionItem("oval_stone")
    val DRAGON_SCALE = evolutionItem("dragon_scale")
    val ELECTIRIZER = evolutionItem("electirizer")
    val MAGMARIZER = evolutionItem("magmarizer")
    val UPGRADE = evolutionItem("upgrade")
    val DUBIOUS_DISC = evolutionItem("dubious_disc")

    // Medicine
    val RARE_CANDY = candyItem("rare_candy") { _, pokemon -> pokemon.getExperienceToNextLevel() }
    val EXPERIENCE_CANDY_XS = candyItem("exp_candy_xs") { _, _ -> CandyItem.DEFAULT_XS_CANDY_YIELD }
    val EXPERIENCE_CANDY_S = candyItem("exp_candy_s") { _, _ -> CandyItem.DEFAULT_S_CANDY_YIELD }
    val EXPERIENCE_CANDY_M = candyItem("exp_candy_m") { _, _ -> CandyItem.DEFAULT_M_CANDY_YIELD }
    val EXPERIENCE_CANDY_L = candyItem("exp_candy_l") { _, _ -> CandyItem.DEFAULT_L_CANDY_YIELD }
    val EXPERIENCE_CANDY_XL = candyItem("exp_candy_xl") { _, _ -> CandyItem.DEFAULT_XL_CANDY_YIELD }
    val HP_UP = this.create("hp_up", VitaminItem(Stats.HP))
    val PROTEIN = this.create("protein", VitaminItem(Stats.ATTACK))
    val IRON = this.create("iron", VitaminItem(Stats.DEFENCE))
    val CALCIUM = this.create("calcium", VitaminItem(Stats.SPECIAL_ATTACK))
    val ZINC = this.create("zinc", VitaminItem(Stats.SPECIAL_DEFENCE))
    val CARBOS = this.create("carbos", VitaminItem(Stats.SPEED))

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

    // Held Items
    val ASSAULT_VEST = heldItem("assault_vest")
    val BLACK_GLASSES = heldItem("black_glasses")
    val CHOICE_BAND = heldItem("choice_band")
    val CHOICE_SPECS = heldItem("choice_specs")
    val EXP_SHARE = heldItem("exp_share")
    val FOCUS_BAND = heldItem("focus_band")
    val HARD_STONE = heldItem("hard_stone")
    val HEAVY_DUTY_BOOTS = heldItem("heavy_duty_boots")
    val LUCKY_EGG = heldItem("lucky_egg")
    val MUSCLE_BAND = heldItem("muscle_band")
    val ROCKY_HELMET = heldItem("rocky_helmet")
    val SAFETY_GOGGLES = heldItem("safety_goggles")
    val WISE_GLASSES = heldItem("wise_glasses")

    // Misc
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