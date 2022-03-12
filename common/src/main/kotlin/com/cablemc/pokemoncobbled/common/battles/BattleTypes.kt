package com.cablemc.pokemoncobbled.common.battles

// note: showdown calls it gameType, but in MC GameType would collide with plugins and shit a lot.

object BattleTypes {
    val SINGLES = makeBattleType("singles", actorsPerSide = 1, slotsPerActor = 1)
    val DOUBLES = makeBattleType("doubles", actorsPerSide = 1, slotsPerActor = 2)
    val TRIPLES = makeBattleType("triples", actorsPerSide = 1, slotsPerActor = 3)
    val MULTI = makeBattleType("multi", actorsPerSide = 2, slotsPerActor = 1)
    // maybe one day we can add MULTI-3 for triple battles with 6 fuckers in it, that'd be sick. We could game it with partial actors though

    fun makeBattleType(name: String, actorsPerSide: Int, slotsPerActor: Int) = object : BattleType {
        override val name = name
        override val actorsPerSide = actorsPerSide
        override val slotsPerActor = slotsPerActor
    }
}

interface BattleType {
    val name: String
    val actorsPerSide: Int
    val slotsPerActor: Int
}