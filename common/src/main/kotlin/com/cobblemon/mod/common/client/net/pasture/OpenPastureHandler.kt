/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.net.pasture

import com.cobblemon.mod.common.CobblemonNetwork
import com.cobblemon.mod.common.api.net.ClientNetworkPacketHandler
import com.cobblemon.mod.common.api.reactive.SettableObservable
import com.cobblemon.mod.common.client.CobblemonClient
import com.cobblemon.mod.common.client.gui.pasture.PasturePCGUIConfiguration
import com.cobblemon.mod.common.client.gui.pc.PCGUI
import com.cobblemon.mod.common.client.gui.pc.PCGUIConfiguration
import com.cobblemon.mod.common.net.messages.client.pasture.OpenPasturePacket
import com.cobblemon.mod.common.net.messages.server.pasture.PasturePokemonPacket
import java.util.UUID
import net.minecraft.client.MinecraftClient

object OpenPastureHandler : ClientNetworkPacketHandler<OpenPasturePacket> {
    override fun handle(packet: OpenPasturePacket, client: MinecraftClient) {

        val pcConfiguration = PasturePCGUIConfiguration(
            pastureId = UUID.randomUUID(),
            pasturePos = packet.pasturePos,
            pasturedPokemon = SettableObservable(packet.tetheredPokemon)
        )

        client.setScreen(PCGUI(pc = CobblemonClient.storage.pcStores[packet.pcId]!!, party = CobblemonClient.storage.myParty, configuration = pcConfiguration))
    }
}