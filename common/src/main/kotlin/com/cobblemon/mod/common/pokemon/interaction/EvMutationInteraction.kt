/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokemon.interaction

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.interaction.PokemonEntityInteraction
import com.cobblemon.mod.common.api.pokemon.stats.Stat
import com.cobblemon.mod.common.api.pokemon.stats.Stats
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.cobblemonResource
import com.cobblemon.mod.common.util.lang
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import java.util.*

/**
 * An interaction that increments the [Pokemon.friendship].
 * This interaction consumes the [ItemStack] behind the trigger.
 *
 * @property amount The amount of friendship to increment.
 *
 * @author Licious
 * @since January 19th, 2022
 */
class EvMutationInteraction(
    private val stat: Stat,
    private val amount: Int
) : PokemonEntityInteraction {

    constructor() : this(Stats.HP, 0)

    override val accepted: Set<PokemonEntityInteraction.Ownership> = EnumSet.of(PokemonEntityInteraction.Ownership.OWNER)

    override fun processInteraction(player: ServerPlayerEntity, entity: PokemonEntity, stack: ItemStack): Boolean {
        if (!Cobblemon.statProvider.ofType(Stat.Type.PERMANENT).contains(this.stat) || entity.pokemon.evs.getOrDefault(this.stat) <= 0) {
            return false
        }
        val result = entity.pokemon.evs.add(this.stat, this.amount)
        if (result != 0) {
            val subKey = if (result > 0) "add" else "deduct"
            player.sendMessage(lang("interaction.ev.$subKey", entity.pokemon.displayName, this.stat.displayName, result))
            return true
        }
        return false
    }

    companion object {

        val TYPE_ID = cobblemonResource("ev_mutation")

    }

}