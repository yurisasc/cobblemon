package com.cablemc.pokemoncobbled.common.blocks

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.blocks.EvolutionStoneOre
import com.cablemc.pokemoncobbled.common.api.blocks.EvolutionStoneOre.Companion.DEEPSLATE_PROPERTIES
import com.cablemc.pokemoncobbled.common.api.blocks.EvolutionStoneOre.Companion.NORMAL_PROPERTIES
import net.minecraft.world.level.block.Block
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject
import thedarkcolour.kotlinforforge.forge.registerObject

object BlockRegistry {
    private val BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, PokemonCobbled.MODID)

    /**
     * Evolution Ores
     */

    val DAWN_STONE_ORE by BLOCKS.registerObject("dawn_stone_ore") {
        EvolutionStoneOre(NORMAL_PROPERTIES)
    }
    val DUSK_STONE_ORE by BLOCKS.registerObject("dusk_stone_ore") {
        EvolutionStoneOre(NORMAL_PROPERTIES)
    }
    val FIRE_STONE_ORE by BLOCKS.registerObject("fire_stone_ore") {
        EvolutionStoneOre(NORMAL_PROPERTIES)
    }
    val ICE_STONE_ORE by BLOCKS.registerObject("ice_stone_ore") {
        EvolutionStoneOre(NORMAL_PROPERTIES)
    }
    val LEAF_STONE_ORE by BLOCKS.registerObject("leaf_stone_ore") {
        EvolutionStoneOre(NORMAL_PROPERTIES)
    }
    val MOON_STONE_ORE by BLOCKS.registerObject("moon_stone_ore") {
        EvolutionStoneOre(NORMAL_PROPERTIES)
    }
    val SHINY_STONE_ORE by BLOCKS.registerObject("shiny_stone_ore") {
        EvolutionStoneOre(NORMAL_PROPERTIES)
    }
    val SUN_STONE_ORE by BLOCKS.registerObject("sun_stone_ore") {
        EvolutionStoneOre(NORMAL_PROPERTIES)
    }
    val THUNDER_STONE_ORE by BLOCKS.registerObject("thunder_stone_ore") {
        EvolutionStoneOre(NORMAL_PROPERTIES)
    }
    val WATER_STONE_ORE by BLOCKS.registerObject("water_stone_ore") {
        EvolutionStoneOre(NORMAL_PROPERTIES)
    }

    /**
     * Deepslate separator
     */

    val DEEPSLATE_DAWN_STONE_ORE by BLOCKS.registerObject("deepslate_dawn_stone_ore") {
        EvolutionStoneOre(DEEPSLATE_PROPERTIES)
    }
    val DEEPSLATE_DUSK_STONE_ORE by BLOCKS.registerObject("deepslate_dusk_stone_ore") {
        EvolutionStoneOre(DEEPSLATE_PROPERTIES)
    }
    val DEEPSLATE_FIRE_STONE_ORE by BLOCKS.registerObject("deepslate_fire_stone_ore") {
        EvolutionStoneOre(DEEPSLATE_PROPERTIES)
    }
    val DEEPSLATE_ICE_STONE_ORE by BLOCKS.registerObject("deepslate_ice_stone_ore") {
        EvolutionStoneOre(DEEPSLATE_PROPERTIES)
    }
    val DEEPSLATE_LEAF_STONE_ORE by BLOCKS.registerObject("deepslate_leaf_stone_ore") {
        EvolutionStoneOre(DEEPSLATE_PROPERTIES)
    }
    val DEEPSLATE_MOON_STONE_ORE by BLOCKS.registerObject("deepslate_moon_stone_ore") {
        EvolutionStoneOre(DEEPSLATE_PROPERTIES)
    }
    val DEEPSLATE_SHINY_STONE_ORE by BLOCKS.registerObject("deepslate_shiny_stone_ore") {
        EvolutionStoneOre(DEEPSLATE_PROPERTIES)
    }
    val DEEPSLATE_SUN_STONE_ORE by BLOCKS.registerObject("deepslate_sun_stone_ore") {
        EvolutionStoneOre(DEEPSLATE_PROPERTIES)
    }
    val DEEPSLATE_THUNDER_STONE_ORE by BLOCKS.registerObject("deepslate_thunder_stone_ore") {
        EvolutionStoneOre(DEEPSLATE_PROPERTIES)
    }
    val DEEPSLATE_WATER_STONE_ORE by BLOCKS.registerObject("deepslate_water_stone_ore") {
        EvolutionStoneOre(DEEPSLATE_PROPERTIES)
    }

    private fun register(
        name: String,
        block: Block
    ): RegistryObject<Block> {
        println("Registering Block $name")
        return BLOCKS.register(name) { block }
    }

    fun register(bus: IEventBus) {
        BLOCKS.register(bus)
    }
}