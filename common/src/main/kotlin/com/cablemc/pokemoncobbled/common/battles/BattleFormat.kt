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
    val generation: Int = 8,
    val battleType: BattleType = BattleTypes.SINGLES,
    val ruleSet: Set<String> = setOf()
) {
    companion object {
        val GEN_8_SINGLES = BattleFormat(
            generation = 8,
            battleType = BattleTypes.SINGLES,
            ruleSet = setOf(BattleRules.OBTAINABLE)
        )

        val GEN_8_DOUBLES = BattleFormat(
            generation = 8,
            battleType = BattleTypes.DOUBLES,
            ruleSet = setOf(BattleRules.OBTAINABLE)
        )

        val GEN_8_MULTI = BattleFormat(
            generation = 8,
            battleType = BattleTypes.MULTI,
            ruleSet = setOf(BattleRules.OBTAINABLE)
        )

        fun loadFromBuffer(buffer: PacketByteBuf): BattleFormat {
            val generation = buffer.readSizedInt(IntSize.U_BYTE)
            val battleType = BattleType.loadFromBuffer(buffer)
            val ruleSet = mutableSetOf<String>()
            repeat(times = buffer.readSizedInt(IntSize.U_BYTE)) { ruleSet.add(buffer.readString()) }
            return BattleFormat(
                generation = generation,
                battleType = battleType,
                ruleSet = ruleSet
            )
        }
    }

    fun saveToBuffer(buffer: PacketByteBuf): PacketByteBuf {
        buffer.writeSizedInt(IntSize.U_BYTE, generation)
        battleType.saveToBuffer(buffer)
        buffer.writeSizedInt(IntSize.U_BYTE, ruleSet.size)
        ruleSet.forEach(buffer::writeString)
        return buffer
    }

    fun toFormatJSON(): String {
        return """
            {
                "mod": "gen$generation",
                "gameType": "${battleType.name}",
                "ruleset": [${ruleSet.joinToString { "\"$it\"" }}]
            }
        """.trimIndent().replace("\n", "")
    }
}