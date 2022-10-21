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
import com.cablemc.pokemod.common.net.messages.client.battle.BattleUpdateTeamPokemonPacket
import net.minecraft.client.MinecraftClient

object BattleUpdateTeamPokemonHandler : ClientPacketHandler<BattleUpdateTeamPokemonPacket> {
    override fun invokeOnClient(packet: BattleUpdateTeamPokemonPacket, ctx: PokemodNetwork.NetworkContext) {
        val battle = PokemodClient.battle ?: return
        val actor = battle.side1.actors.find { it.uuid == MinecraftClient.getInstance().player?.uuid }
        if (actor != null) {
            val previous = actor.pokemon.find { it.uuid == packet.pokemon.uuid }
            if (previous !=  null) {
                actor.pokemon.add(actor.pokemon.indexOf(previous), packet.pokemon)
                actor.pokemon.remove(previous)
            }
        }
    }
}