package com.cablemc.pokemoncobbled.common

import com.cablemc.pokemoncobbled.common.api.pokeball.PokeBalls
import com.cablemc.pokemoncobbled.common.item.PokeBallItem
import dev.architectury.registry.registries.DeferredRegister
import net.minecraft.core.Registry
import net.minecraft.world.item.Item

object CobbledItems {
    private val itemRegister = DeferredRegister.create(PokemonCobbled.MODID, Registry.ITEM_REGISTRY)
    private fun <T : Item> queue(name: String, item: T) = itemRegister.register(name) { item }

    val POKE_BALL = queue("poke_ball", PokeBallItem(PokeBalls.POKE_BALL))
    val POKE_BALL_TYPE: PokeBallItem
        get() = POKE_BALL.get()

    fun register() {
        itemRegister.register()
    }
}