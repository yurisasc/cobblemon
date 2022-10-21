/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.api.pokemon

import com.cablemc.pokemod.common.api.moves.MoveTemplate
import com.cablemc.pokemod.common.api.moves.Moves
import com.cablemc.pokemod.common.util.isInt
import com.google.gson.JsonElement

open class Learnset {
    class Interpreter(
        val loadMove: (JsonElement, Learnset) -> Boolean
    ) {
        companion object {
            fun parseFromPrefixIntoList(prefix: String, list: (Learnset) -> MutableList<MoveTemplate>): Interpreter {
                return Interpreter { element, learnset ->
                    val str = element.takeIf { it.isJsonPrimitive }?.asString ?: return@Interpreter false
                    if (str.startsWith(prefix)) {
                        Moves.getByName(str.substringAfter(":"))
                            ?.let {
                                list(learnset).add(it)
                                return@Interpreter true
                            }
                    }
                    return@Interpreter false
                }
            }
        }
    }

    companion object {
        val tmInterpreter = Interpreter.parseFromPrefixIntoList("tm") { it.tmMoves }
        val eggInterpreter = Interpreter.parseFromPrefixIntoList("egg") { it.eggMoves }
        val tutorInterpreter = Interpreter.parseFromPrefixIntoList("tutor") { it.tutorMoves }
        val levelUpInterpreter = Interpreter { element, learnset ->
            val str = element.takeIf { it.isJsonPrimitive }?.asString ?: return@Interpreter false
            val splits = str.split(":")
            if (splits.size != 2) {
                return@Interpreter false
            } else if (!splits[0].isInt()) {
                return@Interpreter false
            }

            val level = splits[0].toInt()
            val move = Moves.getByName(splits[1]) ?: return@Interpreter false

            val levelLearnset = learnset.levelUpMoves.getOrPut(level) { mutableListOf() }
            if (move !in levelLearnset) {
                levelLearnset.add(move)
            }

            return@Interpreter true

        }

        val interpreters = mutableListOf(
            tmInterpreter,
            eggInterpreter,
            tutorInterpreter,
            levelUpInterpreter
        )
    }

    val levelUpMoves = mutableMapOf<Int, MutableList<MoveTemplate>>()
    val eggMoves = mutableListOf<MoveTemplate>()
    val tutorMoves = mutableListOf<MoveTemplate>()
    val tmMoves = mutableListOf<MoveTemplate>()

    fun getLevelUpMovesUpTo(level: Int) = levelUpMoves
        .entries
        .filter { it.key <= level }
        .sortedBy { it.key }
        .flatMap { it.value }
        .toSet()
}