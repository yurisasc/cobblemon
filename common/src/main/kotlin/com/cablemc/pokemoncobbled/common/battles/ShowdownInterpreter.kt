package com.cablemc.pokemoncobbled.common.battles

import com.cablemc.pokemoncobbled.common.PokemonCobbled.LOGGER
import com.cablemc.pokemoncobbled.common.api.battles.model.PokemonBattle
import com.cablemc.pokemoncobbled.common.api.battles.model.actor.BattleActor
import com.cablemc.pokemoncobbled.common.battles.runner.ShowdownConnection
import java.util.UUID

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
        updateInstructions["|faint|"] = this::handleFaintInstruction
        updateInstructions["|win|"] = this::handleWinInstruction
        updateInstructions["|move|"] = this::handleMoveInstruction
        updateInstructions["|cant|"] = this::handleCantInstruction

        sideUpdateInstructions["|request|"] = this::handleRequestInstruction

        splitUpdateInstructions["|switch|"] = this::handleSwitchInstruction
    }

    fun interpretMessage(message: String) {
        // Check key map and use function if matching
        val battleId = message.split(ShowdownConnection.LINE_START)[0]
        val rawMessage = message.replace(battleId + ShowdownConnection.LINE_START, "")
        val lines = rawMessage.split("\n")

        val battle = BattleRegistry.getBattle(UUID.fromString(battleId))

        if (battle == null) {
            LOGGER.info("No battle could be found with the id: $battleId")
            return
        }

        if (lines[0] == "update") {
            println("WE HAVE UPDATE FOR $battleId")

            var i = 1;
            while (i < lines.size) {
                val line = lines[i]

                // Split blocks have a public and private message below
                if (line.startsWith("|split|")) {
                    val showdownId = line.split("|split|")[1]
                    val targetActor = battle.getActor(showdownId)

                    if (targetActor == null) {
                        LOGGER.info("No actor could be found with the showdown id: $showdownId")
                        return
                    }

                    for (instruction in splitUpdateInstructions.entries) {
                        if (line.startsWith(instruction.key)) {
                            instruction.value(battle, targetActor, lines[i+1], lines[i+2])
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
        }
        else if (lines[0] == "sideupdate") {
            println("WE HAVE SIDE UPDATE FOR $battleId")
            val showdownId = lines[1]
            val targetActor = battle.getActor(showdownId)
            val line = lines[2]

            if (targetActor == null) {
                LOGGER.info("No actor could be found with the showdown id: $showdownId")
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

    }

    /**
     * Format:
     * |gametype|GAMETYPE
     *
     * Definitions:
     * GAMETYPE is singles, doubles, triples, multi, and or freeforall
     */
    private fun handleGameTypeInstruction(battle: PokemonBattle, message: String) {

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

    }

    /**
     * Format:
     * |tier|FORMATNAME
     *
     * Definitions:
     * FORMATNAME is the name of the format being played.
     */
    private fun handleTierInstruction(battle: PokemonBattle, message: String) {

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

    }

    /**
     * Format:
     * |rule|RULE: DESCRIPTION
     *
     * Definitions:
     * RULE is a rule and its description
     */
    private fun handleRuleInstruction(battle: PokemonBattle, message: String) {

    }

    /**
     * Format:
     * |clearpoke
     *
     * Marks the start of Team Preview
     */
    private fun handleClearPokeInstruction(battle: PokemonBattle, message: String) {

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
    private fun handlePokeInstruction(battle: PokemonBattle, message: String) {

    }

    /**
     * Format:
     * |teampreview indicates team preview is over
     */
    private fun handleTeamPreviewInstruction(battle: PokemonBattle, message: String) {
        LOGGER.info("Start Team Preview Instruction")
    }

    /**
     * Format:
     * |start
     *
     * Indicates that the game has started.
     */
    private fun handleStartInstruction(battle: PokemonBattle, message: String) {

    }

    /**
     * Format:
     * |turn|NUMBER
     *
     * It is now turn NUMBER.
     */
    private fun handleTurnInstruction(battle: PokemonBattle, message: String) {

    }

    /**
     * Format:
     * |faint|POKEMON
     *
     * The Pokémon POKEMON has fainted.
     */
    private fun handleFaintInstruction(battle: PokemonBattle, message: String) {

    }

    /**
     * Format:
     * |win|GAMEUUID
     */
    private fun handleWinInstruction(battle: PokemonBattle, message: String) {

    }

    // |move|p1a: Charizard|Tackle|p2a: Magikarp
    private fun handleMoveInstruction(battle: PokemonBattle, message: String) {

    }

    private fun handleCantInstruction(battle: PokemonBattle, message: String) {

    }

    /**
     * Format:
     * |request|REQUEST
     *
     * The protocol message to tell you that it's time for you to make a decision is:
     */
    private fun handleRequestInstruction(battle: PokemonBattle, battleActor: BattleActor, message: String) {

    }

    private fun handleSwitchInstruction(battle: PokemonBattle, battleActor: BattleActor, publicMessage: String, privateMessage: String) {

    }

}