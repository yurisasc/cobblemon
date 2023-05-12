/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.trade

import com.cobblemon.mod.common.pokemon.Pokemon
import java.util.UUID

class ActiveTrade(val player1: TradeParticipant, val player2: TradeParticipant) {
    val player1Offer = TradeOffer()
    val player2Offer = TradeOffer()

    fun getTradeParticipant(uuid: UUID) = if (player1.uuid == uuid) player1 else player2
    fun getOffer(tradeParticipant: TradeParticipant) = if (tradeParticipant == player1) player1Offer else player2Offer
    fun getOpposingOffer(tradeParticipant: TradeParticipant) = if (tradeParticipant == player1) player2Offer else player1Offer

    fun updateOffer(tradeParticipant: TradeParticipant, pokemon: Pokemon?) {
        getOffer(tradeParticipant).updateOffer(pokemon)
        player1.updateOffer(tradeParticipant, pokemon)
        player2.updateOffer(tradeParticipant, pokemon)
    }

    fun updateAcceptance(tradeParticipant: TradeParticipant, acceptance: Boolean) {
        val offer = getOpposingOffer(tradeParticipant)
        if (offer.accepted != acceptance) {
            offer.accepted = acceptance
            getOppositePlayer(tradeParticipant).changeTradeAcceptance(offer.pokemon!!.uuid, acceptance)
        }

        if (offer.accepted && getOffer(tradeParticipant).accepted) {
            performTrade()
        }
    }

    fun getOppositePlayer(tradeParticipant: TradeParticipant) = if (tradeParticipant == player1) player2 else player1

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
        player1.cancelTrade()
        player2.cancelTrade()
    }

    fun completeTrade() {
        player1.completeTrade()
        player2.completeTrade()
    }
}