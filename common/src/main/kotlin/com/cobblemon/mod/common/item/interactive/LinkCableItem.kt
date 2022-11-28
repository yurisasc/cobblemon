/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.item.interactive

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.item.CobblemonItem
import com.cobblemon.mod.common.item.CobblemonItemGroups
import com.cobblemon.mod.common.pokemon.evolution.variants.TradeEvolution
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import java.util.*

class LinkCableItem : PokemonInteractiveItem, CobblemonItem(Settings().group(CobblemonItemGroups.EVOLUTION_ITEM_GROUP)) {

    override val accepted: Set<PokemonInteractiveItem.Ownership> = EnumSet.of(PokemonInteractiveItem.Ownership.OWNER)

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