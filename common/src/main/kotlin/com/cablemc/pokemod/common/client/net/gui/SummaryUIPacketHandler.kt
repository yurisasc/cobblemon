/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client.net.gui

import com.cablemc.pokemod.common.PokemodNetwork
import com.cablemc.pokemod.common.client.gui.summary.Summary
import com.cablemc.pokemod.common.client.net.ClientPacketHandler
import com.cablemc.pokemod.common.net.messages.client.ui.SummaryUIPacket
import net.minecraft.client.MinecraftClient

object SummaryUIPacketHandler: ClientPacketHandler<SummaryUIPacket> {
    override fun invokeOnClient(packet: SummaryUIPacket, ctx: PokemodNetwork.NetworkContext) {
        MinecraftClient.getInstance().setScreen(
            Summary(
                pokemon = packet.pokemonArray.toTypedArray(),
                editable = packet.editable
            )
        )
    }
}