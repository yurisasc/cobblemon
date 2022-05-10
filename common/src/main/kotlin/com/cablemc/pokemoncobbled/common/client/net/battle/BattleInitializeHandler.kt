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
        PokemonCobbledClient.battle = ClientBattle(
            packet.battleId,
            packet.battleFormat
        ).apply {
            side1.actors.addAll(packet.side1.actors.map(this@BattleInitializeHandler::actorFromDTO))
            side2.actors.addAll(packet.side2.actors.map(this@BattleInitializeHandler::actorFromDTO))
            spectating = sides.any { it.actors.any { it.uuid == MinecraftClient.getInstance().player?.uuid } }
        }
    }

    fun actorFromDTO(actorDTO: BattleInitializePacket.BattleActorDTO): ClientBattleActor {
        return ClientBattleActor(
            showdownId = actorDTO.showdownId,
            displayName = actorDTO.displayName,
            uuid = actorDTO.uuid
        ).apply {
            activePokemon.addAll(actorDTO.activePokemon.map {
                ActiveClientBattlePokemon(it?.let {
                    ClientBattlePokemon(
                        uuid = it.uuid,
                        species = it.species,
                        form = it.form,
                        level = it.level,
                        displayName = it.displayName,
                        gender = it.gender,
                        hpRatio = it.hpRatio,
                        status = it.status,
                        statChanges = it.statChanges
                    )
                })
            })
        }
    }
}