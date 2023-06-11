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
 * An interaction that allows the player to select and restore the PP of a move in the [Pokemon.moveSet].
 * This interaction consumes the [ItemStack] behind the trigger.
 *
 * @author Licious
 * @since January 19th, 2022
 */
class PpRestoreInteraction : PokemonEntityInteraction {

    override val accepted: Set<PokemonEntityInteraction.Ownership> = EnumSet.of(PokemonEntityInteraction.Ownership.OWNER)

    override fun processInteraction(player: ServerPlayerEntity, entity: PokemonEntity, stack: ItemStack): Boolean {
        val eligibleMoves = entity.pokemon.moveSet.filter { move -> move.currentPp != move.maxPp }
        if (eligibleMoves.isEmpty()) {
            return false
        }
        if (eligibleMoves.size == 1) {
            val move = eligibleMoves.first()
            move.currentPp = move.maxPp
            entity.pokemon.moveSet.update()
            player.sendMessage(lang("interaction.pp.restore", move.name))
            return true
        }
        // ToDo send out eligibleMoves into a menu the player cannot close.
        return true
    }

    companion object {

        val TYPE_ID = cobblemonResource("pp_restore")

    }

}