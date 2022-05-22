package com.cablemc.pokemoncobbled.common.battles.actor

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.api.battles.model.actor.BattleActor
import com.cablemc.pokemoncobbled.common.api.net.NetworkPacket
import com.cablemc.pokemoncobbled.common.api.text.aqua
import com.cablemc.pokemoncobbled.common.api.text.bold
import com.cablemc.pokemoncobbled.common.api.text.gold
import com.cablemc.pokemoncobbled.common.api.text.gray
import com.cablemc.pokemoncobbled.common.api.text.green
import com.cablemc.pokemoncobbled.common.api.text.onClick
import com.cablemc.pokemoncobbled.common.api.text.onHover
import com.cablemc.pokemoncobbled.common.api.text.plus
import com.cablemc.pokemoncobbled.common.api.text.red
import com.cablemc.pokemoncobbled.common.api.text.strikethrough
import com.cablemc.pokemoncobbled.common.api.text.sum
import com.cablemc.pokemoncobbled.common.api.text.yellow
import com.cablemc.pokemoncobbled.common.battles.ActiveBattlePokemon
import com.cablemc.pokemoncobbled.common.battles.pokemon.BattlePokemon
import com.cablemc.pokemoncobbled.common.util.*
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicBoolean

class PlayerBattleActor(
    uuid: UUID,
    pokemonList: List<BattlePokemon>
) : BattleActor(uuid, pokemonList) {

    // TEMP battle showcase stuff
    var announcedPokemon = false

    fun getPlayerEntity() = getServer()!!.playerManager.getPlayer(uuid)
    override fun sendMessage(component: Text) = getPlayerEntity()?.sendServerMessage(component) ?: Unit
    override fun getName(): MutableText = getPlayerEntity()!!.name.copy()

    override fun getPlayerUUIDs() = setOf(uuid)
    override fun awardExperience(battlePokemon: BattlePokemon, experience: Int) {
        if (battlePokemon.effectedPokemon == battlePokemon.originalPokemon && experience > 0) {
            uuid.getPlayer()
                ?.let { battlePokemon.effectedPokemon.addExperienceWithPlayer(it, experience) }
                ?: run { battlePokemon.effectedPokemon.addExperience(experience) }
        }
    }

    override fun getChoices(activePokemon: Iterable<ActiveBattlePokemon>): CompletableFuture<Iterable<String>> {
        sendMessage(">> ".gold() + battleLang("choose_actions").gold().bold())
        val choices = mutableListOf<String>()
        val future = CompletableFuture<Iterable<String>>()
        getMoveChoices(future, activePokemon.toMutableList(), choices)
        return future
    }

    fun getMoveChoices(allFuture: CompletableFuture<Iterable<String>>, list: MutableList<ActiveBattlePokemon>, madeChoices: MutableList<String>) {
        val first = list.first()
        list.removeAt(0)
        val future = getMoveChoice(first)
        future.thenAccept {
            madeChoices.add(it)
            if (list.isEmpty()) {
                allFuture.complete(madeChoices)
            } else {
                getMoveChoices(allFuture, list, madeChoices)
            }
        }
    }

    /**
     * Returned thing is the move and the target. If it's multi-target, no target needed
     */
    fun getMoveChoice(activeBattlePokemon: ActiveBattlePokemon): CompletableFuture<String> {
        val canChoose = AtomicBoolean(false)
        val future = CompletableFuture<String>()
        val actor = activeBattlePokemon.actor
        actor.sendMessage(activeBattlePokemon.battlePokemon!!.getName().gold() + ":")
        activeBattlePokemon.selectableMoves.forEachIndexed { index, move ->
            val moveIndex = index + 1
            if (move.disabled) {
                actor.sendMessage("- ".yellow() + move.move.asTranslated().aqua().strikethrough().onHover("Disabled"))
            } else if (move.pp == 0) {
                actor.sendMessage("- ".red() + move.move.asTranslated().red().onHover("No PP"))
            } else {
                actor.sendMessage("- ${move.move}".aqua().onClick(canChoose) {
                    val possibleTargets = move.getTargets(activeBattlePokemon)?.filter { it.battlePokemon != null }
                    if (possibleTargets == null || possibleTargets.isEmpty()) {
                        future.complete("move $moveIndex")
                    } else if (possibleTargets.size == 1) {
                        future.complete("move $moveIndex ${possibleTargets.first().getSignedDigitRelativeTo(activeBattlePokemon)}")
                    } else {
                        actor.sendMessage("Choose a target: ".gold())
                        val canChooseTarget = AtomicBoolean()
                        for (target in possibleTargets) {
                            val coloured: (MutableText) -> MutableText = {
                                if (target.isAllied(activeBattlePokemon)) {
                                    it.green()
                                } else {
                                    it.aqua()
                                }
                            }
                            actor.sendMessage("- ".gold() + coloured(target.battlePokemon!!.getName()).onClick(canChooseTarget) {
                                future.complete("move $moveIndex ${target.getSignedDigitRelativeTo(activeBattlePokemon)}")
                            })
                        }
                    }
                })
            }
        }

        val switchLabels = mutableListOf<MutableText>()
        for ((index, battlePokemon) in actor.pokemonList.withIndex()) {
            val pokemonIndex = index + 1
            val canSwitchToIt = battlePokemon.canBeSentOut()
            if (canSwitchToIt) {
                switchLabels.add(
                    "$pokemonIndex"
                        .gold()
                        .onHover(battlePokemon.effectedPokemon.species.translatedName + " ${battlePokemon.health}/${battlePokemon.maxHealth}")
                        .onClick(canChoose) {
                        battlePokemon.willBeSwitchedIn = true
                        future.complete("switch ${battlePokemon.uuid}")
                    }
                )
            } else {
                switchLabels.add("$pokemonIndex".gray().onHover(battlePokemon.effectedPokemon.species.translatedName + " ${battlePokemon.health}/${battlePokemon.maxHealth}"))
            }
        }

        actor.sendMessage(battleLang("switch_option").gold() + ": ".gold() + "[".gray() + switchLabels.sum(", ".gray()) + "]".gray())
        return future
    }

    override fun getSwitch(activePokemon: Iterable<ActiveBattlePokemon>): CompletableFuture<Iterable<UUID>> {
        val switches = mutableListOf<UUID>()
        val future = CompletableFuture<Iterable<UUID>>()
        getSwitchChoices(future, activePokemon.toMutableList(), switches)
        return future
    }

    fun getSwitchChoices(allFuture: CompletableFuture<Iterable<UUID>>, list: MutableList<ActiveBattlePokemon>, madeSwitches: MutableList<UUID>) {
        val first = list.first()
        list.removeAt(0)
        val future = getSwitchChoice(first)
        future.thenApply {
            madeSwitches.add(it)
            if (list.isEmpty()) {
                allFuture.complete(madeSwitches)
            } else {
                getSwitchChoices(allFuture, list, madeSwitches)
            }
        }.exceptionally {
            allFuture.complete(madeSwitches)
        }
    }

    fun getSwitchChoice(activeBattlePokemon: ActiveBattlePokemon): CompletableFuture<UUID> {
        val canChoose = AtomicBoolean(false)
        val future = CompletableFuture<UUID>()
        val actor = activeBattlePokemon.actor
        actor.sendMessage(battleLang("must_switch"))
        val switchLabels = mutableListOf<MutableText>()
        for ((index, battlePokemon) in actor.pokemonList.withIndex()) {
            val pokemonIndex = index + 1
            val canSwitchToIt = battlePokemon.canBeSentOut()
            if (canSwitchToIt) {
                switchLabels.add(
                    "$pokemonIndex"
                        .gold()
                        .onHover(battlePokemon.effectedPokemon.species.translatedName + " ${battlePokemon.health}/${battlePokemon.maxHealth}")
                        .onClick(canChoose) {
                        battlePokemon.willBeSwitchedIn = true
                        future.complete(battlePokemon.uuid)
                    }
                )
            } else {
                switchLabels.add("$pokemonIndex".gray().onHover(battlePokemon.effectedPokemon.species.translatedName + " ${battlePokemon.health}/${battlePokemon.maxHealth}"))
            }
        }

        if (switchLabels.isEmpty()) {
            return CompletableFuture.failedFuture(Exception())
        }

        actor.sendMessage(battleLang("switch_option").gold() + ": ".gold() + "[".gray() + switchLabels.sum(", ".gray()) + "]".gray())
        return future
    }

    override fun sendUpdate(packet: NetworkPacket) {
        CobbledNetwork.sendToPlayers(getPlayerUUIDs().mapNotNull { it.getPlayer() }, packet)
    }
}