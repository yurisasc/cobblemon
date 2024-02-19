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
 * @property catchRateModifier The [CatchRateModifier] of this Pokéball.
 * @property effects list of all [CaptureEffect]s applicable to the Pokéball
 * @property waterDragValue The value of the water drag modifier when the entity travels, default is 0.8.
 * @property model2d The identifier for the resource this Pokéball will use for the 2d model.
 * @property model3d The identifier for the resource this Pokéball will use for the 3d model.
 */
open class PokeRod(
    val name: Identifier
) {

    // This gets attached during item registry
    internal lateinit var item: PokerodItem

    fun item(): PokerodItem = this.item

    fun stack(count: Int = 1): ItemStack = ItemStack(this.item(), count)

}