/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.battles.interpreter

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

    var id = ""
        private set

    var rawMessage: String = rawMessage
        private set

    private val args = arrayListOf<String>()

    private val optionalArguments = hashMapOf<String, String>()
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
                val id = optionalArgumentID.value.replace(OPTIONAL_ARG_START, "").replace(OPTIONAL_ARG_END, "").lowercase()
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
     * @param argumentName
     * @return
     */
    fun effect(argumentName: String = "from"): Effect? {
        val data = this.optionalArgument(argumentName) ?: return null
        return Effect.parse(data)
    }

    private fun push(message: String): String = message.substringAfter(SEPARATOR, "")

    companion object {

        private const val SEPARATOR = "|"
        private const val OPTIONAL_ARG_START = "["
        private const val OPTIONAL_ARG_END = "]"

    }

}