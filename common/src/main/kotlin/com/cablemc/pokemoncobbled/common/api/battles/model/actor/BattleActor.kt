package com.cablemc.pokemoncobbled.common.api.battles.model.actor

import com.cablemc.pokemoncobbled.common.api.battles.model.PokemonBattle
import com.cablemc.pokemoncobbled.common.battles.ActiveBattlePokemon
import com.cablemc.pokemoncobbled.common.battles.ShowdownActionRequest
import com.cablemc.pokemoncobbled.common.battles.pokemon.BattlePokemon
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import java.util.UUID
import java.util.concurrent.CompletableFuture

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

    fun getSide() = if (this in battle.side1.actors) battle.side1 else battle.side2

    fun turn() {
        val request = request ?: return
        if (request.active == null || request.active.isEmpty() || request.wait) {
            this.request = null
            return
        }
        val toChoose = mutableListOf<ActiveBattlePokemon>()
        for ((activeIndex, active) in request.active.withIndex()) {
            val pokemon = activePokemon[activeIndex]
            pokemon.selectableMoves = active.moves
            toChoose.add(pokemon)
        }

        if (toChoose.isNotEmpty()) {
            getChoices(toChoose).thenAccept {
                val switches = it.filter { it.startsWith("switch ") }
                switches.forEach {
                    val uuid = UUID.fromString(it.substring(7))
                    pokemonList.find { it.uuid == uuid }?.willBeSwitchedIn = false
                }

                val joinedChoices = it.joinToString()
                this.request = null
                battle.writeShowdownAction(">$showdownId $joinedChoices")
            }
        }
    }

    fun upkeep() {
        val request = request ?: return
        val forceSwitchPokemon = request.forceSwitch.mapIndexedNotNull { index, b -> if (b) activePokemon[index] else null }
        if (forceSwitchPokemon.isEmpty()) {
            return
        }
        getSwitch(forceSwitchPokemon).thenApply { uuids ->
            var switchRequests = uuids.joinToString { "switch $it" }
            repeat(times = forceSwitchPokemon.size - uuids.count()) {
                switchRequests += ",pass"
            }
            pokemonList.filter { it.uuid in uuids }.forEach { it.willBeSwitchedIn = false }
            battle.writeShowdownAction(">$showdownId $switchRequests")
        }
    }

    abstract fun getName(): MutableComponent
    abstract fun getChoices(activePokemon: Iterable<ActiveBattlePokemon>): CompletableFuture<Iterable<String>>
    abstract fun getSwitch(activePokemon: Iterable<ActiveBattlePokemon>): CompletableFuture<Iterable<UUID>>
    open fun sendMessage(component: Component) {}
    open fun awardExperience(battlePokemon: BattlePokemon, experience: Int) {}
}