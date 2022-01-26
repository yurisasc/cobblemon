package com.cablemc.pokemoncobbled.common.api.battles.model

import com.cablemc.pokemoncobbled.common.api.battles.model.actor.BattleActor
import com.cablemc.pokemoncobbled.common.battles.BattleRegistry
import com.cablemc.pokemoncobbled.common.battles.actor.PlayerBattleActor
import com.cablemc.pokemoncobbled.common.util.DataKeys
import com.cablemc.pokemoncobbled.mod.PokemonCobbledMod
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.minecraft.Util
import net.minecraft.network.chat.TextComponent
import java.util.*

/**
 * Individual battle instance
 *
 * @since January 16th, 2022
 * @author Deltric
 */
class Battle(
    val actors: List<BattleActor>
) {

    val battleId: UUID = UUID.randomUUID()
    val format: String = "gen7ou"

    // TEMP battle showcase stuff
    var announcingRules = false

    /**
     * Gets an actor by their showdown id
     * @return the actor if found otherwise null
     */
    fun getActor(showdownId: String) : BattleActor? {
        return actors.find { actor -> actor.showdownId == showdownId }
    }

    /**
     * Gets an actor by their game id
     * @return the actor if found otherwise null
     */
    fun getActor(actorId: UUID) : BattleActor? {
        return actors.find { actor -> actor.gameId == actorId }
    }

    fun broadcastChatMessage(component: TextComponent) {
        return actors.forEach {
            if(it is PlayerBattleActor) {
                val player = it.getPlayerEntity() ?: return@forEach
                player.sendMessage(component, Util.NIL_UUID)
            }
        }
    }

    fun writeShowdownAction(vararg messages: String) {
        val jsonArray = JsonArray()
        for(message in messages) {
            jsonArray.add(message)
        }
        val request = JsonObject()
        request.addProperty(DataKeys.REQUEST_TYPE, DataKeys.REQUEST_BATTLE_SEND_MESSAGE)
        request.addProperty(DataKeys.REQUEST_BATTLE_ID, battleId.toString())
        request.add(DataKeys.REQUEST_MESSAGES, jsonArray)
        PokemonCobbledMod.showdown.write(BattleRegistry.gson.toJson(request))
    }
}