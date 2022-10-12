/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client.net.battle

import com.cablemc.pokemod.common.PokemodNetwork
import com.cablemc.pokemod.common.client.PokemodClient
import com.cablemc.pokemod.common.client.battle.ActiveClientBattlePokemon
import com.cablemc.pokemod.common.client.battle.ClientBattle
import com.cablemc.pokemod.common.client.battle.ClientBattleActor
import com.cablemc.pokemod.common.client.battle.ClientBattlePokemon
import com.cablemc.pokemod.common.client.net.ClientPacketHandler
import com.cablemc.pokemod.common.net.messages.client.battle.BattleInitializePacket
import net.minecraft.client.MinecraftClient

object BattleInitializeHandler : ClientPacketHandler<BattleInitializePacket> {
    override fun invokeOnClient(packet: BattleInitializePacket, ctx: PokemodNetwork.NetworkContext) {
        val playerUUID = MinecraftClient.getInstance().player?.uuid
        PokemodClient.battle = ClientBattle(
            packet.battleId,
            packet.battleFormat
        ).apply {
            val mySide = if (packet.side2.actors.none { it.uuid == playerUUID }) {
                packet.side1
            } else {
                packet.side2
            }

            val otherSide = if (mySide == packet.side1) packet.side2 else packet.side1

            side1.actors.addAll(mySide.actors.map(this@BattleInitializeHandler::actorFromDTO))
            side2.actors.addAll(otherSide.actors.map(this@BattleInitializeHandler::actorFromDTO))
            spectating = sides.any { it.actors.any { it.uuid == MinecraftClient.getInstance().player?.uuid } }
            for (side in listOf(side1, side2)) {
                side.battle = this
                for (actor in side.actors) {
                    actor.side = side
                    for (pokemon in actor.activePokemon) {
                        pokemon.battlePokemon?.actor = actor
                    }
                }
            }
        }
    }

    fun actorFromDTO(actorDTO: BattleInitializePacket.BattleActorDTO): ClientBattleActor {
        return ClientBattleActor(
            showdownId = actorDTO.showdownId,
            displayName = actorDTO.displayName,
            uuid = actorDTO.uuid,
            type = actorDTO.type
        ).apply {
            activePokemon.addAll(actorDTO.activePokemon.map {
                ActiveClientBattlePokemon(this, it?.let {
                    ClientBattlePokemon(
                        uuid = it.uuid,
                        properties = it.properties,
                        displayName = it.displayName,
                        hpRatio = it.hpRatio,
                        status = it.status,
                        statChanges = it.statChanges,
                    )
                })
            })
        }
    }
}