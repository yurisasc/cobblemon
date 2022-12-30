/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.api.pokemon.helditem

import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.item.ItemStack

/**
 * Responsible for querying Pokémon for the presence of held items and their consumption.
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
     * @param pokemon The [Pokemon] being queried for a held item.
     * @return The literal ID that showdown uses to represent this item such as 'abilityshield' if any.
     */
    fun showdownId(pokemon: Pokemon): String?

    /**
     * Consumes the item.
     * Note that it might not necessarily be the [Pokemon.heldItem].
     *
     * @param pokemon The [Pokemon] that is holding the item.
     */
    fun consume(pokemon: Pokemon)

}