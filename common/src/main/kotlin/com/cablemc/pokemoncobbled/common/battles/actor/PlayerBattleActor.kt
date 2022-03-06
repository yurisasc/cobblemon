package com.cablemc.pokemoncobbled.common.battles.actor

import com.cablemc.pokemoncobbled.common.api.battles.model.actor.BattleActor
import com.cablemc.pokemoncobbled.common.battles.pokemon.BattlePokemon
import com.cablemc.pokemoncobbled.common.util.getServer
import com.cablemc.pokemoncobbled.common.util.sendServerMessage
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerPlayer
import java.util.UUID

class PlayerBattleActor(
    showdownId: String,
    gameId: UUID,
    pokemonList: List<BattlePokemon>
) : BattleActor(showdownId, gameId, pokemonList) {

    // TEMP battle showcase stuff
    var announcingPokemon = false

    fun getPlayerEntity() : ServerPlayer? {
        return getServer()!!.playerList.getPlayer(gameId)
    }

    override fun sendMessage(component: Component) {
        getPlayerEntity()?.sendServerMessage(component)
    }

    override fun getName() : MutableComponent{
        return getPlayerEntity()!!.name.copy()
    }
}