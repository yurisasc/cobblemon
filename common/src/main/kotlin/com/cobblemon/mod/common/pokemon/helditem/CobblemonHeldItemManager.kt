/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokemon.helditem

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.pokemon.helditem.HeldItemManager
import com.cobblemon.mod.common.item.CobblemonItem
import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.item.ItemStack
import net.minecraft.util.registry.Registry

object CobblemonHeldItemManager : HeldItemManager {

    override fun showdownId(pokemon: Pokemon): String? {
        val item = pokemon.heldItem().item
        val identifier = Registry.ITEM.getId(item)
        if (identifier.namespace == Cobblemon.MODID) {
            TODO("Implement a way to dynamically grab IDs from showdown and match identifier path")
        }
        return null
    }

    override fun consume(pokemon: Pokemon) {
        pokemon.swapHeldItem(ItemStack.EMPTY)
    }

}