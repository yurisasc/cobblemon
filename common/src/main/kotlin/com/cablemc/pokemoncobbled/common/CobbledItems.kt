package com.cablemc.pokemoncobbled.common

import com.cablemc.pokemoncobbled.common.api.pokeball.PokeBalls
import com.cablemc.pokemoncobbled.common.item.PokeBallItem
import com.cablemc.pokemoncobbled.common.item.interactive.EvolutionItem
import dev.architectury.registry.registries.DeferredRegister
import net.minecraft.core.Registry
import net.minecraft.world.item.Item

object CobbledItems {

    private val itemRegister = DeferredRegister.create(PokemonCobbled.MODID, Registry.ITEM_REGISTRY)

    // PokeBalls
    val POKE_BALL = queue("poke_ball", PokeBallItem(PokeBalls.POKE_BALL))

    // Evolution items
    val LINK_CABLE = queue("link_cable", EvolutionItem())
    val KINGS_ROCK = queue("kings_rock", EvolutionItem())
    val METAL_COAT = queue("metal_coat", EvolutionItem())
    val BLACK_AUGURITE = queue("black_augurite", EvolutionItem())
    val PROTECTOR = queue("protector", EvolutionItem())
    val OVAL_STONE = queue("oval_stone", EvolutionItem())
    val DRAGON_SCALE = queue("dragon_scale", EvolutionItem())
    val ELECTIRIZER = queue("electirizer", EvolutionItem())
    val MAGMARIZER = queue("magmarizer", EvolutionItem())
    val UPGRADE = queue("upgrade", EvolutionItem())
    val DUBIOUS_DISC = queue("dubious_disc", EvolutionItem())

    fun register() {
        itemRegister.register()
    }

    private fun <T : Item> queue(name: String, item: T): T {
        itemRegister.register(name) { item }
        return item
    }

}