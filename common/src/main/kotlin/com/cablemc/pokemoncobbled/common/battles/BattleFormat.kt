/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.battles

import com.cablemc.pokemoncobbled.common.net.IntSize
import com.cablemc.pokemoncobbled.common.util.readSizedInt
import com.cablemc.pokemoncobbled.common.util.writeSizedInt
import net.minecraft.network.PacketByteBuf

/**
 * Rules around how a battle is going to work.
 *
 * @author Hiroku
 * @since March 9th, 2022
 */
data class BattleFormat(
    val mod: String = "cobbled",
    val battleType: BattleType = BattleTypes.SINGLES,
    val ruleSet: Set<String> = setOf()
) {
    companion object {
        val GEN_8_SINGLES = BattleFormat(
            battleType = BattleTypes.SINGLES,
            ruleSet = setOf(BattleRules.OBTAINABLE)
        )

        val GEN_8_DOUBLES = BattleFormat(
            battleType = BattleTypes.DOUBLES,
            ruleSet = setOf(BattleRules.OBTAINABLE)
        )

        val GEN_8_MULTI = BattleFormat(
            battleType = BattleTypes.MULTI,
            ruleSet = setOf(BattleRules.OBTAINABLE)
        )

        fun loadFromBuffer(buffer: PacketByteBuf): BattleFormat {
            val mod = buffer.readString()
            val battleType = BattleType.loadFromBuffer(buffer)
            val ruleSet = mutableSetOf<String>()
            repeat(times = buffer.readSizedInt(IntSize.U_BYTE)) { ruleSet.add(buffer.readString()) }
            return BattleFormat(
                mod = mod,
                battleType = battleType,
                ruleSet = ruleSet
            )
        }
    }

    fun saveToBuffer(buffer: PacketByteBuf): PacketByteBuf {
        buffer.writeString(mod)
        battleType.saveToBuffer(buffer)
        buffer.writeSizedInt(IntSize.U_BYTE, ruleSet.size)
        ruleSet.forEach(buffer::writeString)
        return buffer
    }

    fun toFormatJSON(): String {
        return """
            {
                "mod": "$mod",
                "gameType": "${battleType.name}",
                "ruleset": [${ruleSet.joinToString { "\"$it\"" }}]
            }
        """.trimIndent().replace("\n", "")
    }
}