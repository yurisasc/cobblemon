package com.cablemc.pokemoncobbled.common

import com.cablemc.pokemoncobbled.common.api.pokeball.PokeBalls
import com.cablemc.pokemoncobbled.common.item.PokeBallItem
import com.cablemc.pokemoncobbled.common.item.interactive.EvolutionItem
import dev.architectury.registry.registries.DeferredRegister
import net.minecraft.core.Registry
import net.minecraft.world.item.Item

object CobbledItems {
    private val itemRegister = DeferredRegister.create(PokemonCobbled.MODID, Registry.ITEM_REGISTRY)
    private fun <T : Item> queue(name: String, item: T) = itemRegister.register(name) { item }

    val POKE_BALL = queue("poke_ball", PokeBallItem(PokeBalls.POKE_BALL))
    val POKE_BALL_TYPE: PokeBallItem
        get() = POKE_BALL.get()


    // Evolution items
    val BLACK_AUGURITE = queue("black_augurite", EvolutionItem())
    val BLACK_AUGURITE_TYPE: EvolutionItem
        get() = BLACK_AUGURITE.get()
    val DRAGON_SCALE = queue("dragon_scale", EvolutionItem())
    val DRAGON_SCALE_TYPE: EvolutionItem
        get() = DRAGON_SCALE.get()
    val DUBIOUS_DISC = queue("dubious_disc", EvolutionItem())
    val DUBIOUS_DISC_TYPE: EvolutionItem
        get() = DUBIOUS_DISC.get()
    val ELECTIRIZER = queue("electirizer", EvolutionItem())
    val ELECTIRIZER_TYPE: EvolutionItem
        get() = ELECTIRIZER.get()
    val KINGS_ROCK = queue("kings_rock", EvolutionItem())
    val KINGS_ROCK_TYPE: EvolutionItem
        get() = KINGS_ROCK.get()
    val LINK_CABLE = queue("link_cable", EvolutionItem())
    val LINK_CABLE_TYPE: EvolutionItem
        get() = LINK_CABLE.get()
    val MAGMARIZER = queue("magmarizer", EvolutionItem())
    val MAGMARIZER_TYPE: EvolutionItem
        get() = MAGMARIZER.get()
    val METAL_COAT = queue("metal_coat", EvolutionItem())
    val METAL_COAT_TYPE: EvolutionItem
        get() = METAL_COAT.get()
    val OVAL_STONE = queue("oval_stone", EvolutionItem())
    val OVAL_STONE_TYPE: EvolutionItem
        get() = OVAL_STONE.get()
    val PROTECTOR = queue("protector", EvolutionItem())
    val PROTECTOR_TYPE: EvolutionItem
        get() = PROTECTOR.get()
    val UPGRADE = queue("upgrade", EvolutionItem())
    val UPGRADE_TYPE: EvolutionItem
        get() = UPGRADE.get()

    fun register() {
        itemRegister.register()
    }
}