package com.cablemc.pokemoncobbled.common.world

import dev.architectury.registry.level.GameRuleFactory
import dev.architectury.registry.level.GameRuleRegistry
import net.minecraft.world.GameRules

object CobbledGameRules {
    lateinit var DO_POKEMON_SPAWNING: GameRules.Key<GameRules.BooleanRule>

    fun register() {
        DO_POKEMON_SPAWNING = GameRuleRegistry.register("doPokemonSpawning", GameRules.Category.SPAWNING, GameRuleFactory.createBooleanRule(true))
    }
}