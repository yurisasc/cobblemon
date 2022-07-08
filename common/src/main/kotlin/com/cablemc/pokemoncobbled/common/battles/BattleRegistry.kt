package com.cablemc.pokemoncobbled.common.battles

import com.cablemc.pokemoncobbled.common.PokemonCobbled.showdown
import com.cablemc.pokemoncobbled.common.api.battles.model.PokemonBattle
import com.cablemc.pokemoncobbled.common.api.battles.model.actor.ActorType
import com.cablemc.pokemoncobbled.common.battles.pokemon.BattlePokemon
import com.cablemc.pokemoncobbled.common.util.DataKeys
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import java.util.Optional
import net.minecraft.server.network.ServerPlayerEntity
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object BattleRegistry {

    val gson = GsonBuilder().disableHtmlEscaping().create()
    private val battleMap = ConcurrentHashMap<UUID, PokemonBattle>()

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
    fun startBattle(
        battleFormat: BattleFormat,
        side1: BattleSide,
        side2: BattleSide
    ): PokemonBattle {
        val battle = PokemonBattle(battleFormat, side1, side2)
        battleMap[battle.battleId] = battle

        // Build request message
        val jsonArray = JsonArray()
        jsonArray.add(">start { \"format\": ${battleFormat.toFormatJSON()} }")

        /*
         * Showdown IDs are like p1, p2, p3, etc. Showdown uses these keys to identify who is doing what to whom.
         *
         * "But why are these showdown IDs so weird"
         *
         * I'll tell you, Jimmy.
         * https://gitlab.com/cable-mc/pokemon-cobbled-showdown/-/blob/master/sim/SIM-PROTOCOL.md#user-content-identifying-pok%C3%A9mon
         *
         * See the lines about multi battles and free for alls. The same side of the battle will share 'parity' (even or odd) across
         * all participants. So side 1 will be 1, 3, 5, ... while side 2 will be 2, 4, 6, ...
         *
         * That isn't how our code works, as we have the BattleSide thing, but it's how Showdown works so we need to play along a bit.
         */

        var actorIndex = 1
        for (actor in battle.side1.actors) {
            actor.showdownId = "p$actorIndex"
            actor.battle = battle
            actorIndex += 2
        }

        actorIndex = 2
        for (actor in battle.side2.actors) {
            actor.showdownId = "p$actorIndex"
            actor.battle = battle
            actorIndex += 2
        }

        for (actor in battle.actors) {
            repeat(battleFormat.battleType.slotsPerActor) {
                actor.activePokemon.add(ActiveBattlePokemon(actor))
            }
            val entities = actor.pokemonList.mapNotNull { it.entity }
            entities.forEach { it.battleId.set(Optional.of(battle.battleId)) }
        }

        // -> Add the players and team
        for (actor in battle.actors.sortedBy { it.showdownId }) {
            jsonArray.add(""">player ${actor.showdownId} {"name":"${actor.uuid}","team":"${actor.pokemonList.packTeam()}"}""")
        }

        // -> Set team size
        for (actor in battle.actors.sortedBy { it.showdownId }) {
            jsonArray.add(">${actor.showdownId} team ${actor.pokemonList.count()}")
        }

        // Compiles the request and sends it off
        val request = JsonObject()
        request.addProperty(DataKeys.REQUEST_TYPE, DataKeys.REQUEST_BATTLE_START)
        request.addProperty(DataKeys.REQUEST_BATTLE_ID, battle.battleId.toString())
        request.add(DataKeys.REQUEST_MESSAGES, jsonArray)
        showdown.write(gson.toJson(request))

        return battle
    }

    fun closeBattle(battle: PokemonBattle) {
        battleMap.remove(battle.battleId)
    }

    fun getBattle(id: UUID) : PokemonBattle? {
        return battleMap[id]
    }

    fun getBattleByParticipatingPlayer(serverPlayerEntity: ServerPlayerEntity) : PokemonBattle? {
        return battleMap.values.find { it.actors.any { it.isForPlayer(serverPlayerEntity) } }
    }

    fun tick() {
        battleMap.forEachValue(Long.MAX_VALUE) { it.tick() }
    }
}