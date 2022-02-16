package com.cablemc.pokemoncobbled.common.item

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.pokeball.PokeBalls
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