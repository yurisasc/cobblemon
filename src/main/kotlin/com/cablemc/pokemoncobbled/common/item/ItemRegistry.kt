package com.cablemc.pokemoncobbled.common.item

import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.api.pokemon.pokeball.Pokeballs
import net.minecraft.world.item.Item
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fmllegacy.RegistryObject
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries

/**
 * Registry for cobbled items
 */
class ItemRegistry {

    private val ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, PokemonCobbled.MODID)

    val POKE_BALL = registerItem("poke_ball", PokeballItem(Pokeballs.POKE_BALL))

    private inline fun registerItem(
        name: String,
        item: Item
    ): RegistryObject<Item> {
        return ITEMS.register(name) { item }
    }

    fun register(bus: IEventBus) {
        ITEMS.register(bus)
    }

}