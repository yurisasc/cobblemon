package com.cablemc.pokemoncobbled.common.entity.pokemon.ai.goals

import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.goal.LookAtEntityGoal

/**
 * An override of the [LookAtEntityGoal] so that Pokémon behaviours can be implemented.
 *
 * @author Hiroku
 * @since July 30th, 2022
 */
class PokemonLookAtEntityGoal(entity: PokemonEntity, targetType: Class<out LivingEntity>, range: Float) : LookAtEntityGoal(entity, targetType, range) {
    fun canLook() = (mob as PokemonEntity).behaviour.moving.canLook
    override fun canStart() = super.canStart() && canLook()
    override fun shouldContinue() = super.shouldContinue() && canLook()
}