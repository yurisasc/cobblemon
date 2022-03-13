package com.cablemc.pokemoncobbled.common.battles.actor

import com.cablemc.pokemoncobbled.common.api.battles.model.actor.BattleActor
import com.cablemc.pokemoncobbled.common.api.storage.party.PartyStore
import com.cablemc.pokemoncobbled.common.util.getServer
import com.cablemc.pokemoncobbled.common.util.sendServerMessage
import net.minecraft.Util
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import java.util.UUID

class PlayerBattleActor(
    showdownId: String,
    gameId: UUID,
    party: PartyStore
) : BattleActor(showdownId, gameId, party) {

    // TEMP battle showcase stuff
    var announcingPokemon = false

    fun getPlayerEntity() : ServerPlayer? {
        return getServer()!!.playerList.getPlayer(gameId)
    }

    override fun sendMessage(message: Component) {
        getPlayerEntity()?.sendServerMessage(message)
    }

    override fun getName() : String {
        return getPlayerEntity()!!.name.string
    }
}