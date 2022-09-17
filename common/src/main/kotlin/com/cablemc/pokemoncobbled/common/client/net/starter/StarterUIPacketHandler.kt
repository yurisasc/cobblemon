/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.client.net.starter

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.gui.startselection.StarterSelectionScreen
import com.cablemc.pokemoncobbled.common.client.net.ClientPacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.client.starter.OpenStarterUIPacket
import net.minecraft.client.MinecraftClient

object StarterUIPacketHandler : ClientPacketHandler<OpenStarterUIPacket> {
    override fun invokeOnClient(packet: OpenStarterUIPacket, ctx: CobbledNetwork.NetworkContext) {
        PokemonCobbledClient.checkedStarterScreen = true
        MinecraftClient.getInstance().setScreen(StarterSelectionScreen(categories = packet.categories))
    }
}