/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.battles

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.Cobblemon.LOGGER
import com.cobblemon.mod.common.CobblemonItems
import com.cobblemon.mod.common.api.battles.interpreter.*
import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor
import com.cobblemon.mod.common.api.battles.model.actor.EntityBackedBattleActor
import com.cobblemon.mod.common.api.data.ShowdownIdentifiable
import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.api.events.battles.BattleFaintedEvent
import com.cobblemon.mod.common.api.events.battles.BattleVictoryEvent
import com.cobblemon.mod.common.api.moves.Moves
import com.cobblemon.mod.common.api.pokemon.stats.Stats
import com.cobblemon.mod.common.api.pokemon.status.Statuses
import com.cobblemon.mod.common.api.text.aqua
import com.cobblemon.mod.common.api.text.gold
import com.cobblemon.mod.common.api.text.plus
import com.cobblemon.mod.common.api.text.red
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.battles.dispatch.BattleDispatch
import com.cobblemon.mod.common.battles.dispatch.DispatchResult
import com.cobblemon.mod.common.battles.dispatch.GO
import com.cobblemon.mod.common.battles.dispatch.UntilDispatch
import com.cobblemon.mod.common.battles.dispatch.WaitDispatch
import com.cobblemon.mod.common.battles.interpreter.ContextManager
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon
import com.cobblemon.mod.common.net.messages.client.battle.*
import com.cobblemon.mod.common.pokemon.evolution.progress.DamageTakenEvolutionProgress
import com.cobblemon.mod.common.pokemon.evolution.progress.RecoilEvolutionProgress
import com.cobblemon.mod.common.pokemon.evolution.progress.UseMoveEvolutionProgress
import com.cobblemon.mod.common.pokemon.status.PersistentStatus
import com.cobblemon.mod.common.util.asTranslated
import com.cobblemon.mod.common.util.battleLang
import com.cobblemon.mod.common.util.getPlayer
import com.cobblemon.mod.common.util.lang
import com.cobblemon.mod.common.util.runOnServer
import com.cobblemon.mod.common.util.swap
import java.util.UUID
import java.util.concurrent.CompletableFuture
import kotlin.math.roundToInt
import net.minecraft.entity.LivingEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text

object ShowdownInterpreter {
    private val updateInstructions = mutableMapOf<String, (PokemonBattle, String, MutableList<String>) -> Unit>()
    private val sideUpdateInstructions = mutableMapOf<String, (PokemonBattle, BattleActor, String) -> Unit>()
    private val splitUpdateInstructions = mutableMapOf<String, (PokemonBattle, BattleActor, String, String) -> Unit>()
    // Stores a reference to the previous ability, activate, or move message in a battle so a minor action can refer back to it (Battle UUID :  BattleMessage)
    private val lastCauser = mutableMapOf<UUID, BattleMessage>()

    init {
        // Note '-cureteam' is a legacy thing that is only used in generation 2 and 4 mods for heal bell and aromatherapy respectively as such we can just ignore that
        updateInstructions["|player|"] = this::handlePlayerInstruction
        updateInstructions["|teamsize|"] = this::handleTeamSizeInstruction
        updateInstructions["|gametype|"] = this::handleGameTypeInstruction
        updateInstructions["|gen|"] = this::handleGenInstruction
        updateInstructions["|tier|"] = this::handleTierInstruction
        updateInstructions["|rated"] = this::handleRatedInstruction
        updateInstructions["|rule|"] = this::handleRuleInstruction
        updateInstructions["|clearpoke"] = this::handleClearPokeInstruction
        updateInstructions["|poke|"] = this::handlePokeInstruction
        updateInstructions["|teampreview"] = this::handleTeamPreviewInstruction
        updateInstructions["|start"] = this::handleStartInstruction
        updateInstructions["|turn|"] = this::handleTurnInstruction
        updateInstructions["|upkeep"] = this::handleUpkeepInstruction
        updateInstructions["|faint|"] = this::handleFaintInstruction
        updateInstructions["|win|"] = this::handleWinInstruction
        updateInstructions["|move|"] = this::handleMoveInstruction
        updateInstructions["|cant|"] = this::handleCantInstruction
        updateInstructions["|-supereffective|"] = this::handleSuperEffectiveInstruction
        updateInstructions["|-resisted|"] = this::handleResistInstruction
        updateInstructions["|-crit"] = this::handleCritInstruction
        updateInstructions["|-weather|"] = this::handleWeatherInstruction
        updateInstructions["|-mustrecharge|"] = this::handleRechargeInstructions
        updateInstructions["|-fail|"] = this::handleFailInstruction
        updateInstructions["|-start|"] = this::handleStartInstructions
        updateInstructions["|-activate|"] = this::handleActivateInstructions
        updateInstructions["|-curestatus|"] = this::handleCureStatusInstruction
        updateInstructions["|-fieldstart|"] = this::handleFieldStartInstructions
        updateInstructions["|-fieldend|"] = this::handleFieldEndInstructions
        updateInstructions["|-ability|"] = this::handleAbilityInstructions
        //To-Do updateInstructions["|-endability|"] = this::handleEndAbilityInstruction
        updateInstructions["|-nothing"] = { battle, _, _ ->
            battle.dispatchGo { battle.broadcastChatMessage(battleLang("nothing")) }
        }
        updateInstructions["|-clearallboost"] = this::handleClearAllBoostInstructions
        updateInstructions["|-singleturn|"] = this::handleSingleTurnInstruction
        updateInstructions["|-singlemove|"] = this::handleSingleMoveInstruction
        updateInstructions["|-prepare|"] = this::handlePrepareInstruction
        updateInstructions["|-swapboost"] = this::handleSwapBoostInstruction
        updateInstructions["|-swapsideconditions|"] = this::handleSilently
        updateInstructions["|-unboost|"] = { battle, line, remainingLines -> boostInstruction(battle, line, remainingLines, false) }
        updateInstructions["|-boost|"] = { battle, line, remainingLines -> boostInstruction(battle, line, remainingLines, true) }
        updateInstructions["|-setboost|"] = this::handleSetBoostInstruction
        updateInstructions["|t:|"] = {_, _, _ -> }
        updateInstructions["|pp_update|"] = this::handlePpUpdateInstruction
        updateInstructions["|-immune"] = this::handleImmuneInstruction
        updateInstructions["|-status|"] = this::handleStatusInstruction
        updateInstructions["|-end|"] = this::handleEndInstruction
        updateInstructions["|-miss|"] = this::handleMissInstruction
        updateInstructions["|-hitcount|"] = this::handleHitCountInstruction
        updateInstructions["|-item|"] = this::handleItemInstruction
        updateInstructions["|-enditem|"] = this::handleEndItemInstruction
        updateInstructions["|-sidestart|"] = this::handleSideStartInstructions
        updateInstructions["|-sideend|"] = this::handleSideEndInstructions
        updateInstructions["|-fieldactivate|"] = this::handleFieldActivateInstructions

        sideUpdateInstructions["|request|"] = this::handleRequestInstruction
        splitUpdateInstructions["|switch|"] = this::handleSwitchInstruction
        splitUpdateInstructions["|-damage|"] = this::handleDamageInstruction
        splitUpdateInstructions["|drag|"] = this::handleDragInstruction
        splitUpdateInstructions["|-heal|"] = this::handleHealInstruction
        splitUpdateInstructions["|-sethp|"] = this::handleSetHpInstructions
        sideUpdateInstructions["|error|"] = this::handleErrorInstructions
    }

    private fun boostInstruction(battle: PokemonBattle, rawMessage: String, remainingLines: MutableList<String>, isBoost: Boolean) {
        val message = BattleMessage(rawMessage)
        val pokemon = message.getBattlePokemon(0, battle) ?: return
        val statKey = message.argumentAt(1) ?: return
        val stages = message.argumentAt(2)?.toInt() ?: return
        val stat = getStat(statKey).displayName
        val severity = getSeverity(stages)
        val rootKey = if (isBoost) "boost" else "unboost"

        if (stages == 0) {
            val othersExist = remainingLines.removeIf {
                val isAlsoBoost = it.startsWith(if (isBoost) "|-boost" else "|-unboost")
                // Same type boost targeting the same person and both zero
                return@removeIf isAlsoBoost && it.split("|")[2] == rawMessage.split("|")[2] && it.split("|")[4] == "0"
            }
            if (othersExist) {
                battle.dispatchGo {
                    battle.broadcastChatMessage(battleLang("$rootKey.cap.multiple", pokemon.getName()))
                }
                return
            }
        }

        battle.dispatchGo {
            battle.broadcastChatMessage(battleLang("$rootKey.$severity", pokemon.getName(), stat))
            val boostBucket = if (isBoost) BattleContext.Type.BOOST else BattleContext.Type.UNBOOST
            val context = getContextFromAction(message, boostBucket, battle)
            // TODO: replace with context that tracks detailed information such as # of stages
            repeat(stages) {pokemon.contextManager.add(context)}
            battle.minorBattleActions[pokemon.uuid] = message
        }
    }

    private fun getStat(statKey: String) = when(statKey) {
        "atk" -> Stats.ATTACK
        "def" -> Stats.DEFENCE
        "spa" -> Stats.SPECIAL_ATTACK
        "spd" -> Stats.SPECIAL_DEFENCE
        "spe" -> Stats.SPEED
        "evasion" -> Stats.EVASION
        else -> Stats.ACCURACY
    }

    private fun getSeverity(stages: Int) = when(stages) {
        0 -> "cap.single"
        1 -> "slight"
        2 -> "sharp"
        else -> "severe"
    }

    private fun handleSetBoostInstruction(battle: PokemonBattle, line: String, remainingLines: MutableList<String>) {
        battle.dispatchGo {
            val message = BattleMessage(line)
            val pokemon = message.getBattlePokemon(0, battle) ?: return@dispatchGo
            val pokemonName = pokemon.getName()
            val effect = message.effect() ?: return@dispatchGo
            val lang = when(effect.id) {
                "bellydrum" -> battleLang("setboost.bellydrum", pokemonName)
                "angerpoint" -> battleLang("setboost.angerpoint", pokemonName)
                else -> battle.createUnimplemented(message)
            }
            battle.broadcastChatMessage(lang)
            pokemon.contextManager.add(getContextFromAction(message, BattleContext.Type.BOOST, battle))
            battle.minorBattleActions[pokemon.uuid] = message
        }
    }

    fun interpretMessage(battleId: UUID, message: String) {
        // Check key map and use function if matching
        if (message.startsWith("{\"winner\":\"")) {
            // The post-win message is something we don't care about just yet. It's basically a summary of what happened in the battle.
            // Check /docs/example-post-win-message.json for its format.
            return
        }

        val battle = BattleRegistry.getBattle(battleId)

        if (battle == null) {
            LOGGER.info("No battle could be found with the id: $battleId")
            return
        }

        runOnServer {
            battle.showdownMessages.add(message)
            interpret(battle, message)
        }
    }

    fun interpret(battle: PokemonBattle, rawMessage: String) {
        battle.log()
        battle.log(rawMessage)
        battle.log()
        try {
            val lines = rawMessage.split("\n").toMutableList()
            if (lines[0] == "update") {
                lines.removeAt(0)
                while (lines.isNotEmpty()) {
                    val line = lines.removeAt(0)

                    // Split blocks have a public and private message below
                    if (line.startsWith("|split|")) {
                        val showdownId = line.split("|split|")[1]
                        val targetActor = battle.getActor(showdownId)

                        if (targetActor == null) {
                            battle.log("No actor could be found with the showdown id: $showdownId")
                            return
                        }

                        val privateMessage = lines[0]
                        val publicMessage = lines[1]

                        for (instruction in splitUpdateInstructions.entries) {
                            if (lines[0].startsWith(instruction.key)) {
                                instruction.value(battle, targetActor, publicMessage, privateMessage)
                                break
                            }
                        }

                        lines.removeFirst()
                        lines.removeFirst()
                    } else {
                        if (line != "|") {
                            val instruction = updateInstructions.entries.find { line.startsWith(it.key) }?.value
                            if (instruction != null) {
                                instruction(battle, line, lines)
                            } else {
                                battle.dispatch {
                                    battle.broadcastChatMessage(line.text())
                                    GO
                                }
                            }
                        }
                    }
                }
            } else if (lines[0] == "sideupdate") {
                val showdownId = lines[1]
                val targetActor = battle.getActor(showdownId)
                val line = lines[2]

                if (targetActor == null) {
                    battle.log("No actor could be found with the showdown id: $showdownId")
                    return
                }

                for (instruction in sideUpdateInstructions.entries) {
                    if (line.startsWith(instruction.key)) {
                        instruction.value(battle, targetActor, line)
                    }
                }
            }
        } catch (e: Exception) {
            LOGGER.error("Caught exception interpreting {}", e)
        }
    }

    /**
     * Format:
     * |player|PLAYER|USERNAME|AVATAR|RATING
     *
     * Definitions:
     * PLAYER is p1 or p2 unless 4 player battle which adds p3 and p4
     * USERNAME is the Cobblemon battle actors uuid
     * AVATAR is unused currently
     * RATING is unused currently
     */
    private fun handlePlayerInstruction(battle: PokemonBattle, message: String, remainingLines: MutableList<String>) {
//        battle.log("Player Instruction")
    }

    /**
     * Format:
     * |teamsize|PLAYER|NUMBER
     *
     * Definitions:
     * PLAYER is p1 or p2 unless 4 player battle which adds p3 and p4
     * NUMBER is number of Pokémon your opponent starts with for team preview.
     */
    private fun handleTeamSizeInstruction(battle: PokemonBattle, message: String, remainingLines: MutableList<String>) {
//        battle.log("Team Size Instruction")
    }

    /**
     * Format:
     * |gametype|GAMETYPE
     *
     * Definitions:
     * GAMETYPE is singles, doubles, triples, multi, and or freeforall
     */
    private fun handleGameTypeInstruction(battle: PokemonBattle, message: String, remainingLines: MutableList<String>) {
        battle.log("Game Type Instruction: $message")
//
//        battle.broadcastChatMessage(LiteralText("${Formatting.GOLD}${Formatting.BOLD}Battle Type:"))
//
//        val tierName = message.split("|gametype|")[1]
//        val textComponent = LiteralText(" ${Formatting.GRAY}$tierName")
//        battle.broadcastChatMessage(textComponent)
//        battle.broadcastChatMessage("".text())
    }

    /**
     * Format:
     * |gen|GENNUM
     *
     * Definitions:
     * GENNUM is Generation number, from 1 to 7. Stadium counts as its respective gens;
     * Let's Go counts as 7, and modded formats count as whatever gen they were based on.
     */
    private fun handleGenInstruction(battle: PokemonBattle, message: String, remainingLines: MutableList<String>) {
        battle.log("Gen Instruction: $message")
    }

    /**
     * Format:
     * |tier|FORMATNAME
     *
     * Definitions:
     * FORMATNAME is the name of the format being played.
     */
    private fun handleTierInstruction(battle: PokemonBattle, message: String, remainingLines: MutableList<String>) {
        battle.log("Tier Instruction: $message")
//
//        battle.broadcastChatMessage(LiteralText("${Formatting.GOLD}${Formatting.BOLD}Battle Tier:"))
//
//        val tierName = message.split("|tier|")[1]
//        val textComponent = LiteralText(" ${Formatting.GRAY}$tierName")
//        battle.broadcastChatMessage(textComponent)
//        battle.broadcastChatMessage("".text())
    }

    /**
     * Format:
     * |rated or |rated|MESSAGE
     *
     * Definitions:
     * No message: Will be sent if the game will affect the player's ladder rating (Elo score).
     * Message: Will be sent if the game is official in some other way, such as being a tournament game.
     * Does not actually mean the game is rated.
     */
    private fun handleRatedInstruction(battle: PokemonBattle, message: String, remainingLines: MutableList<String>) {
//        battle.log("Rated Instruction")
    }

    /**
     * Format:
     * |rule|RULE: DESCRIPTION
     *
     * Definitions:
     * RULE is a rule and its description
     */
    private fun handleRuleInstruction(battle: PokemonBattle, message: String, remainingLines: MutableList<String>) {
        battle.log("Rule Instruction: $message")
        if (!battle.announcingRules) {
            battle.announcingRules = true
//            val textComponent = LiteralText("${Formatting.GOLD}${Formatting.BOLD}Battle Rules:")
//            battle.broadcastChatMessage(textComponent)
        }
//        val rule = message.substringAfter("|rule|")
//        val textComponent = LiteralText("${Formatting.GRAY} - $rule")
//        battle.broadcastChatMessage(textComponent)
    }

    /**
     * Format:
     * |clearpoke
     *
     * Marks the start of Team Preview
     */
    private fun handleClearPokeInstruction(battle: PokemonBattle, message: String, remainingLines: MutableList<String>) {
        battle.log("Clear Poke Instruction")
    }

    /**
     * Format:
     * |poke|PLAYER|DETAILS|ITEM
     *
     * Declares a Pokémon for Team Preview.
     *
     * PLAYER is the player ID
     * DETAILS describes the pokemon
     * ITEM will be an item if the pokemon is holding an item or blank if it isn't
     */
    private fun handlePokeInstruction(battle: PokemonBattle, message: String, remainingLines: MutableList<String>) {
        battle.log("Poke Instruction: $message")
//
//        val args = message.split("|")
//        val showdownId = args[2]
//        val pokemon = args[3]
//
//        val targetActor = battle.getActor(showdownId)
//
//        if (targetActor == null) {
//            battle.log("No actor could be found with the showdown id: $showdownId")
//            return
//        }
//
//        if (targetActor is PlayerBattleActor) {
//            if (!targetActor.announcedPokemon) {
//                targetActor.announcedPokemon = true
//                val textComponent = battleLang("your_team").gold().bold()
//                targetActor.sendMessage(textComponent)
//            }
//
//            battle.broadcastChatMessage("- $pokemon".aqua()) // change to our own description using UUID to get the Pokemon
//        }
    }

    /**
     * Format:
     * |teampreview indicates team preview is over
     */
    private fun handleTeamPreviewInstruction(battle: PokemonBattle, message: String, remainingLines: MutableList<String>) {
        battle.log("Start Team Preview Instruction: $message")
    }

    /**
     * Format:
     * |start
     *
     * Indicates that the game has started.
     */
    private fun handleStartInstruction(battle: PokemonBattle, message: String, remainingLines: MutableList<String>) {
        battle.log("Start Instruction: $message")
    }

    /**
     * Format:
     * |turn|NUMBER
     *
     * It is now turn NUMBER.
     */
    private fun handleTurnInstruction(battle: PokemonBattle, message: String, remainingLines: MutableList<String>) {
        if (!battle.started) {
            battle.started = true
            battle.actors.forEach { actor ->
                if (actor.uuid.getPlayer() != null) {
                    val initializePacket = BattleInitializePacket(battle, actor.getSide())
                    actor.sendUpdate(initializePacket)
                    actor.sendUpdate(BattleMusicPacket(battle))
                }
            }
            battle.actors.forEach { actor ->
                actor.sendUpdate(BattleSetTeamPokemonPacket(actor.pokemonList.map { it.effectedPokemon }))
                val req = actor.request ?: return@forEach
                actor.sendUpdate(BattleQueueRequestPacket(req))
            }
        }

        // TODO maybe tell the client that the turn number has changed
        val turnNumber = message.split("|turn|")[1].toInt()

        battle.dispatch {
            battle.sendToActors(BattleMakeChoicePacket())
            battle.broadcastChatMessage(battleLang("turn", turnNumber).aqua())
            battle.turn(turnNumber)
            GO
        }
    }

    private fun handleUpkeepInstruction(battle: PokemonBattle, message: String, remainingLines: MutableList<String>) {
        battle.dispatch {
            battle.actors.forEach { it.upkeep() }
            GO
        }
    }

    /**
     * Format:
     * |faint|POKEMON
     *
     * The Pokémon POKEMON has fainted.
     */
    private fun handleFaintInstruction(battle: PokemonBattle, rawMessage: String, remainingLines: MutableList<String>) {
        battle.dispatchWaiting(2.5F) {
            val pnx = rawMessage.split("|faint|")[1].substring(0, 3)
            val message = BattleMessage(rawMessage)
            val pokemon = message.getBattlePokemon(0, battle) ?: return@dispatchWaiting
            battle.sendUpdate(BattleFaintPacket(pnx, battleLang("fainted", pokemon.getName())))
            pokemon.effectedPokemon.currentHealth = 0
            pokemon.sendUpdate()
            battle.broadcastChatMessage(battleLang("fainted", pokemon.getName()).red())
            val context = getContextFromFaint(pokemon, battle)
            CobblemonEvents.BATTLE_FAINTED.post(BattleFaintedEvent(battle, pokemon, context))

            battle.getActorAndActiveSlotFromPNX(pnx).second.battlePokemon = null
            pokemon.contextManager.add(context)
            pokemon.contextManager.clear(BattleContext.Type.STATUS, BattleContext.Type.VOLATILE, BattleContext.Type.BOOST, BattleContext.Type.UNBOOST)
            battle.majorBattleActions[pokemon.uuid] = message
        }
    }

    private fun handleWinInstruction(battle: PokemonBattle, message: String, remainingLines: MutableList<String>) {
        battle.dispatch {
            val ids = message.split("|win|")[1].split("&").map { it.trim() }
            val winners = ids.map { battle.getActor(UUID.fromString(it))!! }
            val losers = battle.actors.filter { !winners.contains(it) }
            val winnersText = winners.map { it.getName() }.reduce { acc, next -> acc + " & " + next }

            battle.broadcastChatMessage(battleLang("win", winnersText).gold())

            battle.end()
            CobblemonEvents.BATTLE_VICTORY.post(BattleVictoryEvent(battle, winners, losers))

            this.lastCauser.remove(battle.battleId)
            GO
        }
    }

    fun handleStatusInstruction(battle: PokemonBattle, rawMessage: String, remainingLines: MutableList<String>) {
        battle.dispatchGo {
            val pnx = rawMessage.split("|-status|")[1].substring(0, 3)
            val message = BattleMessage(rawMessage)
            val pokemon = message.getBattlePokemon(0, battle) ?: return@dispatchGo
            val statusLabel = message.argumentAt(1) ?: return@dispatchGo
            val status = Statuses.getStatus(statusLabel)
                ?: return@dispatchGo LOGGER.error("Unrecognized status: $statusLabel")

            if (status is PersistentStatus) {
                pokemon.effectedPokemon.applyStatus(status)
                battle.sendUpdate(BattlePersistentStatusPacket(pnx, status))
            }

            battle.broadcastChatMessage(status.applyMessage.asTranslated(pokemon.getName()))
            pokemon.contextManager.add(getContextFromAction(message, BattleContext.Type.STATUS, battle))
            battle.minorBattleActions[pokemon.uuid] = message
        }
    }

    private fun handleMissInstruction(battle: PokemonBattle, rawMessage: String, remainingLines: MutableList<String>) {
        battle.dispatchWaiting(1.5F) {
            val message = BattleMessage(rawMessage)
            val pokemon = message.getBattlePokemon(0, battle) ?: return@dispatchWaiting
            battle.broadcastChatMessage(battleLang("missed"))
            battle.minorBattleActions[pokemon.uuid] = message
        }
    }

    private fun handleImmuneInstruction(battle: PokemonBattle, rawMessage: String, remainingLines: MutableList<String>) {
        battle.dispatchGo {
            val message = BattleMessage(rawMessage)
            val pokemon = message.getBattlePokemon(0, battle) ?: return@dispatchGo
            val name = pokemon.getName()
            battle.broadcastChatMessage(battleLang("immune", name))
            battle.minorBattleActions[pokemon.uuid] = message
        }
    }

    // |move|p1a: Charizard|Tackle|p2a: Magikarp
    private fun handleMoveInstruction(battle: PokemonBattle, rawMessage: String, remainingLines: MutableList<String>) {
        battle.dispatchGo {
            val message = BattleMessage(rawMessage)
            this.lastCauser[battle.battleId] = message

            val userPokemon = message.getBattlePokemon(0, battle) ?: return@dispatchGo
            val targetPokemon = message.getBattlePokemon(2, battle)

            val effect = message.effectAt(1) ?: return@dispatchGo
            val move = Moves.getByNameOrDummy(effect.id)

            userPokemon.effectedPokemon.let { pokemon ->
                val progress = UseMoveEvolutionProgress()
                if (progress.shouldKeep(pokemon)) {
                    val created = pokemon.evolutionProxy.current().progressFirstOrCreate({ it is UseMoveEvolutionProgress && it.currentProgress().move == move }) { progress }
                    created.updateProgress(UseMoveEvolutionProgress.Progress(created.currentProgress().move, created.currentProgress().amount + 1))
                }
            }

            if (move.name != "struggle" && targetPokemon != null && targetPokemon != userPokemon) {
                battle.broadcastChatMessage(battleLang(
                    key = "used_move_on",
                    userPokemon.getName(),
                    move.displayName,
                    targetPokemon.getName()
                ))
            } else {
                battle.broadcastChatMessage(battleLang(
                    key = "used_move",
                    userPokemon.getName(),
                    move.displayName
                ))
            }

            battle.majorBattleActions[userPokemon.uuid] = message
        }
    }

    private fun handleCantInstruction(battle: PokemonBattle, rawMessage: String, remainingLines: MutableList<String>) {
        battle.dispatchGo {
            val message = BattleMessage(rawMessage)
            val pokemon = message.getBattlePokemon(0, battle) ?: return@dispatchGo
            val reason = message.argumentAt(1) ?: return@dispatchGo
            // This may be null as it's not always given
            val moveName = message.argumentAt(2)?.let { Moves.getByName(it)?.displayName } ?: Text.EMPTY
            val name = pokemon.getName()
            val actionText = when(reason) {
                // ToDo in the games they use a generic image because there is a popup of the ability and the sprite of the mon, it may be good to have a similar system here
                "ability: Armor Tail", "ability: Damp", "ability: Dazzling", "ability: Queenly Majesty" -> battleLang("cant.generic_block", name, moveName)
                "ability: Truant" -> battleLang("cant.truant", name, moveName)
                Statuses.PARALYSIS.showdownName -> lang("status.paralysis.is", name)
                Statuses.SLEEP.showdownName -> lang("status.sleep.is", name)
                Statuses.FROZEN.showdownName -> lang("status.frozen.is", name)
                "flinch" -> battleLang("cant.flinched", name)
                "recharge" -> battleLang("cant.recharge", name)
                "Attract" -> battleLang("cant.attract", name)
                "Disable" -> battleLang("cant.disable", name)
                "Focus Punch" -> battleLang("cant.focus_punch", name)
                "move: Heal Block" -> battleLang("cant.heal_block", name, moveName)
                "move: Imprison" -> battleLang("cant.imprison", name, moveName)
                "move: Gravity" -> battleLang("cant.gravity", name, moveName)
                "Shell Trap" -> battleLang("cant.shell_trap", name)
                "move: Taunt" -> battleLang("cant.taunt", name, moveName)
                "move: Throat Chop" -> battleLang("cant.throat_chop", name, moveName)
                "nopp" -> battleLang("cant.no_pp", name, moveName)
                else -> battle.createUnimplemented(message).copy()
            }
            battle.broadcastChatMessage(actionText.red())
            battle.minorBattleActions[pokemon.uuid] = message
        }
    }

    /**
     * Format:
     * |-resisted|p%a
     *
     * player % resisted the attack.
     */
    private fun handleResistInstruction(battle: PokemonBattle, rawMessage: String, remainingLines: MutableList<String>) {
        battle.dispatchGo {
            val message = BattleMessage(rawMessage)
            val pokemon = message.getBattlePokemon(0, battle) ?: return@dispatchGo
            battle.broadcastChatMessage(battleLang("resisted"))
            battle.minorBattleActions[pokemon.uuid] = message
        }
    }

    /**
     * Format:
     * |pp_update|<side_id>: <pokemon_uuid>|...<move_id>: <move_pp>
     */
    private fun handlePpUpdateInstruction(battle: PokemonBattle, message: String, remainingLines: MutableList<String>) {
        battle.dispatch {
            val editMessaged = message.replace("|pp_update|", "")
            val data = editMessaged.split("|")
            val actorAndPokemonData = data[0].split(": ")
            val actorID = actorAndPokemonData[0]
            val pokemonID = UUID.fromString(actorAndPokemonData[1])
            val actor = battle.getActor(actorID) ?: return@dispatch GO
            val pokemon = actor.pokemonList.firstOrNull { battlePokemon -> battlePokemon.effectedPokemon.uuid == pokemonID } ?: return@dispatch GO
            val moveDatum = data[1].split(", ")
            moveDatum.forEach { moveData ->
                val moveIdAndPp = moveData.split(": ")
                val moveId = moveIdAndPp[0]
                val movePp = moveIdAndPp[1]
                val move = pokemon.effectedPokemon.moveSet.firstOrNull { move -> move.name.equals(moveId, true) } ?: return@dispatch GO
                move.currentPp = movePp.toInt()
            }
            GO
        }
    }

    /**
     * Format:
     * |-supereffective|p%a
     *
     * player % was weak against the attack.
     */
    private fun handleSuperEffectiveInstruction(battle: PokemonBattle, rawMessage: String, remainingLines: MutableList<String>) {
        battle.dispatchGo {
            val message = BattleMessage(rawMessage)
            val pokemon = message.getBattlePokemon(0, battle) ?: return@dispatchGo
            battle.broadcastChatMessage(battleLang("superEffective"))
            battle.minorBattleActions[pokemon.uuid] = message
        }
    }

    private fun handleCritInstruction(battle: PokemonBattle, rawMessage: String, remainingLines: MutableList<String>) {
        battle.dispatchGo {
            val message = BattleMessage(rawMessage)
            val pokemon = message.getBattlePokemon(0, battle) ?: return@dispatchGo
            battle.broadcastChatMessage(battleLang("crit"))
            this.lastCauser[battle.battleId]?.let { message ->
                val battlePokemon = message.getBattlePokemon(0, battle) ?: return@let
                battlePokemon.criticalHits++
            }
            battle.minorBattleActions[pokemon.uuid] = message
        }
    }

    private fun handleWeatherInstruction(battle: PokemonBattle, rawMessage: String, remainingLines: MutableList<String>){
        battle.dispatchGo{
            val message = BattleMessage(rawMessage)
            val weather = message.argumentAt(0) ?: return@dispatchGo

            if (message.hasOptionalArgument("upkeep")) {
                when (weather){
                    "RainDance" -> battle.broadcastChatMessage(battleLang("rain_dance_upkeep"))
                    "Sandstorm" -> battle.broadcastChatMessage(battleLang("sandstorm_upkeep"))
                    "SunnyDay" -> battle.broadcastChatMessage(battleLang("sunny_day_upkeep"))
                    "Hail" -> battle.broadcastChatMessage(battleLang("hail_upkeep"))
                }
            }
            else {
                when (weather){
                    "RainDance" -> battle.broadcastChatMessage(battleLang("rain_dance"))
                    "Sandstorm" -> battle.broadcastChatMessage(battleLang("sandstorm"))
                    "SunnyDay" -> battle.broadcastChatMessage(battleLang("sunny_day_upkeep"))
                    "Hail" -> battle.broadcastChatMessage(battleLang("hail"))
                    "NoWeather" -> battle.broadcastChatMessage(battleLang("rain_dance_upkeep"))
                }
                battle.contextManager.add(getContextFromAction(message, BattleContext.Type.WEATHER, battle))
            }
        }
    }

    private fun handleFailInstruction(battle: PokemonBattle, rawMessage: String, remainingLines: MutableList<String>){
        battle.dispatchWaiting(1.5F){
            val message = BattleMessage(rawMessage)
            val pokemon = message.getBattlePokemon(0, battle) ?: return@dispatchWaiting
            battle.broadcastChatMessage(battleLang("fail"))
            battle.minorBattleActions[pokemon.uuid] = message
        }
    }

    private fun handleRechargeInstructions(battle: PokemonBattle, rawMessage: String, remainingLines: MutableList<String>){
        battle.dispatchWaiting(2F){
            val message = BattleMessage(rawMessage)
            val pokemon = message.getBattlePokemon(0, battle) ?: return@dispatchWaiting
            battle.broadcastChatMessage(battleLang("recharge", pokemon.getName() ?: ""))
            battle.minorBattleActions[pokemon.uuid] = message
        }
    }

    private fun handleCureStatusInstruction(battle: PokemonBattle, rawMessage: String, remainingLines: MutableList<String>) {
        battle.dispatchWaiting {
            val message = BattleMessage(rawMessage)
            val maybeActivePokemon = message.actorAndActivePokemon(0, battle)?.second?.battlePokemon
            val maybePartyPokemon = message.getBattlePokemon(0, battle)
            val pokemon = maybeActivePokemon ?: maybePartyPokemon ?: return@dispatchWaiting
            val status = message.argumentAt(1)?.let(Statuses::getStatus) ?: return@dispatchWaiting
            val effect = message.effect()
            pokemon.effectedPokemon.status = null
            pokemon.sendUpdate()

            if (maybeActivePokemon != null) {
                val pnx = message.argumentAt(0)?.substring(0, 3)
                if (pnx is String) {
                    battle.sendUpdate(BattlePersistentStatusPacket(pnx, null))
                }
            }
            val lang = when {
                effect?.type == Effect.Type.ABILITY -> battleLang("cure_status.ability.${effect.id}", pokemon.getName())
                // Lang related to move stuff is tied to the status as a generic message such as fire moves defrosting Pokémon
                effect?.type == Effect.Type.MOVE -> battleLang("cure_status.move.${status.name}", pokemon.getName(), Moves.getByNameOrDummy(effect.id).displayName)
                message.hasOptionalArgument("msg") -> status.removeMessage?.asTranslated(pokemon.getName()) ?: return@dispatchWaiting
                else -> return@dispatchWaiting
            }
            battle.broadcastChatMessage(lang)
            pokemon.contextManager.remove(status.showdownName, BattleContext.Type.STATUS)
            battle.minorBattleActions[pokemon.uuid] = message
        }
    }

    private fun handleStartInstructions(battle: PokemonBattle, rawMessage: String, remainingLines: MutableList<String>){
        battle.dispatchWaiting(2F) {
            val message = BattleMessage(rawMessage)
            val pokemon = message.getBattlePokemon(0, battle) ?: return@dispatchWaiting
            val effect = message.effectAt(1) ?: return@dispatchWaiting
            if (message.hasOptionalArgument("silent")) {
                LOGGER.debug("Received silent: {}", message.rawMessage)
            }
            else {
                val lang = when (effect.id) {
                    "confusion" -> battleLang("start.confusion", pokemon.getName())
                    "bide" -> battleLang("start.bide", pokemon.getName())
                    "yawn" -> battleLang("start.yawn", pokemon.getName())
                    "leechseed" -> battleLang("start.leechseed", pokemon.getName())
                    "aquaring" -> battleLang("start.aqua_ring", pokemon.getName())
                    "charge" -> battleLang("start.charge", pokemon.getName())
                    "taunt" -> battleLang("start.taunt", pokemon.getName())
                    "autotomize" -> battleLang("start.autotomize", pokemon.getName())
                    "attract" -> battleLang("start.attract", pokemon.getName())
                    // ignore 3 to prevent clutter (-fieldactivate already announces perish)
                    "perish3" -> return@dispatchWaiting
                    "perish2", "perish1", "perish0" -> battleLang("start.perish_count", pokemon.getName(), effect.id.last().digitToInt())
                    else -> battle.createUnimplemented(message)
                }
                battle.broadcastChatMessage(lang)

                // skip adding contexts for every time the perish counter decrements
                if (!effect.id.contains("perish")) {
                    // don't need to add unique: showdown won't send -start instruction if volatile status is already present
                    pokemon.contextManager.add(getContextFromAction(message, BattleContext.Type.VOLATILE, battle))
                }
            }
            battle.minorBattleActions[pokemon.uuid] = message
        }
    }

    private fun handleSingleTurnInstruction(battle: PokemonBattle, rawMessage: String, remainingLines: MutableList<String>) {
        battle.dispatchGo{
            val message = BattleMessage(rawMessage)
            val pokemon = message.getBattlePokemon(0, battle) ?: return@dispatchGo
            val pokemonName = pokemon.getName()
            val effect = message.effectAt(1) ?: return@dispatchGo
            val lang = when(effect.id) {
                "protect" -> battleLang("singleturn.protect", pokemonName)
                "endure" -> battleLang("singleturn.endure", pokemonName)
                "craftyshield" -> battleLang("singleturn.crafty_shield", pokemonName)
                "powder" -> battleLang("singleturn.powder", pokemonName)
                "followme" -> battleLang("singleturn.follow_me", pokemonName)
                "snatch" -> battleLang("singleturn.snatch", pokemonName)
                "quickguard" -> battleLang("singleturn.quick_guard", pokemonName)
                "wideguard" -> battleLang("singleturn.wide_guard", pokemonName)
                "roost" -> battleLang("singleturn.roost", pokemonName)
                "matblock" -> battleLang("singleturn.mat_block", pokemonName)
                "maxguard" -> battleLang("singleturn.max_guard", pokemonName)
                "instruct" -> battleLang("singleturn.instruct", pokemonName, message.actorAndActivePokemonFromOptional(battle)?.second?.battlePokemon?.getName() ?: return@dispatchGo)
                "focuspunch" -> battleLang("singleturn.focus_punch", pokemonName)
                "electrify" -> battleLang("singleturn.electrify", pokemonName)
                "beakblast" -> battleLang("singleturn.beak_blast", pokemonName)
                "helpinghand" -> battleLang("singleturn.helping_hand", pokemonName, message.actorAndActivePokemonFromOptional(battle)?.second?.battlePokemon?.getName() ?: return@dispatchGo)
                "magiccoat" -> battleLang("singleturn.magic_coat", pokemonName)
                "ragepowder" -> battleLang("singleturn.rage_powder", pokemonName)
                "shelltrap" -> battleLang("singleturn.shell_trap", pokemonName)
                "spotlight" -> battleLang("singleturn.spotlight", pokemonName)
                else -> battle.createUnimplemented(message)
            }
            battle.broadcastChatMessage(lang)
            battle.minorBattleActions[pokemon.uuid] = message
        }
    }

    private fun handleSingleMoveInstruction(battle: PokemonBattle, rawMessage: String, remainingLines: MutableList<String>) {
        battle.dispatchGo {
            val message = BattleMessage(rawMessage)
            val pokemon = message.getBattlePokemon(0, battle) ?: return@dispatchGo
            val pokemonName = pokemon.getName()
            val effect = message.effectAt(1) ?: return@dispatchGo
            val lang = when (effect.id) {
                "destinybond" -> battleLang("singlemove.destiny_bond", pokemonName)
                "glaiverush" -> battleLang("singlemove.glaive_rush", pokemonName)
                "grudge" -> battleLang("singlemove.grudge", pokemonName)
                "rage" -> battleLang("singlemove.rage", pokemonName)
                else -> battle.createUnimplemented(message)
            }
            battle.broadcastChatMessage(lang)
            battle.minorBattleActions[pokemon.uuid] = message
        }
    }

    private fun handleActivateInstructions(battle: PokemonBattle, rawMessage: String, remainingLines: MutableList<String>){
        battle.dispatchGo{
            val message = BattleMessage(rawMessage)
            this.lastCauser[battle.battleId] = message

            // Sim protocol claims it's '|-activate|EFFECT' but it seems to always be '|-activate|POKEMON|EFFECT'
            // can also be '|-activate|POKEMON|EFFECT|[of] POKEMON'
            val pokemon = message.getBattlePokemon(0, battle) ?: return@dispatchGo
            val pokemonName = pokemon.getName()
            val effect = message.effectAt(1) ?: return@dispatchGo
            // Don't say anything about it, it's too spammy
            if (effect.id == "confusion") {
                return@dispatchGo
            }
            val lang = when(effect.id) {
                "protect" -> battleLang("activate.protect", pokemonName)
                // Includes a 3rd argument being the magnitude level as a number
                "magnitude" -> battleLang("activate.magnitude", message.argumentAt(2)?.toIntOrNull() ?: 1)
                "bide" -> battleLang("activate.bide", pokemonName)
                "gravity" -> battleLang("activate.gravity", pokemonName)
                "focusband" -> battleLang("item.hung_on.end", pokemonName, CobblemonItems.FOCUS_BAND.name)
                "mistyterrain" -> battleLang("activate.misty_terrain", pokemonName)
                "psychicterrain" -> battleLang("activate.psychic_terrain", pokemonName)
                "healbell" -> battleLang("activate.heal_bell")
                "aromatherapy" -> battleLang("activate.aromatherapy")
                "trapped" -> battleLang("activate.trapped")
                "quickclaw" -> battleLang("item.quick_claw.end", pokemonName)
                "bind" -> battleLang("activate.bind", pokemonName, message.actorAndActivePokemonFromOptional(battle)?.second?.battlePokemon?.getName() ?: return@dispatchGo)
                "courtchange" -> battleLang("activate.court_change", pokemonName)
                "guardsplit" -> battleLang("activate.guard_split", pokemonName)
                "spite" -> battleLang("activate.spite", pokemonName, message.argumentAt(2)!!, message.argumentAt(3)!!)
                "wrap" -> battleLang("activate.wrap", pokemonName, message.actorAndActivePokemonFromOptional(battle)?.second?.battlePokemon?.getName() ?: return@dispatchGo)
                "lockon" -> battleLang("activate.lock_on", message.actorAndActivePokemonFromOptional(battle)?.second?.battlePokemon?.getName() ?: return@dispatchGo, pokemonName)
                "protosynthesis" -> battleLang("activate.protosynthesis", pokemonName)
                "struggle" -> battleLang("activate.struggle", pokemonName)
                // Don't need additional lang, -sidestart handles it
                "toxicdebris" -> "".asTranslated()
                "destinybond" -> {
                    battle.activePokemon.mapNotNull { it.battlePokemon?.uuid }.forEach { battle.minorBattleActions[it] = message }
                    battleLang("activate.destiny_bond", pokemonName)
                }
                "shedskin" -> {
                    val status = pokemon.effectedPokemon.status?.status?.showdownName ?: return@dispatchGo
                    when (status) {
                        "brn" -> lang("status.burn.cure", pokemonName)
                        "frz" -> lang("status.frozen.thawed", pokemonName)
                        "par" -> lang("status.paralysis.cure", pokemonName)
                        "slp" -> lang("status.sleep.woke", pokemonName)
                        else -> lang("status.poison.cure", pokemonName)
                    }
                }
                "synchronize" -> battleLang("activate.synchronize", pokemonName)
                "endure" -> battleLang("activate.endure", pokemonName)
                "firespin" -> battleLang("activate.firespin", pokemonName)
                "attract" -> {
                    val sourcePokemonName = message.getSourceBattlePokemon(battle)?.getName() ?: return@dispatchGo
                    battleLang("activate.attract", pokemonName, sourcePokemonName)
                }
                else -> battle.createUnimplemented(message)
            }
            battle.broadcastChatMessage(lang)
            battle.minorBattleActions[pokemon.uuid] = message
        }
    }

    private fun handleFieldStartInstructions(battle: PokemonBattle, rawMessage: String, remainingLines: MutableList<String>){
        battle.dispatchWaiting {
            val message = BattleMessage(rawMessage)
            val move = message.argumentAt(0) ?: return@dispatchWaiting
            // Note persistent is a CAP ability only we can ignore the flag
            val lang: Text = when (move) {
                // Covers ability starts too they share lang
                "move: Electric Terrain" -> battleLang("field_start.electric_terrain")
                "move: Grassy Terrain" -> battleLang("field_start.grassy_terrain")
                "move: Gravity" -> battleLang("field_start.gravity")
                "move: Magic Room" -> battleLang("field_start.magic_room")
                "move: Misty Terrain" -> battleLang("field_start.misty_terrain")
                "move: Mud Sport" -> battleLang("field_start.mud_sport")
                "move: Psychic Terrain" -> battleLang("field_start.psychic_terrain")
                "move: Trick Room" -> {
                    val user = message.actorAndActivePokemonFromOptional(battle, "of")?.second?.battlePokemon
                    battleLang("field_start.trick_room", user?.getName() ?: Text.literal("UNKNOWN"))
                }
                "move: Water Sport" -> battleLang("field_start.water_sport")
                "move: Wonder Room" -> battleLang("field_start.wonder_room")
                else -> battle.createUnimplemented(message)
            }
            battle.broadcastChatMessage(lang)

            val type = BattleContext.Type.valueOf(move.substringAfterLast(" ").uppercase())
            battle.contextManager.add(getContextFromAction(message, type, battle))
        }
    }

    private fun handleFieldEndInstructions(battle: PokemonBattle, rawMessage: String, remainingLines: MutableList<String>){
        battle.dispatchWaiting {
            val message = BattleMessage(rawMessage)
            val move = message.argumentAt(0) ?: return@dispatchWaiting
            // Note persistent is a CAP ability only we can ignore the flag
            val lang: Text = when (move) {
                // Covers ability starts too they share lang
                "move: Electric Terrain" -> battleLang("field_end.electric_terrain")
                "move: Grassy Terrain" -> battleLang("field_end.grassy_terrain")
                "move: Gravity" -> battleLang("field_end.gravity")
                "move: Magic Room" -> battleLang("field_end.magic_room")
                "move: Misty Terrain" -> battleLang("field_end.misty_terrain")
                "move: Mud Sport" -> battleLang("field_end.mud_sport")
                "move: Psychic Terrain" -> battleLang("field_end.psychic_terrain")
                "move: Trick Room" -> {
                    val user = message.actorAndActivePokemonFromOptional(battle, "of")?.second?.battlePokemon
                    battleLang("field_end.trick_room", user?.getName() ?: Text.literal("UNKNOWN"))
                }
                "move: Water Sport" -> battleLang("field_end.water_sport")
                "move: Wonder Room" -> battleLang("field_start.wonder_room")
                else -> battle.createUnimplemented(message)
            }
            battle.broadcastChatMessage(lang)

            val type = BattleContext.Type.valueOf(move.substringAfterLast(" ").uppercase())
            battle.contextManager.remove(message.effectAt(0)!!.id, type)
        }
    }

    private fun handleFieldActivateInstructions(battle: PokemonBattle, rawMessage: String, remainingLines: MutableList<String>){
        battle.dispatchWaiting {
            val message = BattleMessage(rawMessage)
            val effect = message.effectAt(0) ?: return@dispatchWaiting
            val lang = when (effect.id) {
                "perishsong" -> battleLang("field_activate.perish_song")
                else -> battle.createUnimplemented(message)
            }
            battle.broadcastChatMessage(lang)

            // share this action with all active Pokemon
            battle.activePokemon.forEach {
                it.battlePokemon?.contextManager?.addUnique(getContextFromAction(message, BattleContext.Type.VOLATILE, battle))
            }
        }
    }

    private fun handleAbilityInstructions(battle: PokemonBattle, rawMessage: String, remainingLines: MutableList<String>) {
        battle.dispatchGo {
            val message = BattleMessage(rawMessage)
            this.lastCauser[battle.battleId] = message

            val pokemon = message.getBattlePokemon(0, battle) ?: return@dispatchGo
            val pokemonName = pokemon.getName()
            val effect = message.effectAt(1) ?: return@dispatchGo
            val feedback = when (effect.id) {
                "speedboost" -> battleLang("ability.speed_boost", pokemonName)
                "sturdy" -> battleLang("ability.sturdy", pokemonName)
                "intimidate" -> battleLang("ability.intimidate", pokemonName)
                "unnerve" -> battleLang("ability.unnerve", pokemonName)
                "anticipation" -> battleLang("ability.anticipation", pokemonName)
                "airlock" -> battleLang("ability.air_lock")
                "cloudnine" -> battleLang("ability.air_lock") //Cloud Nine shares the same text as Air Lock
                else -> battleLang("ability.generic", pokemonName, message.argumentAt(1)!!)
            }
            battle.broadcastChatMessage(feedback)
            battle.minorBattleActions[pokemon.uuid] = message
        }
    }

    private fun handlePrepareInstruction(battle: PokemonBattle, rawMessage: String, remainingLines: MutableList<String>) {
        battle.dispatchGo {
            val message = BattleMessage(rawMessage)
            val pokemon = message.getBattlePokemon(0, battle) ?: return@dispatchGo
            val pokemonName = pokemon.getName()
            val effect = message.effectAt(1) ?: return@dispatchGo
            //Prevents spam when the move Role Play is used
            val lang = when(effect.id) {
                "bounce" -> battleLang("prepare.bounce", pokemonName)
                "dig" -> battleLang("prepare.dig", pokemonName)
                "dive" -> battleLang("prepare.dive", pokemonName)
                "fly" -> battleLang("prepare.fly", pokemonName)
                "freezeshock" -> battleLang("prepare.freeze_shock", pokemonName)
                "geomancy" -> battleLang("prepare.geomancy", pokemonName)
                "iceburn" -> battleLang("prepare.ice_burn", pokemonName)
                "meteorbeam" -> battleLang("prepare.meteor_beam", pokemonName)
                "phantomforce" -> battleLang("prepare.phantom_force", pokemonName)
                "razorwind" -> battleLang("prepare.razor_wind", pokemonName)
                "shadowforce" -> battleLang("prepare.phantom_force", pokemonName) //Phantom Force and Shadow Force share the same text
                "skullbash" -> battleLang("prepare.skull_bash", pokemonName)
                "skyattack" -> battleLang("prepare.sky_attack", pokemonName)
                "skydrop" -> battleLang("prepare.sky_drop", pokemonName)
                "solarbeam" -> battleLang("prepare.solar_beam", pokemonName)
                "solarblade" -> battleLang("prepare.solar_beam", pokemonName) //Solar Beam and Solar Blade share the same text
                else -> battle.createUnimplemented(message)
            }
            battle.broadcastChatMessage(lang)
            battle.minorBattleActions[pokemon.uuid] = message
        }
    }

    private fun handleSwapBoostInstruction(battle: PokemonBattle, rawMessage: String, remainingLines: MutableList<String>) {
        battle.dispatchGo {
            val message = BattleMessage(rawMessage)
            val pokemon = message.getBattlePokemon(0, battle) ?: return@dispatchGo
            val pokemonName = pokemon.getName()
            val targetPokemon = message.getBattlePokemon(1, battle) ?: return@dispatchGo
            val targetPokemonName = targetPokemon.getName()
            val effect = message.effect() ?: return@dispatchGo
            val lang = when(effect.id) {
                "guardswap" -> battleLang("swapboost.generic", pokemonName, targetPokemonName)
                "heartswap" -> battleLang("swapboost.generic", pokemonName, targetPokemonName)
                "powerswap" -> battleLang("swapboost.generic", pokemonName, targetPokemonName)
                else -> battle.createUnimplemented(message)
            }
            battle.broadcastChatMessage(lang)

            pokemon.contextManager.swap(targetPokemon.contextManager, BattleContext.Type.BOOST)
            pokemon.contextManager.swap(targetPokemon.contextManager, BattleContext.Type.UNBOOST)
            battle.minorBattleActions[pokemon.uuid] = message
        }
    }

    private fun handleEndInstruction(battle: PokemonBattle, rawMessage: String, remainingLines: MutableList<String>) {
        battle.dispatchWaiting {
            val message = BattleMessage(rawMessage)
            val pokemon = message.getBattlePokemon(0, battle) ?: return@dispatchWaiting
            val pokemonName = pokemon.getName()
            val effect = message.effectAt(1) ?: return@dispatchWaiting
            if (message.hasOptionalArgument("silent")) {
                LOGGER.debug("Received silent: {}", message.rawMessage)
            }
            else {
                val feedback = when (effect.id) {
                    "confusion" -> battleLang("end.confusion", pokemonName)
                    "bide" -> battleLang("end.bide", pokemonName)
                    "bind" -> battleLang("end.bind", pokemonName)
                    "wrap" -> battleLang("end.wrap", pokemonName)
                    "disable" -> battleLang("end.disable", pokemonName)
                    "protosynthesis" -> battleLang("end.protosynthesis", pokemonName)
                    "yawn" -> lang("status.sleep.apply", pokemonName)
                    "taunt" -> battleLang("end.taunt", pokemonName)
                    "firespin" -> battleLang("end.firespin", pokemonName)
                    else -> battle.createUnimplemented(message)
                }
                battle.broadcastChatMessage(feedback)
            }
            pokemon.contextManager.remove(effect.id, BattleContext.Type.VOLATILE)
            battle.minorBattleActions[pokemon.uuid] = message
        }
    }

    private fun handleSideStartInstructions(battle: PokemonBattle, rawMessage: String, remainingLines: MutableList<String>){
        battle.dispatchWaiting {
            val message = BattleMessage(rawMessage)
            val side = if (message.argumentAt(0)?.get(1) == '1') battle.side1 else battle.side2
            val condition = message.argumentAt(1) ?: return@dispatchWaiting
            battle.sides.forEach {
                val subject = if (it == side) battleLang("side_subject.ally") else battleLang("side_subject.opponent")
                val lang = when (message.argumentAt(1)) {
                    "move: Stealth Rock" -> battleLang("side_start.stealth_rock", subject)
                    "Spikes" -> battleLang("side_start.spikes", subject)
                    "move: Toxic Spikes" -> battleLang("side_start.toxic_spikes", subject)
                    "move: Sticky Web" -> battleLang("side_start.sticky_web", subject)
                    "move: Reflect" -> battleLang("side_start.reflect", subject)
                    "move: Light Screen" -> battleLang("side_start.light_screen", subject)
                    "move: Aurora Veil" -> battleLang("side_start.aurora_veil", subject)
                    "move: Tailwind" -> battleLang("side_start.tailwind", subject)
                    else -> battle.createUnimplemented(message)
                }
                it.broadcastChatMessage(lang)
            }

            val bucket = when(condition.substringAfterLast(" ").lowercase()) {
                "reflect", "screen", "veil" -> BattleContext.Type.SCREEN
                "spikes", "rock", "web" -> BattleContext.Type.HAZARD
                "tailwind" -> BattleContext.Type.TAILWIND
                else -> BattleContext.Type.MISC
            }
            side.contextManager.add(getContextFromAction(message, bucket, battle))
        }
    }

    private fun handleSideEndInstructions(battle: PokemonBattle, rawMessage: String, remainingLines: MutableList<String>){
        battle.dispatchWaiting {
            val message = BattleMessage(rawMessage)
            val side = if (message.argumentAt(0)?.get(1) == '1') battle.side1 else battle.side2
            val condition = message.argumentAt(1) ?: return@dispatchWaiting
            val conditionEffect = message.effectAt(1) ?: return@dispatchWaiting
            battle.sides.forEach {
                val subject = if (it == side) battleLang("side_subject.ally") else battleLang("side_subject.opponent")
                val lang = when (message.argumentAt(1)) {
                    "move: Stealth Rock" -> battleLang("side_end.stealth_rock", subject)
                    "Spikes" -> battleLang("side_end.spikes", subject)
                    "move: Toxic Spikes" -> battleLang("side_end.toxic_spikes", subject)
                    "move: Sticky Web" -> battleLang("side_end.sticky_web", subject)
                    "move: Reflect" -> battleLang("side_end.reflect", subject)
                    "move: Light Screen" -> battleLang("side_end.light_screen", subject)
                    "move: Aurora Veil" -> battleLang("side_end.aurora_veil", subject)
                    "move: Tailwind" -> battleLang("side_end.tailwind", subject)
                    else -> battle.createUnimplemented(message)
                }
                it.broadcastChatMessage(lang)
            }

            val bucket = when(condition.substringAfterLast(" ").lowercase()) {
                "reflect", "screen", "veil" -> BattleContext.Type.SCREEN
                "spikes", "rock", "web" -> BattleContext.Type.HAZARD
                "tailwind" -> BattleContext.Type.TAILWIND
                else -> BattleContext.Type.MISC
            }
            side.contextManager.remove(conditionEffect.id, bucket)
        }
    }

    /**
     * Format:
     * |error|ERROR
     *
     * Some examples
     * |error|[Invalid choice] Can't choose for Team Preview: You're not in a Team Preview phase
     * |error|[Unavailable choice] Can't switch: The active Pokémon is trapped
     * The protocol message to tell you to send a different decision:
     */
    private fun handleErrorInstructions(battle: PokemonBattle, battleActor: BattleActor, message: String) {
        battle.log("Error Instruction")
        battle.dispatchGo {
            //TODO: some lang stuff for the error messages (Whats the protocol for adding to other langs )
            //Also is it okay to ignore the team preview error for now? - You bet!
            val battleMessage = BattleMessage(message)
            val lang = when(message) {
                "|error|[Unavailable choice] Can't switch: The active Pokémon is trapped" -> battleLang("error.pokemon_is_trapped").red()
                "|error|[Invalid choice] Can't choose for Team Preview: You're not in a Team Preview phase" -> return@dispatchGo
                else -> battle.createUnimplemented(battleMessage)
            }
            battleActor.sendMessage(lang)
            battleActor.mustChoose = true
            battleActor.sendUpdate(BattleMadeInvalidChoicePacket())
        }
    }

    /**
     * Format:
     * |request|REQUEST
     *
     * The protocol message to tell you that it's time for you to make a decision is:
     */
    private fun handleRequestInstruction(battle: PokemonBattle, battleActor: BattleActor, message: String) {
        battle.log("Request Instruction")

        if (message.contains("teamPreview")) // TODO probably change when we're allowing team preview
            return

        // Parse Json message and update state info for actor
        val request = BattleRegistry.gson.fromJson(message.split("|request|")[1], ShowdownActionRequest::class.java)
        if (battle.started) {
            battle.dispatchGo {
                battleActor.sendUpdate(BattleQueueRequestPacket(request))
                battleActor.request = request
                battleActor.responses.clear()
                // We need to send this out because 'upkeep' isn't received until the request is handled since the turn won't swap
                if (request.forceSwitch.withIndex().any { it.value && battleActor.activePokemon.getOrNull(it.index)?.isGone() == false }) {
                    battle.doWhenClear {
                        battleActor.mustChoose = true
                        battleActor.sendUpdate(BattleMakeChoicePacket())
                    }
                }
            }
        } else {
            battleActor.request = request
            battleActor.responses.clear()
        }
    }

    private fun handleSwitchInstruction(battle: PokemonBattle, battleActor: BattleActor, publicMessage: String, privateMessage: String) {
        val message = BattleMessage(publicMessage)
        val (pnx, pokemonID) = message.pnxAndUuid(0) ?: return
        if (!battle.started) {
            val (actor, activePokemon) = battle.getActorAndActiveSlotFromPNX(pnx)
            val pokemon = battle.getBattlePokemon(pnx, pokemonID)
            val entity = if (actor is EntityBackedBattleActor<*>) actor.entity else null

            activePokemon.battlePokemon = pokemon
            val pokemonEntity = pokemon.entity
            if (pokemonEntity == null && entity != null) {
                val targetPos = battleActor.getSide().getOppositeSide().actors.filterIsInstance<EntityBackedBattleActor<*>>().firstOrNull()?.entity?.pos?.let { pos ->
                    val offset = pos.subtract(entity.pos)
                    val idealPos = entity.pos.add(offset.multiply(0.33))
                    idealPos
                } ?: entity.pos

                pokemon.effectedPokemon.sendOutWithAnimation(
                    source = entity,
                    battleId = battle.battleId,
                    level = entity.world as ServerWorld,
                    position = targetPos
                )
            }
        } else {
            battle.dispatchInsert {
                val (actor, activePokemon) = battle.getActorAndActiveSlotFromPNX(pnx)
                val pokemon = battle.getBattlePokemon(pnx, pokemonID)
                val entity = if (actor is EntityBackedBattleActor<*>) actor.entity else null
                pokemon.sendUpdate()

                if (activePokemon.battlePokemon == pokemon) {
                    return@dispatchInsert emptySet() // Already switched in, Showdown does this if the pokemon is going to die before it can switch
                }

                activePokemon.battlePokemon?.let { oldPokemon ->
                    if (message.effect()?.id == "batonpass") oldPokemon.contextManager.swap(pokemon.contextManager, BattleContext.Type.BOOST, BattleContext.Type.UNBOOST)
                    oldPokemon.contextManager.clear(BattleContext.Type.VOLATILE, BattleContext.Type.BOOST, BattleContext.Type.UNBOOST)
                    battle.majorBattleActions[oldPokemon.uuid] = message
                }
                battle.majorBattleActions[pokemon.uuid] = message

                setOf(
                    BattleDispatch {
                        if (entity != null) {
                            this.createEntitySwitch(battle, actor, entity, pnx, activePokemon, pokemon)
                        } else {
                            this.createNonEntitySwitch(battle, actor, pnx, activePokemon, pokemon)
                        }
                    }
                )
            }
        }
    }

    private fun createEntitySwitch(battle: PokemonBattle, actor: BattleActor, entity: LivingEntity, pnx: String, activePokemon: ActiveBattlePokemon, newPokemon: BattlePokemon): DispatchResult {
        val pokemonEntity = activePokemon.battlePokemon?.entity
        // If we can't find the entity for some reason then we're going to skip the recall animation
        val sendOutFuture = CompletableFuture<Unit>()
        (pokemonEntity?.recallWithAnimation() ?: CompletableFuture.completedFuture(Unit)).thenApply {
            // Queue actual swap and send-in after the animation has ended
            actor.pokemonList.swap(actor.activePokemon.indexOf(activePokemon), actor.pokemonList.indexOf(newPokemon))
            activePokemon.battlePokemon = newPokemon
            battle.sendSidedUpdate(actor, BattleSwitchPokemonPacket(pnx, newPokemon, true), BattleSwitchPokemonPacket(pnx, newPokemon, false))
            if (newPokemon.entity != null) {
                sendOutFuture.complete(Unit)
            } else {
                val lastPosition = activePokemon.position
                // Send out at previous Pokémon's location if it is known, otherwise actor location
                val world = lastPosition?.first ?: entity.world as ServerWorld
                val pos = lastPosition?.second ?: entity.pos
                newPokemon.effectedPokemon.sendOutWithAnimation(
                    source = entity,
                    battleId = battle.battleId,
                    level = world,
                    position = pos
                ).thenAccept { sendOutFuture.complete(Unit) }
            }
        }

        return UntilDispatch { sendOutFuture.isDone }
    }

    private fun createNonEntitySwitch(battle: PokemonBattle, actor: BattleActor, pnx: String, activePokemon: ActiveBattlePokemon, newPokemon: BattlePokemon): DispatchResult {
        actor.pokemonList.swap(actor.activePokemon.indexOf(activePokemon), actor.pokemonList.indexOf(newPokemon))
        activePokemon.battlePokemon = newPokemon
        battle.sendSidedUpdate(actor, BattleSwitchPokemonPacket(pnx, newPokemon, true), BattleSwitchPokemonPacket(pnx, newPokemon, false))
        return WaitDispatch(1.5F)
    }

    fun handleDamageInstruction(battle: PokemonBattle, actor: BattleActor, publicMessage: String, privateMessage: String) {
        val pnx = publicMessage.split("|")[2].split(":")[0]
        val battleMessage = BattleMessage(privateMessage)
        val battlePokemon = battleMessage.getBattlePokemon(0, battle) ?: return
        if (battleMessage.optionalArgument("from")?.equals("recoil", true) == true) {
            battlePokemon.effectedPokemon.let { pokemon ->
                val recoilProgress = RecoilEvolutionProgress()
                // Lazy cheat to see if it's necessary to use this
                if (recoilProgress.shouldKeep(pokemon)) {
                    val progress = pokemon.evolutionProxy.current().progressFirstOrCreate({ it is RecoilEvolutionProgress }) { recoilProgress }
                    val newPercentage = battleMessage.argumentAt(1)?.split("/")?.getOrNull(0)?.toIntOrNull() ?: 0
                    val newHealth = (pokemon.hp * (newPercentage / 100.0)).roundToInt()
                    val difference = pokemon.currentHealth - newHealth
                    if (difference > 0) {
                        progress.updateProgress(RecoilEvolutionProgress.Progress(progress.currentProgress().recoil + difference))
                    }
                }
            }
        }
        val newHealth = privateMessage.split("|")[3].split(" ")[0]

        battle.dispatch {
            val newHealthRatio: Float
            val remainingHealth = newHealth.split("/")[0].toInt()

            when (battleMessage.optionalArgument("from")) {
                "confusion" -> battle.broadcastChatMessage(battleLang("hurt.confusion", battlePokemon.getName()))
                "move: Wrap" -> battle.broadcastChatMessage(battleLang("hurt.wrap", battlePokemon.getName()))
                "Leech Seed" -> battle.broadcastChatMessage(battleLang("hurt.leechseed", battlePokemon.getName()))
            }

            if (newHealth == "0") {
                newHealthRatio = 0F
                battle.dispatch {
                    battlePokemon.effectedPokemon.currentHealth = 0
                    battlePokemon.sendUpdate()
                    GO
                }
            } else {
                val maxHealth = newHealth.split("/")[1].toInt()
                val difference = maxHealth - remainingHealth
                newHealthRatio = remainingHealth.toFloat() / maxHealth
                battle.dispatch {
                    battlePokemon.effectedPokemon.currentHealth = remainingHealth
                    if (difference > 0) {
                        battlePokemon.effectedPokemon.let { pokemon ->
                            val damageProgress = DamageTakenEvolutionProgress()
                            // Lazy cheat to see if it's necessary to use this
                            if (damageProgress.shouldKeep(pokemon)) {
                                val progress = pokemon.evolutionProxy.current().progressFirstOrCreate({ it is DamageTakenEvolutionProgress }) { damageProgress }
                                progress.updateProgress(DamageTakenEvolutionProgress.Progress(progress.currentProgress().amount + difference))
                            }
                        }
                    }
                    battlePokemon.sendUpdate()
                    GO
                }
            }
            battle.sendSidedUpdate(actor, BattleHealthChangePacket(pnx, remainingHealth.toFloat()), BattleHealthChangePacket(pnx, newHealthRatio))
            battle.minorBattleActions[battlePokemon.uuid] = battleMessage
            WaitDispatch(1F)
        }
    }

    fun handleDragInstruction(battle: PokemonBattle, actor: BattleActor, publicMessage: String, privateMessage: String) {
        battle.dispatchGo {
            val message = BattleMessage(publicMessage)
            val (pnx, pokemonID) = message.pnxAndUuid(0) ?: return@dispatchGo
            val (_, activePokemon) = battle.getActorAndActiveSlotFromPNX(pnx)
            val pokemon = battle.getBattlePokemon(pnx, pokemonID)

            battle.broadcastChatMessage(battleLang("dragged_out", pokemon.getName()))
            activePokemon.battlePokemon?.let { oldPokemon ->
                oldPokemon.contextManager.clear(BattleContext.Type.VOLATILE, BattleContext.Type.BOOST, BattleContext.Type.UNBOOST)
                battle.majorBattleActions[oldPokemon.uuid] = message
            }
            battle.majorBattleActions[pokemon.uuid] = message

            val entity = if (actor is EntityBackedBattleActor<*>) actor.entity else null
            battle.dispatch {
                if (entity != null) {
                    this.createEntitySwitch(battle, actor, entity, pnx, activePokemon, pokemon)
                } else {
                    this.createNonEntitySwitch(battle, actor, pnx, activePokemon, pokemon)
                }
            }
        }

    }

    // |-hitcount|POKEMON|NUM
    fun handleHitCountInstruction(battle: PokemonBattle, rawMessage: String, remainingLines: MutableList<String>) {
        battle.dispatchGo {
            val message = BattleMessage(rawMessage)
            val battlePokemon = message.getBattlePokemon(0, battle) ?: return@dispatchGo
            val hitCount = message.argumentAt(1)?.toIntOrNull() ?: return@dispatchGo
            val lang = if (hitCount == 1) battleLang("hit_count_singular") else battleLang("hit_count", hitCount)
            battle.minorBattleActions[battlePokemon.uuid] = message
            battle.broadcastChatMessage(lang)
        }
    }


    fun handleItemInstruction(battle: PokemonBattle, baseMessage: String, remainingLines: MutableList<String>) {
        battle.dispatchGo {
            val battleMessage = BattleMessage(baseMessage)
            val battlePokemon = battleMessage.getBattlePokemon(0, battle) ?: return@dispatchGo
            battlePokemon.heldItemManager.handleStartInstruction(battlePokemon, battle, battleMessage)
            battle.minorBattleActions[battlePokemon.uuid] = battleMessage
            battlePokemon.contextManager.add(getContextFromAction(battleMessage, BattleContext.Type.ITEM, battle))
        }
    }

    fun handleEndItemInstruction(battle: PokemonBattle, baseMessage: String, remainingLines: MutableList<String>) {
        battle.dispatchGo {
            val battleMessage = BattleMessage(baseMessage)
            val battlePokemon = battleMessage.getBattlePokemon(0, battle) ?: return@dispatchGo
            val item = battleMessage.effectAt(1) ?: return@dispatchGo
            battlePokemon.heldItemManager.handleEndInstruction(battlePokemon, battle, battleMessage)
            battle.minorBattleActions[battlePokemon.uuid] = battleMessage
            battlePokemon.contextManager.remove(item.id, BattleContext.Type.ITEM)
        }
    }

    private fun handleHealInstruction(battle: PokemonBattle, actor: BattleActor, rawPublic: String, rawPrivate: String) {
        battle.dispatchWaiting {
            val publicMessage = BattleMessage(rawPublic)
            val privateMessage = BattleMessage(rawPrivate)
            val pnx = privateMessage.argumentAt(0)?.substring(0, 3) ?: return@dispatchWaiting
            val battlePokemon = privateMessage.actorAndActivePokemon(0, battle)?.second?.battlePokemon ?: return@dispatchWaiting
            val rawHpAndStatus = privateMessage.argumentAt(1)?.split(" ") ?: return@dispatchWaiting
            val rawHpRatio = rawHpAndStatus.getOrNull(0) ?: return@dispatchWaiting
            val newHealth = rawHpRatio.split("/").getOrNull(0)?.toIntOrNull() ?: return@dispatchWaiting
            val newHealthRatio = publicMessage.argumentAt(1)?.split("/")?.getOrNull(0)?.toFloatOrNull()?.times(0.01F) ?: return@dispatchWaiting
            battle.sendSidedUpdate(actor, BattleHealthChangePacket(pnx, newHealth.toFloat()), BattleHealthChangePacket(pnx, newHealthRatio))
            val silent = privateMessage.hasOptionalArgument("silent")
            if (!silent) {
                val message: Text = when {
                    privateMessage.hasOptionalArgument("zeffect") -> battleLang("heal.z_effect", battlePokemon.getName())
                    privateMessage.hasOptionalArgument("wisher") -> {
                        val name = privateMessage.optionalArgument("wisher")!!
                        val showdownId = name.lowercase().replace(ShowdownIdentifiable.REGEX, "")
                        val wisher = actor.pokemonList.firstOrNull { it.effectedPokemon.showdownId() == showdownId }
                        // If no Pokémon is found this is a nickname
                        battleLang("heal.wish", wisher?.getName() ?: actor.nameOwned(name))
                    }
                    privateMessage.optionalArgument("from") == "drain" -> {
                        val drained = privateMessage.actorAndActivePokemonFromOptional(battle, "of")?.second?.battlePokemon ?: return@dispatchWaiting
                        battleLang("heal.drain", drained.getName())
                    }
                    privateMessage.hasOptionalArgument("from") -> {
                        val effect = privateMessage.effect("from") ?: return@dispatchWaiting
                        when (effect.id) {
                            "healingwish" -> battleLang("heal.healing_wish", battlePokemon.getName())
                            "lunardance" -> battleLang("heal.lunar_dance", battlePokemon.getName())
                            "revivalblessing" -> battleLang("heal.revival_blessing", battlePokemon.getName())
                            "aquaring" -> battleLang("heal.aqua_ring", battlePokemon.getName())
                            "ingrain" -> battleLang("heal.ingrain", battlePokemon.getName())
                            "grassyterrain" -> battleLang("heal.grassy_terrain", battlePokemon.getName())
                            "leftovers" -> battleLang("heal.leftovers", battlePokemon.getName())
                            "raindish" -> battleLang("heal.rain_dish", battlePokemon.getName())
                            else -> battle.createUnimplementedSplit(publicMessage, privateMessage)
                        }
                    }
                    else -> battleLang("heal.generic", battlePokemon.getName())
                }
                battle.broadcastChatMessage(message)
                battle.minorBattleActions[battlePokemon.uuid] = privateMessage
            }
            battlePokemon.effectedPokemon.currentHealth = newHealth
            // This part is not always present
            val rawStatus = rawHpAndStatus.getOrNull(1) ?: return@dispatchWaiting
            val status = Statuses.getStatus(rawStatus) ?: return@dispatchWaiting
            if (status is PersistentStatus) {
                battlePokemon.effectedPokemon.applyStatus(status)
                battle.sendUpdate(BattlePersistentStatusPacket(pnx, status))
                if (!silent) {
                    status.applyMessage.let { battle.broadcastChatMessage(it.asTranslated(battlePokemon.getName())) }
                }
            }
        }
    }

    private fun handleSetHpInstructions(battle: PokemonBattle, actor: BattleActor, rawPublic: String, rawPrivate: String){
        battle.dispatchWaiting {
            val publicMessage = BattleMessage(rawPublic)
            val privateMessage = BattleMessage(rawPrivate)
            val pnx = privateMessage.argumentAt(0)?.substring(0, 3) ?: return@dispatchWaiting
            val flatHp = privateMessage.argumentAt(1)?.split("/")?.getOrNull(0)?.toFloatOrNull() ?: return@dispatchWaiting
            val ratioHp = publicMessage.argumentAt(1)?.split("/")?.getOrNull(0)?.toFloatOrNull()?.times(0.01F) ?: return@dispatchWaiting
            val battlePokemon = privateMessage.actorAndActivePokemon(0, battle)?.second?.battlePokemon ?: return@dispatchWaiting
            battlePokemon.effectedPokemon.currentHealth = flatHp.roundToInt()
            battle.sendSidedUpdate(actor, BattleHealthChangePacket(pnx, flatHp), BattleHealthChangePacket(pnx, ratioHp))
            // It doesn't matter which we check when silent both have it
            if (publicMessage.hasOptionalArgument("silent")) {
                return@dispatchWaiting
            }
            val effect = publicMessage.effect() ?: return@dispatchWaiting
            val lang: Text = when (effect.id) {
                "painsplit" -> battleLang("set_hp.pain_split")
                else -> battle.createUnimplemented(publicMessage)
            }
            battle.broadcastChatMessage(lang)
            battle.minorBattleActions[battlePokemon.uuid] = publicMessage
        }
    }

    private fun handleClearAllBoostInstructions(battle: PokemonBattle, rawMessage: String, remainingLines: MutableList<String>) {
        battle.dispatchGo {
            battle.activePokemon.forEach {
                it.battlePokemon?.contextManager?.clear(BattleContext.Type.VOLATILE, BattleContext.Type.BOOST, BattleContext.Type.UNBOOST)
            }
            battle.broadcastChatMessage(battleLang("clearallboost"))
        }
    }

    // Used for things that are only meant for visual information we don't have
    private fun handleSilently(battle: PokemonBattle, baseMessage: String, remainingLines: MutableList<String>) {
        battle.dispatchGo {  }
    }

    private fun getContextFromFaint(pokemon: BattlePokemon, battle: PokemonBattle): BattleContext {
        val cause = battle.minorBattleActions[pokemon.uuid] ?: lastCauser[battle.battleId] ?: return MissingContext()
        val side = pokemon.actor.getSide()

        return when (cause.id) {
            "-damage", "move" -> {
                // damage from abilities
                cause.effect("of")?.let {
                    val effectID = cause.effect()?.id ?: it.id
                    val originPnx = cause.optionalArgument("of")!!.substringBefore(':')
                    val uuid = cause.optionalArgument("of")!!.substringAfter(':').trim()
                    val origin = battle.getBattlePokemon(originPnx, uuid)
                    BasicContext(effectID, battle.turn, BattleContext.Type.FAINT, origin)
                } ?:
                // damage from weather, statuses, entry hazards
                cause.effect()?.let { effect ->
                    val damagingContexts = BattleContext.Type.values().filter { it.damaging }
                    val contextBuckets = damagingContexts.map { pokemon.contextManager.get(it) ?: side.contextManager.get(it)
                        ?: battle.contextManager.get(it) }
                    ContextManager.scoop(effect.id, *contextBuckets.toTypedArray())
                } ?:
                // damage from moves and suicide
                lastCauser[battle.battleId]?.let {
                    val move = it.effectAt(1)!!.id
                    val origin = it.getBattlePokemon(0, battle)
                    BasicContext(move, battle.turn, BattleContext.Type.FAINT, origin)
                } ?:
                MissingContext()
            }
            // perish song
            "-start" -> {
                cause.effectAt(1)?.let {
                    val effectID = if (it.id.contains("perish")) "perishsong" else it.id
                    ContextManager.scoop(effectID, pokemon.contextManager.get(BattleContext.Type.VOLATILE))
                } ?:
                MissingContext()
            }
            // destiny bond
            "-activate" -> {
                cause.effectAt(1)?.let {
                    val origin = cause.getBattlePokemon(0, battle)
                    BasicContext(it.id, battle.turn, BattleContext.Type.FAINT, origin)
                } ?:
                MissingContext()
            }

            else -> MissingContext()
        }
    }

    private fun getContextFromAction(message: BattleMessage, type: BattleContext.Type, battle: PokemonBattle): BattleContext {
        // |-action|POKEMON|EFFECT|[from]EFFECT|[of]POKEMON or |-action|EFFECT|[from]EFFECT|[of]POKEMON
        return message.actorAndActivePokemonFromOptional(battle)?.let {
            // ex: |-item|p2a: ###|Black Sludge|[from] ability: Pickpocket|[of] p1a: ###
            val effectID = message.effectAt(1)?.id ?: message.effectAt(0)?.id ?: return@let MissingContext()
            BasicContext(effectID, battle.turn, type, it.second.battlePokemon)
        } ?:
        // |-action|POKEMON|EFFECT| (caused by a move or another action)
        message.actorAndActivePokemon(0, battle)?.let {
            // ex: |-status|p2a: ###|par -> |move|p1a: ###|Glare|p2a: ###
            // ex: |-unboost|p1a: ###|atk|1 -> |-ability|p2a: ###|Intimidate|boost
            val effectID = message.effectAt(1)?.id ?: return@let MissingContext()
            val origin = lastCauser[battle.battleId]?.getBattlePokemon(0, battle) ?: return@let MissingContext()
            BasicContext(effectID, battle.turn, type, origin)
        } ?:
        // |-action|EFFECT
        lastCauser[battle.battleId]?.let {
            // ex: |-sidestart|p2: ###|move: Toxic Spikes -> |-activate|p1a: ###|ability: Toxic Debris
            // ex: |-weather|Sandstorm -> |move|p1a: ###|Sandstorm|p1a: ###
            val effectID = message.effectAt(1)?.id ?: message.effectAt(0)?.id ?: return@let MissingContext()
            BasicContext(effectID, battle.turn, type, it.getBattlePokemon(0, battle))
        } ?:
        MissingContext()
    }
}