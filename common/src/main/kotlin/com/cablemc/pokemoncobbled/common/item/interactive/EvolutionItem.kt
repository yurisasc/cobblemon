package com.cablemc.pokemoncobbled.common.item.interactive

import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemoncobbled.common.item.CobbledItemGroups
import com.cablemc.pokemoncobbled.common.pokemon.evolution.ItemInteractionEvolution
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity

class EvolutionItem(properties: Settings = Settings().group(CobbledItemGroups.EVOLUTION_ITEM_GROUP)) : PokemonInteractiveItem(properties, Ownership.OWNER) {

    override fun processInteraction(player: ServerPlayerEntity, entity: PokemonEntity, stack: ItemStack) {
        val pokemon = entity.pokemon
        pokemon.species.evolutions.filterIsInstance<ItemInteractionEvolution>()
            .sortedBy { evolution -> evolution.optional }
            .forEach { evolution ->
                if (evolution.attemptEvolution(pokemon, this)) {
                    this.consumeItem(player, stack)
                }
            }
    }

}