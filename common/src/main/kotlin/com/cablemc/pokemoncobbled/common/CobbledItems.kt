package com.cablemc.pokemoncobbled.common

import dev.architectury.registry.registries.DeferredRegister
import net.minecraft.core.Registry
import net.minecraft.world.item.Item

object CobbledItems {
    private val itemRegister = DeferredRegister.create(PokemonCobbled.MODID, Registry.ITEM_REGISTRY)
    private fun queue(name: String, item: Item) = itemRegister.register(name) { item }

    // TODO uncomment once entity stuff is moved over
//    val POKE_BALL = queue("poke_ball", PokeBallItem(PokeBalls.POKE_BALL))

    fun register() {
        itemRegister.register()
    }
}