/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.pokemon.interaction

import com.cobblemon.mod.common.api.interaction.PokemonEntityInteraction
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
class FriendshipIncrementInteraction(
    private val amount: Int
) : PokemonEntityInteraction {

    constructor() : this(0)

    override val accepted: Set<PokemonEntityInteraction.Ownership> = EnumSet.of(PokemonEntityInteraction.Ownership.OWNER)

    override fun processInteraction(player: ServerPlayerEntity, entity: PokemonEntity, stack: ItemStack): Boolean {
        if (this.amount <= 0 || !entity.pokemon.isPossibleFriendship(entity.pokemon.friendship + this.amount)) {
            return false
        }
        entity.pokemon.incrementFriendship(this.amount)
        player.sendMessage(lang("interaction.friendsip.add"))
        return true
    }

    companion object {

        val TYPE_ID = cobblemonResource("friendship_increment")

    }

}