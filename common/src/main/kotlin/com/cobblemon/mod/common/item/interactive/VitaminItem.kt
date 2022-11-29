/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.item.interactive

import com.cobblemon.mod.common.api.item.PokemonInteractiveItem
import com.cobblemon.mod.common.api.pokemon.stats.Stat
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.item.CobblemonItem
import com.cobblemon.mod.common.item.CobblemonItemGroups
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import java.util.*

class VitaminItem(
    val stat: Stat
) : PokemonInteractiveItem, CobblemonItem(Settings().group(CobblemonItemGroups.MEDICINE_ITEM_GROUP)) {

    override val accepted: Set<PokemonInteractiveItem.Ownership> = EnumSet.of(PokemonInteractiveItem.Ownership.OWNER)

    override fun processInteraction(player: ServerPlayerEntity, entity: PokemonEntity, stack: ItemStack): Boolean {
        val pokemon = entity.pokemon
        val evsGained = pokemon.evs.add(stat, EV_YIELD)
        if (evsGained > 0) {
            this.consumeItem(player, stack)
            return true
        }
        return false
    }

    companion object {
        const val EV_YIELD = 10
    }
}