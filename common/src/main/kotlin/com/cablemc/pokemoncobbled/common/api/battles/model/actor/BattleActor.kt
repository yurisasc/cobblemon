package com.cablemc.pokemoncobbled.common.api.battles.model.actor

import com.cablemc.pokemoncobbled.common.api.battles.model.PokemonBattle
import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import com.cablemc.pokemoncobbled.common.battles.ActiveBattlePokemon
import com.cablemc.pokemoncobbled.common.battles.ShowdownActionRequest
import com.cablemc.pokemoncobbled.common.battles.ShowdownActionResponse
import com.cablemc.pokemoncobbled.common.battles.pokemon.BattlePokemon
import com.cablemc.pokemoncobbled.common.exception.IllegalActionChoiceException
import com.cablemc.pokemoncobbled.common.net.messages.client.battle.BattleMakeChoicePacket
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import java.util.UUID

abstract class BattleActor(
    val uuid: UUID,
    val pokemonList: List<BattlePokemon>
) {
    init {
        pokemonList.forEach { it.actor = this }
    }

    lateinit var showdownId: String
    lateinit var battle: PokemonBattle

    val activePokemon = mutableListOf<ActiveBattlePokemon>()
    var canDynamax = false

    var request: ShowdownActionRequest? = null
    var responses = mutableListOf<ShowdownActionResponse>()
    var mustChoose = false

    fun getSide() = if (this in battle.side1.actors) battle.side1 else battle.side2
    open fun getPlayerUUIDs(): Iterable<UUID> = emptyList()

    fun turn() {
        val request = request ?: return
        responses.clear()
        mustChoose = true
        sendUpdate(BattleMakeChoicePacket())

        val requestActive = request.active
        if (requestActive == null || requestActive.isEmpty() || request.wait) {
            this.request = null
            return
        }
//        val toChoose = mutableListOf<ActiveBattlePokemon>()
//        for ((activeIndex, active) in requestActive.withIndex()) {
//            val pokemon = activePokemon[activeIndex]
//            pokemon.selectableMoves = active.moves
//            toChoose.add(pokemon)
//        }
//
//        if (toChoose.isNotEmpty()) {
//            getChoices(toChoose).thenAccept {
//                val switches = it.filter { it.startsWith("switch ") }
//                switches.forEach {
//                    val uuid = UUID.fromString(it.substring(7))
//                    pokemonList.find { it.uuid == uuid }?.willBeSwitchedIn = false
//                }
//
//                val joinedChoices = it.joinToString()
//                this.request = null
//                battle.writeShowdownAction(">$showdownId $joinedChoices")
//            }
//        }
    }

    fun upkeep() {
        val request = request ?: return
        val forceSwitchPokemon = request.forceSwitch.mapIndexedNotNull { index, b -> if (b) activePokemon[index] else null }
        if (forceSwitchPokemon.isEmpty()) {
            return
        }

        sendUpdate(BattleMakeChoicePacket())
        mustChoose = true

//        getSwitch(forceSwitchPokemon).thenApply { uuids ->
//            var switchRequests = uuids.joinToString { "switch $it" }
//            repeat(times = forceSwitchPokemon.size - uuids.count()) {
//                switchRequests += ",pass"
//            }
//            pokemonList.filter { it.uuid in uuids }.forEach { it.willBeSwitchedIn = false }
//            battle.writeShowdownAction(">$showdownId $switchRequests")
//        }
    }

    fun setActionResponses(responses: List<ShowdownActionResponse>) {
        val request = request ?: return
        responses.forEachIndexed { index, response ->
            val activeBattlePokemon = activePokemon.let { if (it.size > index) it[index] else return }
            val showdownMoveSet = request.active?.let { if (it.size > index) it[index] else null }
            val forceSwitch = request.forceSwitch.let { if (it.size > index) it[index] else false }
            if (!response.isValid(activeBattlePokemon, showdownMoveSet, forceSwitch)) {
                throw IllegalActionChoiceException(this, "Invalid action choice for ${activeBattlePokemon.battlePokemon!!.getName().asString()}: $response")
            }
            this.responses.add(response)
        }
        mustChoose = false

        battle.checkForInputDispatch()
    }

    fun writeShowdownResponse() {
        val showdownMessages = mutableListOf<String>()
        var index = 0
        request!!.iterate(activePokemon) { activeBattlePokemon, showdownMoveSet, _ ->
            showdownMessages.add(responses[index].toShowdownString(activeBattlePokemon, showdownMoveSet))
            index++
        }
        responses.clear()
        request = null
        battle.writeShowdownAction(">$showdownId ${showdownMessages.joinToString()}")
    }

    abstract fun getName(): MutableText
    open fun sendMessage(component: Text) {}
    open fun awardExperience(battlePokemon: BattlePokemon, experience: Int) {}
    open fun sendUpdate(packet: NetworkPacket) {}
}