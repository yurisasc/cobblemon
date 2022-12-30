/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.pokemon.helditem

import com.cobblemon.mod.common.battles.pokemon.BattlePokemon
import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.item.ItemStack

/**
 * Responsible for querying [BattlePokemon] for the presence of held items and their consumption.
 * These are invoked when creating battles and are only used for in battle data.
 * For operations on the [ItemStack] a [Pokemon] may be holding see [Pokemon.heldItem] and [Pokemon.swapHeldItem]
 *
 * @author Licious
 * @since December 30th, 2022
 */
interface HeldItemManager {

    /**
     * Queries the given [pokemon] for the presence of a held item.
     *
     * @param pokemon The [BattlePokemon] being queried for a held item.
     * @return The literal ID that showdown uses to represent this item such as 'abilityshield' if any.
     */
    fun showdownId(pokemon: BattlePokemon): String?

    /**
     * Consumes the item.
     * Note that it might not necessarily be the [Pokemon.heldItem].
     *
     * @param pokemon The [BattlePokemon] that is holding the item.
     */
    fun consume(pokemon: BattlePokemon)


    companion object {

        /**
         * A [HeldItemManager] that never finds an item ID nor consumes anything.
         * This is meant to be used when a battle Pokémon cannot be attached to any of the registered managers.
         */
        val EMPTY = object : HeldItemManager {
            override fun showdownId(pokemon: BattlePokemon): String? = null
            override fun consume(pokemon: BattlePokemon) {}
        }

    }

}