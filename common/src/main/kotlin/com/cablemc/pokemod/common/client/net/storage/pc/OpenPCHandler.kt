/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client.net.storage.pc

import com.cablemc.pokemod.common.PokemodNetwork
import com.cablemc.pokemod.common.client.PokemodClient
import com.cablemc.pokemod.common.client.gui.pc.PCGui
import com.cablemc.pokemod.common.client.net.ClientPacketHandler
import com.cablemc.pokemod.common.net.messages.client.storage.pc.OpenPCPacket
import net.minecraft.client.MinecraftClient

object OpenPCHandler : ClientPacketHandler<OpenPCPacket> {
    override fun invokeOnClient(packet: OpenPCPacket, ctx: PokemodNetwork.NetworkContext) {
        val pc = PokemodClient.storage.pcStores[packet.storeID] ?: return
        MinecraftClient.getInstance().setScreen(PCGui(pc, PokemodClient.storage.myParty))
    }
}