package com.cablemc.pokemoncobbled.common.battles

import com.cablemc.pokemoncobbled.common.PokemonCobbled.showdown
import com.cablemc.pokemoncobbled.common.api.battles.model.PokemonBattle
import com.cablemc.pokemoncobbled.common.api.battles.model.actor.BattleActor
import com.cablemc.pokemoncobbled.common.battles.pokemon.BattlePokemon
import com.cablemc.pokemoncobbled.common.util.DataKeys
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.minecraft.server.level.ServerPlayer
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object BattleRegistry {

    val gson = GsonBuilder().disableHtmlEscaping().create()
    private val battleMap: ConcurrentHashMap<UUID, PokemonBattle> = ConcurrentHashMap()

    /**
     * Packs a team into the showdown format
     * @return a string of the packed team
     */
    fun List<BattlePokemon>.packTeam() : String {
        val team = mutableListOf<String>()
        for (pokemon in this) {
            val pk = pokemon.effectedPokemon
            val packedTeamBuilder = StringBuilder()
            // If no nickname, write species first and leave next blank
            packedTeamBuilder.append("${pk.species.name}|")
            // Species, left empty if no nickname
            packedTeamBuilder.append("|")

            // REQUIRES OUR SHOWDOWN
            packedTeamBuilder.append("${pk.uuid}|")

            // Held item, empty if non TODO: Replace with actual held item
            packedTeamBuilder.append("|")
            // Ability
            packedTeamBuilder.append("${pk.ability.name.replace("_", "")}|")
            // Moves
            packedTeamBuilder.append(
                "${
                    pk.moveSet.getMoves().joinToString(",") { move -> move.name.replace("_", "") }
                }|"
            )
            // Nature
            packedTeamBuilder.append("${pk.nature.name.path}|")
            // EVs
            packedTeamBuilder.append("${pk.evs.map { ev -> ev.value }.joinToString(",")}|")
            // Gender TODO: Replace with actual gender variable
            packedTeamBuilder.append("M|")
            // IVs
            packedTeamBuilder.append("${pk.ivs.map { iv -> iv.value }.joinToString(",")}|")
            // Shiny
            packedTeamBuilder.append("${if (pk.shiny) "S" else ""}|")
            // Level
            packedTeamBuilder.append("${pk.level}|")
            // Happiness
            packedTeamBuilder.append("${pk.friendship}|")
            // Caught Ball TODO: Replace with actual pokeball variable
            packedTeamBuilder.append("|")
            // Hidden Power Type
            packedTeamBuilder.append("|")

            team.add(packedTeamBuilder.toString())
        }
        return team.joinToString("]")
    }

    /**
     * Temporary starting method for a battle.
     * TODO: Replace with a builder for battle definition and then a starting method that takes the built result?
     */
    fun startBattle(vararg battleActors: BattleActor) {
        val battle = PokemonBattle(battleActors.asList())
        battleMap[battle.battleId] = battle

        // Build request message
        val jsonArray = JsonArray()
        jsonArray.add(""">start { "gameType": "doubles", "formatid":"${battle.format}"}""")

        // -> Add the players and team
        for (actor in battle.actors) {
            jsonArray.add(""">player ${actor.showdownId} {"name":"${actor.gameId}","team":"${actor.pokemonList.packTeam()}"}""")
        }

        // -> Set team size
        for (actor in battle.actors) {
            jsonArray.add(""">${actor.showdownId} team ${actor.pokemonList.count()}""")
        }

        // Compiles the request and sends it off
        val request = JsonObject()
        request.addProperty(DataKeys.REQUEST_TYPE, DataKeys.REQUEST_BATTLE_START)
        request.addProperty(DataKeys.REQUEST_BATTLE_ID, battle.battleId.toString())
        request.add(DataKeys.REQUEST_MESSAGES, jsonArray)
        showdown.write(gson.toJson(request))
    }

    fun closeBattle(battle: PokemonBattle) {
        battleMap.remove(battle.battleId)
    }

    fun getBattle(id: UUID) : PokemonBattle? {
        return battleMap[id]
    }

    fun getBattleByParticipatingPlayer(serverPlayer: ServerPlayer) : PokemonBattle? {
        for (entry in battleMap.entries) {
            val found = entry.value.actors.find {
                it.gameId.equals(serverPlayer.uuid)
            }
            if (found != null) {
                return entry.value
            }
        }
        return null
    }

}