package com.cablemc.pokemoncobbled.common.battles.actor

import com.cablemc.pokemoncobbled.common.api.battles.model.actor.BattleActor
import com.cablemc.pokemoncobbled.common.api.storage.party.PartyStore
import com.cablemc.pokemoncobbled.common.util.getServer
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
        return getServer().playerList.getPlayer(gameId)
    }

    fun sendMessage(message: Component) {
        getPlayerEntity()!!.sendMessage(message, Util.NIL_UUID)
    }

}