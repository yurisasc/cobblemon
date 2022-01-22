package com.cablemc.pokemoncobbled.common.battles

import com.cablemc.pokemoncobbled.common.api.battles.model.Battle
import com.cablemc.pokemoncobbled.common.api.battles.model.actor.AIBattleActor
import com.cablemc.pokemoncobbled.common.api.battles.model.actor.BattleActor
import com.cablemc.pokemoncobbled.common.api.scheduling.taskBuilder
import com.cablemc.pokemoncobbled.common.battles.actor.PlayerBattleActor
import com.cablemc.pokemoncobbled.common.battles.runner.ShowdownConnection
import com.cablemc.pokemoncobbled.mod.PokemonCobbledMod
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.TextComponent
import java.util.*
import kotlin.random.Random

object ShowdownInterpreter {

    private val updateInstructions = mutableMapOf<String, (Battle, String) -> Unit>()
    private val sideUpdateInstructions = mutableMapOf<String, (Battle, BattleActor, String) -> Unit>()
    private val splitUpdateInstructions = mutableMapOf<String, (Battle, BattleActor, String, String) -> Unit>()

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
        updateInstructions["|faint|"] = this::handleFaintInstruction
        updateInstructions["|win|"] = this::handleWinInstruction
        updateInstructions["|move|"] = this::handleMoveInstruction
        updateInstructions["|cant|"] = this::handleCantInstruction

        sideUpdateInstructions["|request|"] = this::handleRequestInstruction

        splitUpdateInstructions["|switch|"] = this::handleSwitchInstruction
    }

    fun interpretMessage(message: String) {
        // Check key map and use function if matching
        val battleId = message.split(ShowdownConnection.lineStarter)[0]
        val rawMessage = message.replace(battleId + ShowdownConnection.lineStarter, "")
        val lines = rawMessage.split("\n")

        val battle = BattleRegistry.getBattle(UUID.fromString(battleId))

        if(battle == null) {
            PokemonCobbledMod.LOGGER.info("No battle could be found with the id: $battleId")
            return
        }

        if(lines[0] == "update") {
            println("WE HAVE UPDATE FOR $battleId")

            var i = 1;
            while(i < lines.size) {
                val line = lines[i]

                // Split blocks have a public and private message below
                if(line.startsWith("|split|")) {
                    val showdownId = line.split("|split|")[1]
                    val targetActor = battle.getActor(showdownId)

                    if(targetActor == null) {
                        PokemonCobbledMod.LOGGER.info("No actor could be found with the showdown id: $showdownId")
                        return
                    }

                    for(instruction in splitUpdateInstructions.entries) {
                        if(line.startsWith(instruction.key)) {
                            instruction.value(battle, targetActor, lines[i+1], lines[i+2])
                        }
                    }

                    i += 2;
                } else {
                    for(instruction in updateInstructions.entries) {
                        if(line.startsWith(instruction.key)) {
                            instruction.value(battle, line)
                        }
                    }
                    i++
                }
            }
        }
        else if(lines[0] == "sideupdate") {
            println("WE HAVE SIDE UPDATE FOR $battleId")
            val showdownId = lines[1]
            val targetActor = battle.getActor(showdownId)
            val line = lines[2]

            if(targetActor == null) {
                PokemonCobbledMod.LOGGER.info("No actor could be found with the showdown id: $showdownId")
                return
            }

            for(instruction in sideUpdateInstructions.entries) {
                if(line.startsWith(instruction.key)) {
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
    private fun handlePlayerInstruction(battle: Battle, message: String) {
        PokemonCobbledMod.LOGGER.info("Player Instruction")
    }

    /**
     * Format:
     * |teamsize|PLAYER|NUMBER
     *
     * Definitions:
     * PLAYER is p1 or p2 unless 4 player battle which adds p3 and p4
     * NUMBER is number of Pokemon your opponent starts with for team preview.
     */
    private fun handleTeamSizeInstruction(battle: Battle, message: String) {
        PokemonCobbledMod.LOGGER.info("Team Size Instruction")
    }

    /**
     * Format:
     * |gametype|GAMETYPE
     *
     * Definitions:
     * GAMETYPE is singles, doubles, triples, multi, and or freeforall
     */
    private fun handleGameTypeInstruction(battle: Battle, message: String) {
        PokemonCobbledMod.LOGGER.info("Game Type Instruction")

        battle.broadcastChatMessage(TextComponent("${ChatFormatting.GOLD}${ChatFormatting.BOLD}Battle Type:"))

        val tierName = message.split("|gametype|")[1]
        var textComponent = TextComponent(" ${ChatFormatting.GRAY}$tierName")
        battle.broadcastChatMessage(textComponent)
        battle.broadcastChatMessage(TextComponent(""))
    }

    /**
     * Format:
     * |gen|GENNUM
     *
     * Definitions:
     * GENNUM is Generation number, from 1 to 7. Stadium counts as its respective gens;
     * Let's Go counts as 7, and modded formats count as whatever gen they were based on.
     */
    private fun handleGenInstruction(battle: Battle, message: String) {
        PokemonCobbledMod.LOGGER.info("Gen Instruction")
    }

    /**
     * Format:
     * |tier|FORMATNAME
     *
     * Definitions:
     * FORMATNAME is the name of the format being played.
     */
    private fun handleTierInstruction(battle: Battle, message: String) {
        PokemonCobbledMod.LOGGER.info("Tier Instruction")

        battle.broadcastChatMessage(TextComponent("${ChatFormatting.GOLD}${ChatFormatting.BOLD}Battle Tier:"))

        val tierName = message.split("|tier|")[1]
        var textComponent = TextComponent(" ${ChatFormatting.GRAY}$tierName")
        battle.broadcastChatMessage(textComponent)
        battle.broadcastChatMessage(TextComponent(""))
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
    private fun handleRatedInstruction(battle: Battle, message: String) {
        PokemonCobbledMod.LOGGER.info("Rated Instruction")
    }

    /**
     * Format:
     * |rule|RULE: DESCRIPTION
     *
     * Definitions:
     * RULE is a rule and its description
     */
    private fun handleRuleInstruction(battle: Battle, message: String) {
        PokemonCobbledMod.LOGGER.info("Rule Instruction")

        if(battle.announcingRules == false) {
            battle.announcingRules = true
            var textComponent = TextComponent("${ChatFormatting.GOLD}${ChatFormatting.BOLD}Battle Rules:")
            battle.broadcastChatMessage(textComponent)
        }

        val rule = message.split("|rule|")[1]
        var textComponent = TextComponent("${ChatFormatting.GRAY} - ${rule}")
        battle.broadcastChatMessage(textComponent)
    }

    /**
     * Format:
     * |clearpoke
     *
     * Marks the start of Team Preview
     */
    private fun handleClearPokeInstruction(battle: Battle, message: String) {
        PokemonCobbledMod.LOGGER.info("Clear Poke Instruction")
    }

    /**
     * Format:
     * |poke|PLAYER|DETAILS|ITEM
     *
     * Declares a Pokemon for Team Preview.
     *
     * PLAYER is the player ID
     * DETAILS describes the pokemon
     * ITEM will be a item if the pokemon is holding an item or blank if it isn't
     */
    private fun handlePokeInstruction(battle: Battle, message: String) {
        PokemonCobbledMod.LOGGER.info("Poke Instruction")

        val args = message.split("|")
        val showdownId = args[2]
        val pokemon = args[3]

        val targetActor = battle.getActor(showdownId)

        if(targetActor == null) {
            PokemonCobbledMod.LOGGER.info("No actor could be found with the showdown id: $showdownId")
            return
        }

        if(targetActor is PlayerBattleActor) {
            if(targetActor.announcingPokemon == false) {
                battle.broadcastChatMessage(TextComponent(""))
                targetActor.announcingPokemon = true
                var textComponent = TextComponent("${ChatFormatting.GOLD}${ChatFormatting.BOLD}Your Team:")
                targetActor.sendMessage(textComponent)
            }

            var textComponent = TextComponent("${ChatFormatting.YELLOW} - ${pokemon}")
            battle.broadcastChatMessage(textComponent)
        }
    }

    /**
     * Format:
     * |teampreview indicates team preview is over
     */
    private fun handleTeamPreviewInstruction(battle: Battle, message: String) {
        PokemonCobbledMod.LOGGER.info("Start Team Preview Instruction")
    }

    /**
     * Format:
     * |start
     *
     * Indicates that the game has started.
     */
    private fun handleStartInstruction(battle: Battle, message: String) {
        PokemonCobbledMod.LOGGER.info("Start Instruction")
    }

    /**
     * Format:
     * |turn|NUMBER
     *
     * It is now turn NUMBER.
     */
    private fun handleTurnInstruction(battle: Battle, message: String) {
        battle.broadcastChatMessage(TextComponent(""))
        battle.broadcastChatMessage(TextComponent("${ChatFormatting.AQUA}" + ">>> ${ChatFormatting.BOLD}It is now turn " + message.split("|turn|")[1]))
        battle.broadcastChatMessage(TextComponent(""))
    }

    /**
     * Format:
     * |faint|POKEMON
     *
     * The Pokémon POKEMON has fainted.
     */
    private fun handleFaintInstruction(battle: Battle, message: String) {
        val player = message.split("|faint|")[1].substring(0, 2)
        val pokemon = message.split("|faint|")[1].split(" ")[1]
        val actor = battle.getActor(player)

        battle.broadcastChatMessage(TextComponent(""))
        battle.broadcastChatMessage(TextComponent("${ChatFormatting.RED}" + ">>> ${ChatFormatting.BOLD}${actor!!.getName()}'s $pokemon has fainted!"))
        battle.broadcastChatMessage(TextComponent(""))
    }

    private fun handleWinInstruction(battle: Battle, message: String) {
        val id = message.split("|win|")[1]
        val actor = battle.getActor(UUID.fromString(id))

        battle.broadcastChatMessage(TextComponent(""))
        battle.broadcastChatMessage(TextComponent("${ChatFormatting.GOLD}" + ">>> ${ChatFormatting.BOLD}${actor!!.getName()} has won the battle!!"))
        battle.broadcastChatMessage(TextComponent(""))

        BattleRegistry.closeBattle(battle)
    }

    // |move|p1a: Charizard|Tackle|p2a: Magikarp
    private fun handleMoveInstruction(battle: Battle, message: String) {
        var editMessaged = message.replace("|move|", "")

        val playerA = editMessaged.split("|")[0].substring(0, 2)
        val pokemonA = editMessaged.split("|")[0].split(" ")[1]
        val actorA = battle.getActor(playerA)

        val playerB = editMessaged.split("|")[2].substring(0, 2)
        val pokemonB = editMessaged.split("|")[2].split(" ")[1]
        val actorB = battle.getActor(playerB)

        val move = editMessaged.split("|")[1].split("|")[0]
        battle.broadcastChatMessage(TextComponent(""))
        battle.broadcastChatMessage(TextComponent("${ChatFormatting.YELLOW}" + ">>> ${ChatFormatting.BOLD}${actorA!!.getName()}'s $pokemonA has used $move on ${actorB!!.getName()}'s $pokemonB!!"))
    }

    private fun handleCantInstruction(battle: Battle, message: String) {
        var editMessaged = message.replace("|cant|", "")

        val playerA = editMessaged.split("|")[0].substring(0, 2)
        val pokemonA = editMessaged.split("|")[0].split(" ")[1]
        val actorA = battle.getActor(playerA)

        val action = editMessaged.split("|")[1]
        val actionText = if(action == "flinch") "flinched" else action

        battle.broadcastChatMessage(TextComponent("${ChatFormatting.RED}" + ">>> ${ChatFormatting.BOLD}${actorA!!.getName()}'s $pokemonA has $actionText"))
    }


        /**
     * Format:
     * |request|REQUEST
     *
     * The protocol message to tell you that it's time for you to make a decision is:
     */
    private fun handleRequestInstruction(battle: Battle, battleActor: BattleActor, message: String) {
        PokemonCobbledMod.LOGGER.info("Request Instruction")

        if(message.contains("teamPreview"))
            return

        // Parse Json message and update state info for actor
        val request: ShowdownActionRequest = BattleRegistry.gson.fromJson(message.split("|request|")[1], ShowdownActionRequest::class.java)

        // Then request decision
        if(battleActor is AIBattleActor) {
            //battleActor.battleAI.chooseMove(battle, battleActor, emptyList())
            battle.writeShowdownAction(">${battleActor.showdownId} move ${Random.nextInt(1, 5)}")
        } else if(battleActor is PlayerBattleActor) {
            // TODO: Ask them for a input choice

            // Force switch to toggle
            if(message.contains("forceSwitch")) {
                battleActor.sendMessage(TextComponent(""))
                battleActor.sendMessage(TextComponent("${ChatFormatting.GOLD}${ChatFormatting.BOLD}Switch your Pokemon! (/switch <1-3>)"))
            } else {
                taskBuilder().delay(1).execute {
                    battleActor.sendMessage(TextComponent("${ChatFormatting.GOLD}${ChatFormatting.BOLD}Pick A Move: (/move <1-4>)"))

                    var i = 1;
                    for(move in request.active[0].moves) {
                        battleActor.sendMessage(TextComponent("$i. ${move.move}"))
                        i++
                    }
                }.build()
            }
        }
    }

    private fun handleSwitchInstruction(battle: Battle, battleActor: BattleActor, publicMessage: String, privateMessage: String) {

    }

}