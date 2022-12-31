/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.battles.interpreter

import com.cobblemon.mod.common.api.battles.model.PokemonBattle
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor
import com.cobblemon.mod.common.battles.ActiveBattlePokemon

/**
 * TODO
 *
 * @constructor
 * TODO
 *
 * @param rawMessage
 *
 * @author Licious
 * @since December 31st, 2022
 */
class BattleMessage(rawMessage: String) {

    /**
     * TODO
     */
    var id = ""
        private set

    /**
     * TODO
     */
    var rawMessage: String = rawMessage
        private set

    /**
     * TODO
     */
    private val args = arrayListOf<String>()

    /**
     * TODO
     */
    private val optionalArguments = hashMapOf<String, String>()

    /**
     * TODO
     */
    private val optionalArgumentMatcher = Regex("^\\${OPTIONAL_ARG_START}([^]]+)${OPTIONAL_ARG_END}")

    init {
        this.parse(rawMessage)
    }

    /**
     * TODO
     *
     * @param index
     * @return
     */
    fun argumentAt(index: Int): String? = this.args.getOrNull(index)

    /**
     * TODO
     *
     * @param name
     * @return
     */
    fun optionalArgument(name: String): String? = this.optionalArguments[name.lowercase()]

    /**
     * TODO
     *
     * @param rawMessage
     * @return
     */
    fun parse(rawMessage: String): BattleMessage {
        var message = rawMessage.trim()
        this.rawMessage = message
        this.args.clear()
        this.optionalArguments.clear()
        if (!message.startsWith(SEPARATOR) || message == SEPARATOR) {
            return this
        }
        message = this.push(message)
        this.id = message.substringBefore(SEPARATOR)
        message = this.push(message)
        while (message.isNotBlank()) {
            val currentData = message.substringBefore(SEPARATOR)
            val optionalArgumentID = this.optionalArgumentMatcher.find(currentData)
            if (optionalArgumentID != null) {
                val id = optionalArgumentID.value.removePrefix(OPTIONAL_ARG_START).removeSuffix(OPTIONAL_ARG_END).lowercase()
                val value = currentData.substringAfter(optionalArgumentID.value).trim()
                this.optionalArguments[id] = value
            }
            else {
                this.args.add(currentData)
            }
            message = this.push(message)
        }
        return this
    }

    /**
     * TODO
     *
     * @param index
     * @param battle
     * @return
     */
    fun actorAndActivePokemon(index: Int, battle: PokemonBattle): Pair<BattleActor, ActiveBattlePokemon>? {
        val pnx = this.argumentAt(index)?.substring(0, 3) ?: return null
        return this.actorAndActivePokemon(pnx, battle)
    }

    /**
     * TODO
     *
     * @param argumentName
     * @return
     */
    fun effect(argumentName: String = "from"): Effect? {
        val data = this.optionalArgument(argumentName) ?: return null
        return Effect.parse(data)
    }

    /**
     * TODO
     *
     * @param argumentName
     * @return
     */
    fun actorAndActivePokemonFromOptional(battle: PokemonBattle, argumentName: String = "of"): Pair<BattleActor, ActiveBattlePokemon>? {
        val pnx = this.optionalArgument(argumentName)?.substring(0, 3) ?: return null
        return this.actorAndActivePokemon(pnx, battle)
    }

    /**
     * TODO
     *
     * @param message
     * @return
     */
    private fun push(message: String): String = message.substringAfter(SEPARATOR, "")

    /**
     * TODO
     *
     * @param pnx
     * @param battle
     * @return
     */
    private fun actorAndActivePokemon(pnx: String, battle: PokemonBattle): Pair<BattleActor, ActiveBattlePokemon>? = try {
        battle.getActorAndActiveSlotFromPNX(pnx)
    } catch (_: Exception) {
        null
    }

    companion object {

        private const val SEPARATOR = "|"
        private const val OPTIONAL_ARG_START = "["
        private const val OPTIONAL_ARG_END = "]"

    }

}