package com.cablemc.pokemoncobbled.common.api.battles.model

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.PokemonCobbled.LOGGER
import com.cablemc.pokemoncobbled.common.PokemonCobbled.showdown
import com.cablemc.pokemoncobbled.common.api.battles.model.actor.BattleActor
import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import com.cablemc.pokemoncobbled.common.battles.ActiveBattlePokemon
import com.cablemc.pokemoncobbled.common.battles.BattleFormat
import com.cablemc.pokemoncobbled.common.battles.BattleRegistry
import com.cablemc.pokemoncobbled.common.battles.BattleSide
import com.cablemc.pokemoncobbled.common.battles.dispatch.BattleDispatch
import com.cablemc.pokemoncobbled.common.battles.dispatch.DispatchResult
import com.cablemc.pokemoncobbled.common.battles.dispatch.GO
import com.cablemc.pokemoncobbled.common.net.messages.client.battle.BattleEndPacket
import com.cablemc.pokemoncobbled.common.util.DataKeys
import com.cablemc.pokemoncobbled.common.util.getPlayer
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.minecraft.text.Text
import java.util.UUID
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Individual battle instance
 *
 * @since January 16th, 2022
 * @author Deltric, Hiroku
 */
class PokemonBattle(
    val format: BattleFormat,
    val side1: BattleSide,
    val side2: BattleSide
) {
    /** Whether or not logging will be silenced for this battle. */
    var mute = false
    init {
        side1.battle = this
        side2.battle = this
    }

    val sides: Iterable<BattleSide>
        get() = listOf(side1, side2)
    val actors: Iterable<BattleActor>
        get() = sides.flatMap { it.actors.toList() }
    val activePokemon: Iterable<ActiveBattlePokemon>
        get() = actors.flatMap { it.activePokemon }
    val playerUUIDs: Iterable<UUID>
        get() = actors.flatMap { it.getPlayerUUIDs() }
    val players = playerUUIDs.mapNotNull { it.getPlayer() }
    val spectators = mutableListOf<UUID>()

    val battleId = UUID.randomUUID()

    val showdownMessages = mutableListOf<String>()
    var started = false
    // TEMP battle showcase stuff
    var announcingRules = false

    var dispatchResult = GO
    val dispatches = ConcurrentLinkedQueue<BattleDispatch>()


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
        return actors.find { actor -> actor.uuid == actorId }
    }

    /**
     * Gets a BattleActor and an [ActiveBattlePokemon] from a pnx key, e.g. p2a
     *
     * Returns null if either the pn or x is invalid.
     */
    fun getActorAndActiveSlotFromPNX(pnx: String): Pair<BattleActor, ActiveBattlePokemon> {
        val actor = actors.find { it.showdownId == pnx.substring(0, 2) }
            ?: throw IllegalStateException("Invalid pnx: $pnx - unknown actor")
        val letter = pnx[2]
        val pokemon = actor.getSide().activePokemon.find { it.getLetter() == letter }
            ?: throw IllegalStateException("Invalid pnx: $pnx - unknown pokemon")
        return actor to pokemon
    }

    fun broadcastChatMessage(component: Text) {
        return actors.forEach { it.sendMessage(component) }
    }

    fun writeShowdownAction(vararg messages: String) {
        val jsonArray = JsonArray()
        for (message in messages) {
            jsonArray.add(message)
        }
        val request = JsonObject()
        request.addProperty(DataKeys.REQUEST_TYPE, DataKeys.REQUEST_BATTLE_SEND_MESSAGE)
        request.addProperty(DataKeys.REQUEST_BATTLE_ID, battleId.toString())
        request.add(DataKeys.REQUEST_MESSAGES, jsonArray)
        val json = BattleRegistry.gson.toJson(request)
        log(json)
        showdown.write(json)
    }

    fun turn() {
        actors.forEach { it.turn() }
        for (side in sides) {
            val opposite = side.getOppositeSide()
            side.activePokemon.forEach {
                val battlePokemon = it.battlePokemon ?: return@forEach
                battlePokemon.facedOpponents.addAll(opposite.activePokemon.mapNotNull { it.battlePokemon })
            }
        }
    }

    fun end() {
        for (actor in actors) {
            for (pokemon in actor.pokemonList.filter { it.health > 0 }) {
                if (pokemon.facedOpponents.isNotEmpty() /* TODO exp share held item check */) {
                    val experience = PokemonCobbled.experienceCalculator.calculate(pokemon)
                    if (experience > 0) {
                        actor.awardExperience(pokemon, experience)
                    }
                }
            }
        }
        sendUpdate(BattleEndPacket())
    }

    fun log(message: String = "") {
        if (!mute) {
            LOGGER.info(message)
        }
    }

    fun sendUpdate(packet: NetworkPacket) {
        actors.forEach { it.sendUpdate(packet) }
        sendSpectatorUpdate(packet)
    }

    fun sendToActors(packet: NetworkPacket) {
        CobbledNetwork.sendToPlayers(actors.flatMap { it.getPlayerUUIDs().mapNotNull { it.getPlayer() } }, packet)
    }

    fun sendSplitUpdate(privateActor: BattleActor, publicPacket: NetworkPacket, privatePacket: NetworkPacket) {
        actors.forEach {  it.sendUpdate(if (it == privateActor) privatePacket else publicPacket) }
        sendSpectatorUpdate(publicPacket)
    }

    fun sendSpectatorUpdate(packet: NetworkPacket) {
        CobbledNetwork.sendToPlayers(spectators.mapNotNull { it.getPlayer() }, packet)
    }

    fun dispatch(dispatcher: () -> DispatchResult) {
        dispatches.add(BattleDispatch { dispatcher() })
    }

    fun dispatch(dispatcher: BattleDispatch) {
        dispatches.add(dispatcher)
    }

    fun tick() {
        while (dispatchResult.canProceed()) {
            val dispatch = dispatches.poll() ?: break
            dispatchResult = dispatch(this)
        }
    }
}