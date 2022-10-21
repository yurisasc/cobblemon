/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client.net.battle

import com.cablemc.pokemod.common.PokemodNetwork
import com.cablemc.pokemod.common.client.PokemodClient
import com.cablemc.pokemod.common.client.net.ClientPacketHandler
import com.cablemc.pokemod.common.net.messages.client.battle.BattleSetTeamPokemonPacket
import net.minecraft.client.MinecraftClient

object BattleSetTeamPokemonHandler : ClientPacketHandler<BattleSetTeamPokemonPacket> {
    override fun invokeOnClient(packet: BattleSetTeamPokemonPacket, ctx: PokemodNetwork.NetworkContext) {
        PokemodClient.battle!!.side1.actors
            .find { it.uuid == MinecraftClient.getInstance().player?.uuid }
            ?.pokemon = packet.team
    }
}