package com.cablemc.pokemoncobbled.common

import com.cablemc.pokemoncobbled.common.api.blocks.EvolutionStoneOre
import com.cablemc.pokemoncobbled.common.api.blocks.EvolutionStoneOre.Companion.DEEPSLATE_PROPERTIES
import com.cablemc.pokemoncobbled.common.api.blocks.EvolutionStoneOre.Companion.NORMAL_PROPERTIES
import dev.architectury.registry.registries.DeferredRegister
import net.minecraft.core.Registry
import net.minecraft.world.level.block.Block

object CobbledBlocks {
    private val blockRegister = DeferredRegister.create(PokemonCobbled.MODID, Registry.BLOCK_REGISTRY)
    private fun <T : Block> queue(name: String, block: T) = blockRegister.register(name) { block }

    /**
     * Evolution Ores
     */

    val DAWN_STONE_ORE = queue("dawn_stone_ore", EvolutionStoneOre(NORMAL_PROPERTIES))
    val DUSK_STONE_ORE = queue("dusk_stone_ore", EvolutionStoneOre(NORMAL_PROPERTIES))
    val FIRE_STONE_ORE = queue("fire_stone_ore", EvolutionStoneOre(NORMAL_PROPERTIES))
    val ICE_STONE_ORE = queue("ice_stone_ore", EvolutionStoneOre(NORMAL_PROPERTIES))
    val LEAF_STONE_ORE = queue("leaf_stone_ore", EvolutionStoneOre(NORMAL_PROPERTIES))
    val MOON_STONE_ORE = queue("moon_stone_ore", EvolutionStoneOre(NORMAL_PROPERTIES))
    val DRIPSTONE_MOON_STONE_ORE = queue("dripstone_moon_stone_ore", EvolutionStoneOre(NORMAL_PROPERTIES))
    val SHINY_STONE_ORE = queue("shiny_stone_ore", EvolutionStoneOre(NORMAL_PROPERTIES))
    val SUN_STONE_ORE = queue("sun_stone_ore", EvolutionStoneOre(NORMAL_PROPERTIES))
    val THUNDER_STONE_ORE = queue("thunder_stone_ore", EvolutionStoneOre(NORMAL_PROPERTIES))
    val WATER_STONE_ORE = queue("water_stone_ore", EvolutionStoneOre(NORMAL_PROPERTIES))

    /**
     * Deepslate separator
     */

    val DEEPSLATE_DAWN_STONE_ORE = queue("deepslate_dawn_stone_ore", EvolutionStoneOre(DEEPSLATE_PROPERTIES))
    val DEEPSLATE_DUSK_STONE_ORE = queue("deepslate_dusk_stone_ore", EvolutionStoneOre(DEEPSLATE_PROPERTIES))
    val DEEPSLATE_FIRE_STONE_ORE = queue("deepslate_fire_stone_ore", EvolutionStoneOre(DEEPSLATE_PROPERTIES))
    val DEEPSLATE_ICE_STONE_ORE = queue("deepslate_ice_stone_ore", EvolutionStoneOre(DEEPSLATE_PROPERTIES))
    val DEEPSLATE_LEAF_STONE_ORE = queue("deepslate_leaf_stone_ore", EvolutionStoneOre(DEEPSLATE_PROPERTIES))
    val DEEPSLATE_MOON_STONE_ORE = queue("deepslate_moon_stone_ore", EvolutionStoneOre(DEEPSLATE_PROPERTIES))
    val DEEPSLATE_SHINY_STONE_ORE = queue("deepslate_shiny_stone_ore", EvolutionStoneOre(DEEPSLATE_PROPERTIES))
    val DEEPSLATE_SUN_STONE_ORE = queue("deepslate_sun_stone_ore", EvolutionStoneOre(DEEPSLATE_PROPERTIES))
    val DEEPSLATE_THUNDER_STONE_ORE = queue("deepslate_thunder_stone_ore", EvolutionStoneOre(DEEPSLATE_PROPERTIES))
    val DEEPSLATE_WATER_STONE_ORE = queue("deepslate_water_stone_ore", EvolutionStoneOre(DEEPSLATE_PROPERTIES))


    fun register() {
        blockRegister.register()
    }
}