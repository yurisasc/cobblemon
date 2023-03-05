/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.trading

import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.server.network.ServerPlayerEntity

class ActiveTrade(val player1: ServerPlayerEntity, val player2: ServerPlayerEntity) {
    val player1Offer = TradeOffer()
    val player2Offer = TradeOffer()

    fun getOffer(player: ServerPlayerEntity) = if (player == player1) player1Offer else player2Offer
    fun getOpposingOffer(player: ServerPlayerEntity) = if (player == player1) player2Offer else player1Offer

    fun updateOffer(player: ServerPlayerEntity, pokemon: Pokemon) {
        getOffer(player).updateOffer(pokemon)
    }

    fun acceptOffer(player: ServerPlayerEntity) {
        getOpposingOffer(player).accepted = true
    }

    fun performTrade() {
        TradeManager.performTrade(
            player1 = player1,
            player2 = player2,
            pokemon1 = player1Offer.pokemon!!,
            pokemon2 = player2Offer.pokemon!!
        )
    }
}