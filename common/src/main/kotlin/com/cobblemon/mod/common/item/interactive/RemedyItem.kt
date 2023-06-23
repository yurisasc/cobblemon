/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.item.interactive

import com.bedrockk.molang.runtime.MoLangRuntime
import com.cobblemon.mod.common.CobblemonMechanics
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.item.CobblemonItem
import com.cobblemon.mod.common.util.asExpression
import com.cobblemon.mod.common.util.resolveDouble
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity

class RemedyItem(val remedyStrength: String) : CobblemonItem(Settings()), PokemonInteractiveItem {
    override val accepted = setOf(PokemonInteractiveItem.Ownership.OWNER)
    companion object {
        const val NORMAL = "normal"
        const val FINE = "fine"
        const val SUPERB = "superb"
        private val runtime = MoLangRuntime()
    }

    override fun processInteraction(player: ServerPlayerEntity, entity: PokemonEntity, stack: ItemStack): Boolean {
        val amount = runtime.resolveDouble(CobblemonMechanics.remedies[remedyStrength] ?: "20".asExpression())
        entity.pokemon.currentHealth += amount.toInt()
        stack.decrement(1)
        return true
    }
}