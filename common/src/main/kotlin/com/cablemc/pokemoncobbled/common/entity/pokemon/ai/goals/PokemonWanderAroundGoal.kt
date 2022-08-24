package com.cablemc.pokemoncobbled.common.entity.pokemon.ai.goals

import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import net.minecraft.entity.ai.goal.WanderAroundGoal

/**
 * An override of the [WanderAroundGoal] so that Pokémon behaviours can be implemented.
 *
 * @author Hiroku
 * @since July 30th, 2022
 */
class PokemonWanderAroundGoal(entity: PokemonEntity, speed: Double) : WanderAroundGoal(entity, speed) {
    fun canMove() = (mob as PokemonEntity).behaviour.moving.walk.canWalk
    override fun canStart() = super.canStart() && canMove()
    override fun shouldContinue() = super.shouldContinue() && canMove()
}