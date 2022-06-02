package com.cablemc.pokemoncobbled.common.api.battles.model.actor

import com.cablemc.pokemoncobbled.common.api.battles.model.ai.BattleAI
import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import com.cablemc.pokemoncobbled.common.battles.pokemon.BattlePokemon
import com.cablemc.pokemoncobbled.common.net.messages.client.battle.BattleMakeChoicePacket
import java.util.UUID

abstract class AIBattleActor(
    gameId: UUID,
    pokemonList: List<BattlePokemon>,
    val battleAI: BattleAI
) : BattleActor(gameId, pokemonList) {
    override fun sendUpdate(packet: NetworkPacket) {
        super.sendUpdate(packet)

        if (packet is BattleMakeChoicePacket) {
            setActionResponses(request!!.iterate(this, battleAI::choose) )
        }
    }
}