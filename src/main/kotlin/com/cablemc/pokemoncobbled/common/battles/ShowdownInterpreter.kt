package com.cablemc.pokemoncobbled.common.battles

import com.cablemc.pokemoncobbled.common.api.battles.model.Battle
import com.cablemc.pokemoncobbled.common.battles.runner.ShowdownConnection
import com.cablemc.pokemoncobbled.mod.PokemonCobbledMod
import java.util.*

object ShowdownInterpreter {

    private val updateInstructions = mutableMapOf<String, (Battle, String) -> Unit>()

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
        updateInstructions["|start"] = this::handleStartInstruction
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

            for (i in 1 until lines.size) {
                for(instruction in updateInstructions.entries) {
                    val message = lines[i]
                    if(message.startsWith(instruction.key)) {
                        instruction.value(battle, message)
                    }
                }
            }
        }
        else if(lines[0] == "sideupdate") {
            println("WE HAVE SIDE UPDATE FOR $battleId")
            // TODO: lines[1] is the player id
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

}