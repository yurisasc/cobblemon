/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client.net.starter

import com.cablemc.pokemod.common.PokemodNetwork
import com.cablemc.pokemod.common.client.PokemodClient
import com.cablemc.pokemod.common.client.gui.startselection.StarterSelectionScreen
import com.cablemc.pokemod.common.client.net.ClientPacketHandler
import com.cablemc.pokemod.common.net.messages.client.starter.OpenStarterUIPacket
import net.minecraft.client.MinecraftClient

object StarterUIPacketHandler : ClientPacketHandler<OpenStarterUIPacket> {
    override fun invokeOnClient(packet: OpenStarterUIPacket, ctx: PokemodNetwork.NetworkContext) {
        PokemodClient.checkedStarterScreen = true
        MinecraftClient.getInstance().setScreen(StarterSelectionScreen(categories = packet.categories))
    }
}