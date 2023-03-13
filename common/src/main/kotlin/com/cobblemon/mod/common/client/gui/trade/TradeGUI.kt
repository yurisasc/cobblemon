/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.gui.trade

import com.cobblemon.mod.common.CobblemonNetwork
import com.cobblemon.mod.common.client.trade.ClientTrade
import com.cobblemon.mod.common.net.messages.server.trade.CancelTradePacket
import com.cobblemon.mod.common.net.messages.server.trade.ChangeTradeAcceptancePacket
import com.cobblemon.mod.common.net.messages.server.trade.UpdateTradeOfferPacket
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.lang
import java.util.UUID
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.MutableText

/**
 * Notes for Village:
 * When your chosen trade Pokémon is changed, [UpdateTradeOfferPacket].
 * When you change ready status, [ChangeTradeAcceptancePacket].
 * When you cancel the trade, just run this.close()
 */
class TradeGUI(
    val trade: ClientTrade,
    val traderId: UUID,
    val traderName: MutableText
): Screen(lang("trade.gui.title")) {


    init {
        trade.cancelEmitter.subscribe {
            super.close()
            // Maybe a sound
        }
        trade.completedEmitter.subscribe {
            super.close()
            // Make a sound maybe
        }
        trade.oppositeOffer.subscribe { newOffer: Pokemon? ->
            // Update any GUI stuff
        }
        trade.oppositeAcceptedMyOffer.subscribe { acceptance ->
            // Swap it to say they are ready or w/e
        }
        trade.myOffer.subscribe { myOffer: Pokemon? ->
            // Update any GUI
        }
    }

    override fun close() {
        CobblemonNetwork.sendToServer(CancelTradePacket())
        super.close()
    }
}