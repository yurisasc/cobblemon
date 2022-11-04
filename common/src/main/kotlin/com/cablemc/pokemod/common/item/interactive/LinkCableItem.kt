/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.item.interactive

import com.cablemc.pokemod.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemod.common.item.PokemodItemGroups
import com.cablemc.pokemod.common.pokemon.evolution.variants.TradeEvolution
import com.cablemc.pokemod.common.util.asTranslated
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity

class LinkCableItem : PokemonInteractiveItem(Settings().group(PokemodItemGroups.EVOLUTION_ITEM_GROUP), Ownership.OWNER) {

    override fun processInteraction(player: ServerPlayerEntity, entity: PokemonEntity, stack: ItemStack): Boolean {
        val pokemon = entity.pokemon
        pokemon.evolutions.filterIsInstance<TradeEvolution>().forEach { evolution ->
            // If an evolution is possible non-optional or has been successfully queued we will consume the item and stop
            if (evolution.evolve(pokemon)) {
                this.consumeItem(player, stack)
                return true
            }
        }
        return false
    }

}