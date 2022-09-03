package com.cablemc.pokemoncobbled.common.entity.pokemon.ai.goals

import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import net.minecraft.entity.ai.goal.FollowOwnerGoal

/**
 * An override of the [FollowOwnerGoal] so that Pokémon behaviours can be implemented.
 *
 * @author Hiroku
 * @since July 30th, 2022
 */
class PokemonFollowOwnerGoal(
    val entity: PokemonEntity,
    speed: Double,
    minDistance: Float,
    maxDistance: Float,
    leavesAllowed: Boolean
) : FollowOwnerGoal(entity, speed, minDistance, maxDistance, leavesAllowed) {
    fun canMove() = entity.behaviour.moving.walk.canWalk // TODO probably depends on whether we're underwater or not
    override fun canStart() = super.canStart() && canMove()
    override fun shouldContinue() = super.shouldContinue() && canMove()
}