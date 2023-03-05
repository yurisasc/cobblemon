/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.trading

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.pokemon.evolution.variants.TradeEvolution
import java.util.UUID
import net.minecraft.server.network.ServerPlayerEntity

object TradeManager {
    class TradeRequest(val senderId: UUID, val receiverId: UUID)

    val requests = mutableListOf<TradeRequest>()
    val activeTrades = mutableListOf<ActiveTrade>()

    fun getExistingRequest(playerId: UUID) = requests.find { it.senderId == playerId || it.receiverId == playerId }
    fun getActiveTrade(playerId: UUID) =
        activeTrades.find { it.player1.uuid == playerId || it.player2.uuid == playerId }

    fun performTrade(player1: ServerPlayerEntity, pokemon1: Pokemon, player2: ServerPlayerEntity, pokemon2: Pokemon) {
        val party1 = Cobblemon.storage.getParty(player1)
        val party2 = Cobblemon.storage.getParty(player2)

        party1.remove(pokemon1)
        party2.remove(pokemon2)

        pokemon1.evolutions.filterIsInstance<TradeEvolution>().firstOrNull {
            it.attemptEvolution(pokemon1, pokemon2)
        }

        pokemon2.evolutions.filterIsInstance<TradeEvolution>().firstOrNull {
            it.attemptEvolution(pokemon2, pokemon1)
        }
    }
}