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

    //val FIRE_STONE_ORE = register("fire_stone_ore", EvolutionStoneOre(NORMAL_PROPERTIES))
    val FIRE_STONE_ORE by BLOCKS.registerObject("fire_stone_ore") {
        EvolutionStoneOre(NORMAL_PROPERTIES)
    }
    val DEEPSLATE_FIRE_STONE_ORE by BLOCKS.registerObject("deepslate_fire_stone_ore") {
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