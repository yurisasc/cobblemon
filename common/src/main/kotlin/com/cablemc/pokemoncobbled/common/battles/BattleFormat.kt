package com.cablemc.pokemoncobbled.common.battles

/**
 * Rules around how a battle is going to work.
 *
 * @author Hiroku
 * @since March 9th, 2022
 */
data class BattleFormat(
    val generation: Int = 8,
    val battleType: BattleType = BattleTypes.SINGLES,
    val ruleSet: List<String> = listOf()
) {
    companion object {
        val GEN_8_SINGLES = BattleFormat(
            generation = 8,
            battleType = BattleTypes.SINGLES,
            ruleSet = listOf(BattleRules.OBTAINABLE)
        )

        val GEN_8_DOUBLES = BattleFormat(
            generation = 8,
            battleType = BattleTypes.DOUBLES,
            ruleSet = listOf(BattleRules.OBTAINABLE)
        )

        val GEN_8_MULTI = BattleFormat(
            generation = 8,
            battleType = BattleTypes.MULTI,
            ruleSet = listOf(BattleRules.OBTAINABLE)
        )
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