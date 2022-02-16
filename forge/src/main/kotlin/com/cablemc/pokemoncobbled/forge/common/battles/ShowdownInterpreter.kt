package com.cablemc.pokemoncobbled.forge.common.battles

import com.cablemc.pokemoncobbled.forge.common.api.battles.model.Battle
import com.cablemc.pokemoncobbled.forge.common.api.battles.model.actor.BattleActor
import com.cablemc.pokemoncobbled.forge.common.battles.runner.ShowdownConnection
import com.cablemc.pokemoncobbled.forge.mod.PokemonCobbledMod
import java.util.*

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

        if (battle == null) {
            PokemonCobbledMod.LOGGER.info("No battle could be found with the id: $battleId")
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
                        PokemonCobbledMod.LOGGER.info("No actor could be found with the showdown id: $showdownId")
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
                PokemonCobbledMod.LOGGER.info("No actor could be found with the showdown id: $showdownId")
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
    private fun handlePlayerInstruction(battle: Battle, message: String) {

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

    }

    /**
     * Format:
     * |gametype|GAMETYPE
     *
     * Definitions:
     * GAMETYPE is singles, doubles, triples, multi, and or freeforall
     */
    private fun handleGameTypeInstruction(battle: Battle, message: String) {

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

    }

    /**
     * Format:
     * |tier|FORMATNAME
     *
     * Definitions:
     * FORMATNAME is the name of the format being played.
     */
    private fun handleTierInstruction(battle: Battle, message: String) {

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

    }

    /**
     * Format:
     * |rule|RULE: DESCRIPTION
     *
     * Definitions:
     * RULE is a rule and its description
     */
    private fun handleRuleInstruction(battle: Battle, message: String) {

    }

    /**
     * Format:
     * |clearpoke
     *
     * Marks the start of Team Preview
     */
    private fun handleClearPokeInstruction(battle: Battle, message: String) {

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

    }

    /**
     * Format:
     * |turn|NUMBER
     *
     * It is now turn NUMBER.
     */
    private fun handleTurnInstruction(battle: Battle, message: String) {

    }

    /**
     * Format:
     * |faint|POKEMON
     *
     * The Pokémon POKEMON has fainted.
     */
    private fun handleFaintInstruction(battle: Battle, message: String) {

    }

    /**
     * Format:
     * |win|GAMEUUID
     */
    private fun handleWinInstruction(battle: Battle, message: String) {

    }

    // |move|p1a: Charizard|Tackle|p2a: Magikarp
    private fun handleMoveInstruction(battle: Battle, message: String) {

    }

    private fun handleCantInstruction(battle: Battle, message: String) {

    }

    /**
     * Format:
     * |request|REQUEST
     *
     * The protocol message to tell you that it's time for you to make a decision is:
     */
    private fun handleRequestInstruction(battle: Battle, battleActor: BattleActor, message: String) {

    }

    private fun handleSwitchInstruction(battle: Battle, battleActor: BattleActor, publicMessage: String, privateMessage: String) {

    }

}