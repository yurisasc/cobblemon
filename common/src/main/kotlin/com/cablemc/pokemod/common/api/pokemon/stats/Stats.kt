/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.api.pokemon.stats

object Stats {
    private val allStats = mutableListOf<Stat>()

    var HP = registerStat("hp")
    var ATTACK = registerStat("attack")
    var DEFENCE = registerStat("defence")
    var SPECIAL_ATTACK = registerStat("special_attack")
    var SPECIAL_DEFENCE = registerStat("special_defence")
    var SPEED = registerStat("speed")

    val EVASION = registerStat("evasion")
    val ACCURACY = registerStat("accuracy")

    val mainStats = listOf(HP, ATTACK, DEFENCE, SPECIAL_ATTACK, SPECIAL_DEFENCE, SPEED)

    fun registerStat(id: String): Stat {
        val stat = object : Stat {
            override val id = id
        }
        allStats.add(stat)
        return stat
    }

    fun getStat(id: String, ignoreCase: Boolean = false) = allStats.find { it.id.equals(id, ignoreCase) } ?: Stat.Dummy(id)
}