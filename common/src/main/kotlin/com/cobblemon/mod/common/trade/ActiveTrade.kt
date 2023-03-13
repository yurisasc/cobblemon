/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.trade

import com.cobblemon.mod.common.CobblemonNetwork.sendPacket
import com.cobblemon.mod.common.net.messages.client.trade.TradeAcceptanceChangedPacket
import com.cobblemon.mod.common.net.messages.client.trade.TradeCancelledPacket
import com.cobblemon.mod.common.net.messages.client.trade.TradeCompletedPacket
import com.cobblemon.mod.common.net.messages.client.trade.TradeUpdatedPacket
import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.server.network.ServerPlayerEntity

class ActiveTrade(val player1: ServerPlayerEntity, val player2: ServerPlayerEntity) {
    val player1Offer = TradeOffer()
    val player2Offer = TradeOffer()

    fun getOffer(player: ServerPlayerEntity) = if (player == player1) player1Offer else player2Offer
    fun getOpposingOffer(player: ServerPlayerEntity) = if (player == player1) player2Offer else player1Offer

    fun updateOffer(player: ServerPlayerEntity, pokemon: Pokemon?) {
        getOffer(player).updateOffer(pokemon)
        val packet = TradeUpdatedPacket(player.uuid, pokemon)
        getOppositePlayer(player).sendPacket(packet)
        player.sendPacket(packet)
    }

    fun updateAcceptance(player: ServerPlayerEntity, acceptance: Boolean) {
        val offer = getOpposingOffer(player)
        if (offer.accepted != acceptance) {
            offer.accepted = acceptance
            getOppositePlayer(player).sendPacket(TradeAcceptanceChangedPacket(offer.pokemon!!.uuid, acceptance))
        }

        if (offer.accepted && getOffer(player).accepted) {
            performTrade()
        }
    }

    fun getOppositePlayer(player: ServerPlayerEntity) = if (player == player1) player2 else player1

    fun performTrade() {
        TradeManager.performTrade(
            player1 = player1,
            player2 = player2,
            pokemon1 = player1Offer.pokemon!!,
            pokemon2 = player2Offer.pokemon!!
        )
        completeTrade()
    }

    fun cancelTrade() {
        val cancelTrade = TradeCancelledPacket()
        player1.sendPacket(cancelTrade)
        player2.sendPacket(cancelTrade)
    }

    fun completeTrade() {
        val completeTrade = TradeCompletedPacket()
        player1.sendPacket(completeTrade)
        player2.sendPacket(completeTrade)
    }
}