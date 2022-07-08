package com.cablemc.pokemoncobbled.common.client.net.battle

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.client.PokemonCobbledClient
import com.cablemc.pokemoncobbled.common.client.battle.ActiveClientBattlePokemon
import com.cablemc.pokemoncobbled.common.client.battle.ClientBattle
import com.cablemc.pokemoncobbled.common.client.battle.ClientBattleActor
import com.cablemc.pokemoncobbled.common.client.battle.ClientBattlePokemon
import com.cablemc.pokemoncobbled.common.net.PacketHandler
import com.cablemc.pokemoncobbled.common.net.messages.client.battle.BattleInitializePacket
import net.minecraft.client.MinecraftClient

object BattleInitializeHandler : PacketHandler<BattleInitializePacket> {
    override fun invoke(packet: BattleInitializePacket, ctx: CobbledNetwork.NetworkContext) {
        val playerUUID = MinecraftClient.getInstance().player?.uuid
        PokemonCobbledClient.battle = ClientBattle(
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