package com.cablemc.pokemoncobbled.common.item.interactive

import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemoncobbled.common.item.CobbledCreativeTabs
import com.cablemc.pokemoncobbled.common.pokemon.evolution.ItemInteractionEvolution
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack

class EvolutionItem(properties: Properties = Properties().tab(CobbledCreativeTabs.EVOLUTION_ITEM_TAB)) : PokemonInteractiveItem(properties, Ownership.OWNER) {

    override fun processInteraction(player: ServerPlayer, entity: PokemonEntity, stack: ItemStack) {
        val pokemon = entity.pokemon
        pokemon.species.evolutions.filterIsInstance<ItemInteractionEvolution>()
            .sortedBy { evolution -> evolution.optional }
            .forEach { evolution ->
                if (evolution.attemptEvolution(pokemon, this)) {
                    if (!player.isCreative)
                        stack.shrink(1)
                    if (evolution.optional)
                        return
                }
            }
    }

}