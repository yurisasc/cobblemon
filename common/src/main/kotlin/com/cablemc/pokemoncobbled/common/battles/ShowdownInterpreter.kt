package com.cablemc.pokemoncobbled.common.battles

import com.cablemc.pokemoncobbled.common.CobbledNetwork
import com.cablemc.pokemoncobbled.common.PokemonCobbled.LOGGER
import com.cablemc.pokemoncobbled.common.api.battles.model.PokemonBattle
import com.cablemc.pokemoncobbled.common.api.battles.model.actor.BattleActor
import com.cablemc.pokemoncobbled.common.api.text.*
import com.cablemc.pokemoncobbled.common.battles.actor.PlayerBattleActor
import com.cablemc.pokemoncobbled.common.battles.dispatch.GO
import com.cablemc.pokemoncobbled.common.battles.runner.ShowdownConnection
import com.cablemc.pokemoncobbled.common.net.messages.client.battle.BattleFaintPacket
import com.cablemc.pokemoncobbled.common.net.messages.client.battle.BattleInitializePacket
import com.cablemc.pokemoncobbled.common.net.messages.client.battle.BattleMakeChoicePacket
import com.cablemc.pokemoncobbled.common.net.messages.client.battle.BattleQueueRequestPacket
import com.cablemc.pokemoncobbled.common.util.battleLang
import com.cablemc.pokemoncobbled.common.util.getPlayer
import net.minecraft.text.LiteralText
import net.minecraft.util.Formatting
import java.util.*

object ShowdownInterpreter {
    private val updateInstructions = mutableMapOf<String, (PokemonBattle, String) -> Unit>()
    private val sideUpdateInstructions = mutableMapOf<String, (PokemonBattle, BattleActor, String) -> Unit>()
    private val splitUpdateInstructions = mutableMapOf<String, (PokemonBattle, BattleActor, String, String) -> Unit>()

    init {
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
        sideUpdateInstructions["|request|"] = this::handleRequestInstruction
        splitUpdateInstructions["|switch|"] = this::handleSwitchInstruction
        splitUpdateInstructions["|-damage|"] = this::handleDamageInstruction
        splitUpdateInstructions["|drag|"] = this::handleDragInstruction
    }

    fun interpretMessage(message: String) {
        // Check key map and use function if matching
        val battleId = message.split(ShowdownConnection.LINE_START)[0]
        val rawMessage = message.replace(battleId + ShowdownConnection.LINE_START, "")

        val battle = BattleRegistry.getBattle(UUID.fromString(battleId))

        if (battle == null) {
            LOGGER.info("No battle could be found with the id: $battleId")
            return
        }

        battle.showdownMessages.add(message)
        interpret(battle, rawMessage)
    }

    fun interpret(battle: PokemonBattle, rawMessage: String) {
        battle.log()
        battle.log(rawMessage)
        battle.log()

        val lines = rawMessage.split("\n")
        if (lines[0] == "update") {
            var i = 1;
            while (i < lines.size) {
                val line = lines[i]

                // Split blocks have a public and private message below
                if (line.startsWith("|split|")) {
                    val showdownId = line.split("|split|")[1]
                    val targetActor = battle.getActor(showdownId)

                    if (targetActor == null) {
                        battle.log("No actor could be found with the showdown id: $showdownId")
                        return
                    }

                    for (instruction in splitUpdateInstructions.entries) {
                        if (lines[i+1].startsWith(instruction.key)) {
                            instruction.value(battle, targetActor, lines[i+2], lines[i+1])
                        }
                    }

                    i += 2;
                } else {
                    for (instruction in updateInstructions.entries) {
                        if (line.startsWith(instruction.key)) {
                            instruction.value(battle, line)
                        }
                    }
                    i++
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
    }

    /**
     * Format:
     * |player|PLAYER|USERNAME|AVATAR|RATING
     *
     * Definitions:
     * PLAYER is p1 or p2 unless 4 player battle which adds p3 and p4
     * USERNAME is the cobbled battle actors uuid
     * AVATAR is unused currently
     * RATING is unused currently
     */
    private fun handlePlayerInstruction(battle: PokemonBattle, message: String) {
//        battle.log("Player Instruction")
    }

    /**
     * Format:
     * |teamsize|PLAYER|NUMBER
     *
     * Definitions:
     * PLAYER is p1 or p2 unless 4 player battle which adds p3 and p4
     * NUMBER is number of Pokemon your opponent starts with for team preview.
     */
    private fun handleTeamSizeInstruction(battle: PokemonBattle, message: String) {
//        battle.log("Team Size Instruction")
    }

    /**
     * Format:
     * |gametype|GAMETYPE
     *
     * Definitions:
     * GAMETYPE is singles, doubles, triples, multi, and or freeforall
     */
    private fun handleGameTypeInstruction(battle: PokemonBattle, message: String) {
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
    private fun handleGenInstruction(battle: PokemonBattle, message: String) {
        battle.log("Gen Instruction: $message")
    }

    /**
     * Format:
     * |tier|FORMATNAME
     *
     * Definitions:
     * FORMATNAME is the name of the format being played.
     */
    private fun handleTierInstruction(battle: PokemonBattle, message: String) {
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
    private fun handleRatedInstruction(battle: PokemonBattle, message: String) {
//        battle.log("Rated Instruction")
    }

    /**
     * Format:
     * |rule|RULE: DESCRIPTION
     *
     * Definitions:
     * RULE is a rule and its description
     */
    private fun handleRuleInstruction(battle: PokemonBattle, message: String) {
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
    private fun handleClearPokeInstruction(battle: PokemonBattle, message: String) {
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
    private fun handlePokeInstruction(battle: PokemonBattle, message: String) {
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
    private fun handleTeamPreviewInstruction(battle: PokemonBattle, message: String) {
        battle.log("Start Team Preview Instruction: $message")
    }

    /**
     * Format:
     * |start
     *
     * Indicates that the game has started.
     */
    private fun handleStartInstruction(battle: PokemonBattle, message: String) {
        battle.log("Start Instruction: $message")
    }

    /**
     * Format:
     * |turn|NUMBER
     *
     * It is now turn NUMBER.
     */
    private fun handleTurnInstruction(battle: PokemonBattle, message: String) {
        if (!battle.started) {
            battle.started = true
            val players = battle.actors.mapNotNull { it.uuid.getPlayer() }
            if (players.isNotEmpty()) {
                val initializePacket = BattleInitializePacket(battle)
                players.forEach { CobbledNetwork.sendToPlayer(it, initializePacket) }
            }
        }

        // TODO maybe tell the client that the turn number has changed
        val turnNumber = message.split("|turn|")[1].toInt()

        battle.dispatch {
            battle.sendToActors(BattleMakeChoicePacket())
            GO
        }

        battle.broadcastChatMessage("".text())
        battle.broadcastChatMessage(">>".aqua() + " It is now turn ${message.split("|turn|")[1]}".aqua())
        battle.broadcastChatMessage("".text())
        battle.turn()
    }

    private fun handleUpkeepInstruction(battle: PokemonBattle, message: String) {
        battle.actors.forEach { it.upkeep() }
    }

    /**
     * Format:
     * |faint|POKEMON
     *
     * The Pokémon POKEMON has fainted.
     */
    private fun handleFaintInstruction(battle: PokemonBattle, message: String) {
        val pnx = message.split("|faint|")[1].substring(0, 3)
        val (_, pokemon) = battle.getActorAndActiveSlotFromPNX(pnx)
        battle.dispatch {
            battle.sendUpdate(BattleFaintPacket(pnx, battleLang("fainted", pokemon.battlePokemon?.getName() ?: "ALREADY DEAD")))
            pokemon.battlePokemon?.effectedPokemon?.currentHealth = 0
            pokemon.battlePokemon = null
            GO
        }
//        battle.broadcastChatMessage("".text())
//        battle.broadcastChatMessage(">> ".red() + battleLang("fainted", pokemon.battlePokemon?.getName() ?: "ALREADY DEAD".red()).gold())
//        battle.broadcastChatMessage("".text())
    }

    private fun handleWinInstruction(battle: PokemonBattle, message: String) {
        val ids = message.split("|win|")[1].split("&").map { it.trim() }
        val winners = ids.map { battle.getActor(UUID.fromString(it))!!.getName() }.reduce { acc, next -> acc + " & " + next }

        battle.broadcastChatMessage(">> ".gold() + battleLang("win", winners).gold())

        battle.end()
        BattleRegistry.closeBattle(battle)
    }

    // |move|p1a: Charizard|Tackle|p2a: Magikarp
    private fun handleMoveInstruction(battle: PokemonBattle, message: String) {
        val editMessaged = message.replace("|move|", "")

        val userPNX = editMessaged.split("|")[0].split(":")[0].trim()
        val (_, userPokemon) = battle.getActorAndActiveSlotFromPNX(userPNX)
        val move = editMessaged.split("|")[1].split("|")[0]
        val hasTarget = editMessaged.split("|").size == 3 && editMessaged.split("|")[2].isNotEmpty()
        if (hasTarget) {
            val targetPNX = editMessaged.split("|")[2].split(":")[0]
            val (_, targetPokemon) = battle.getActorAndActiveSlotFromPNX(targetPNX)
            battle.broadcastChatMessage(">> ".yellow() + battleLang(
                key = "used_move_on",
                userPokemon.battlePokemon?.getName() ?: "ERROR".red(),
                move,
                targetPokemon.battlePokemon?.getName() ?: "ERROR".red()
            ))
        } else {
            battle.broadcastChatMessage(">> ".yellow() + battleLang(
                key = "used_move_on",
                userPokemon.battlePokemon?.getName() ?: "ERROR".red(),
                move
            ))
        }
    }

    private fun handleCantInstruction(battle: PokemonBattle, message: String) {
        val editMessaged = message.replace("|cant|", "")

        val pnx = editMessaged.split("|")[0].split(":")[0]
        val (actor, pokemon) = battle.getActorAndActiveSlotFromPNX(pnx)
        val action = editMessaged.split("|")[1]
        val actionText = if (action == "flinch") "flinched" else action

        battle.broadcastChatMessage(">> ".red() + (pokemon.battlePokemon?.getName() ?: "DEAD".text()) + " has $actionText".red())
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
        battleActor.sendUpdate(BattleQueueRequestPacket(request))
        battleActor.request = request
    }

    private fun handleSwitchInstruction(battle: PokemonBattle, battleActor: BattleActor, publicMessage: String, privateMessage: String) {
        val pnx = publicMessage.split("|")[2].split(":")[0]
        val (actor, activePokemon) = battle.getActorAndActiveSlotFromPNX(pnx)
        val uuid = UUID.fromString(publicMessage.split("|")[2].split(":")[1].trim())
        val pokemon = actor.pokemonList.find { it.uuid == uuid } ?: throw IllegalStateException("Unable to find ${actor.showdownId}'s Pokemon with UUID: $uuid")
        activePokemon.battlePokemon = pokemon
    }

    fun handleDamageInstruction(battle: PokemonBattle, actor: BattleActor, publicMessage: String, privateMessage: String) {
        val (_, activePokemon) = battle.getActorAndActiveSlotFromPNX(publicMessage.split("|")[2].split(":")[0])
        val newHealth = privateMessage.split("|")[3].split(" ")[0]
        if (newHealth == "0") {
            activePokemon.battlePokemon?.effectedPokemon?.currentHealth = 0
        } else {
            val remainingHealth = newHealth.split("/")[0].toInt()
            activePokemon.battlePokemon?.effectedPokemon?.currentHealth = remainingHealth
        }
    }

    fun handleDragInstruction(battle: PokemonBattle, actor: BattleActor, publicMessage: String, privateMessage: String) {
        val (_, activePokemon) = battle.getActorAndActiveSlotFromPNX(publicMessage.split("|")[2].split(":")[0])
        val uuid = UUID.fromString(publicMessage.split("|")[3].split(",")[1].trim())
        val pokemon = actor.pokemonList.find { it.uuid == uuid } ?: throw IllegalStateException("Unable to find ${actor.showdownId}'s Pokemon with UUID: $uuid")
        battle.broadcastChatMessage(">> ".yellow() + battleLang("dragged_out", pokemon.getName()).yellow())
        activePokemon.battlePokemon = pokemon
    }
}