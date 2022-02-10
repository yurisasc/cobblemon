package com.cablemc.pokemoncobbled.common.item

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.pokeball.PokeBalls
import com.cablemc.pokemoncobbled.common.blocks.BlockRegistry
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
    val FIRE_STONE_ORE = ITEMS.register("fire_stone_ore") {
        BlockItem(BlockRegistry.FIRE_STONE_ORE, Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS))
    }
    val DEEPSLATE_FIRE_STONE_ORE = ITEMS.register("deepslate_fire_stone_ore") {
        BlockItem(BlockRegistry.DEEPSLATE_FIRE_STONE_ORE, Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS))
    }

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