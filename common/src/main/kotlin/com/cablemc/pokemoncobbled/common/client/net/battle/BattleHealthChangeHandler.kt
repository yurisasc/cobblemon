/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.client.net.battle

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.battle.animations.HealthChangeAnimation
import com.cablemc.pokemoncobbled.common.client.net.ClientPacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.client.battle.BattleHealthChangePacket
import net.minecraft.client.MinecraftClient

object BattleHealthChangeHandler : ClientPacketHandler<BattleHealthChangePacket> {
    override fun invokeOnClient(packet: BattleHealthChangePacket, ctx: CobbledNetwork.NetworkContext) {
        MinecraftClient.getInstance().execute {
            val battle = PokemonCobbledClient.battle ?: return@execute
            val (_, activePokemon) = battle.getPokemonFromPNX(packet.pnx)
            activePokemon.animations.add(
                HealthChangeAnimation(newHealthRatio = packet.newHealthRatio)
            )
        }
    }
}