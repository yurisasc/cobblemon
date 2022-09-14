package com.cablemc.pokemoncobbled.common.world

import net.minecraft.world.GameRules

object CobbledGameRules {
    lateinit var DO_POKEMON_SPAWNING: GameRules.Key<GameRules.BooleanRule>

    fun register() {
        DO_POKEMON_SPAWNING = GameRules.register("doPokemonSpawning", GameRules.Category.SPAWNING, GameRules.BooleanRule.create(true))
    }
}