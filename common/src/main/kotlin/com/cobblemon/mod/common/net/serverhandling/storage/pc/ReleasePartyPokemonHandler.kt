/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.net.serverhandling.storage.pc

import com.cobblemon.mod.common.api.net.ServerNetworkPacketHandler
import com.cobblemon.mod.common.client.settings.ServerSettings
import com.cobblemon.mod.common.net.messages.server.storage.party.ReleasePartyPokemonPacket
import com.cobblemon.mod.common.util.party
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity

object ReleasePartyPokemonHandler : ServerNetworkPacketHandler<ReleasePartyPokemonPacket> {
    override fun handle(packet: ReleasePartyPokemonPacket, server: MinecraftServer, player: ServerPlayerEntity) {
        val party = player.party()
        val pokemon = party[packet.position] ?: return
        if (pokemon.uuid != packet.pokemonID) {
            return // Desync
        }
        if (ServerSettings.preventCompletePartyDeposit && party.filterNotNull().size <= 1) {
            return // Don't allow empty party
        }
        party.remove(pokemon)
    }
}