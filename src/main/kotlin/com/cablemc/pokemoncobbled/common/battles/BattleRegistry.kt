package com.cablemc.pokemoncobbled.common.battles

import com.cablemc.pokemoncobbled.common.api.battles.model.Battle
import com.cablemc.pokemoncobbled.common.api.battles.model.actor.BattleActor
import com.cablemc.pokemoncobbled.common.util.DataKeys
import com.cablemc.pokemoncobbled.mod.PokemonCobbledMod
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.minecraft.server.level.ServerPlayer
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object BattleRegistry {

    val gson: Gson = GsonBuilder().disableHtmlEscaping().create()
    private val battleMap: ConcurrentHashMap<UUID, Battle> = ConcurrentHashMap()

    /**
     * Temporary starting method for a battle.
     * TODO: Replace with a builder for battle definition and then a starting method that takes the built result?
     */
    fun startBattle(vararg battleActors: BattleActor) {
        val battle = Battle(battleActors.asList())
        battleMap[battle.battleId] = battle

        // Build request message
        val jsonArray = JsonArray()
        jsonArray.add(""">start {"formatid":"${battle.format}"}""")

        // -> Add the players and team
        for(actor in battle.actors) {
            jsonArray.add(""">player ${actor.showdownId} {"name":"${actor.gameId}","team":"${actor.party.packTeam()}"}""")
        }

        // -> Set team size
        for(actor in battle.actors) {
            jsonArray.add(""">${actor.showdownId} team ${actor.party.count()}""")
        }

        // Compiles the request and sends it off
        val request = JsonObject()
        request.addProperty(DataKeys.REQUEST_TYPE, DataKeys.REQUEST_BATTLE_START)
        request.addProperty(DataKeys.REQUEST_BATTLE_ID, battle.battleId.toString())
        request.add(DataKeys.REQUEST_MESSAGES, jsonArray)
        PokemonCobbledMod.showdown.write(gson.toJson(request))
    }

    fun closeBattle(battle: Battle) {
        battleMap.remove(battle.battleId)
    }

    fun getBattle(id: UUID) : Battle? {
        return battleMap[id]
    }

    fun getBattleByParticipatingPlayer(serverPlayer: ServerPlayer) : Battle? {
        for(entry in battleMap.entries) {
            val found = entry.value.actors.find {
                it.gameId.equals(serverPlayer.uuid)
            }
            if(found != null)
                return entry.value
        }
        return null
    }

}