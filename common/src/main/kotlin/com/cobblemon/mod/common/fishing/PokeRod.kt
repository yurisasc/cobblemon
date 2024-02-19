/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.fishing

import com.cobblemon.mod.common.api.pokeball.PokeBalls
import com.cobblemon.mod.common.api.pokeball.catching.CaptureEffect
import com.cobblemon.mod.common.api.pokeball.catching.CatchRateModifier
import com.cobblemon.mod.common.item.PokeBallItem
import com.cobblemon.mod.common.item.interactive.PokerodItem
import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier

/**
 * Base poke rod object
 * It is intended that there is one poke rod object initialized for a given poke rod type.
 *
 * @property name the poke rod registry name
 * @property bobberType The [ItemStack] of this Pokérod that is the bobber.
 * @property lineColor list of [RGB] values that apply to the fishing line of the Pokérod
 */
open class PokeRod(
    val name: Identifier,
    val bobberType: ItemStack,
    val lineColor: Triple<Int, Int, Int>
) {

    // This gets attached during item registry
    internal lateinit var item: PokerodItem

    fun item(): PokerodItem = this.item

    fun stack(count: Int = 1): ItemStack = ItemStack(this.item(), count)

}