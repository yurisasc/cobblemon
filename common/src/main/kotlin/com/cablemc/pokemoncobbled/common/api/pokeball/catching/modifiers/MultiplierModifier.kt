package com.cablemc.pokemoncobbled.common.api.pokeball.catching.modifiers

import com.cablemc.pokemoncobbled.common.api.pokeball.catching.CatchRateModifier
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.entity.LivingEntity

class MultiplierModifier(private val multiplier: Float) : CatchRateModifier {
    override fun modifyCatchRate(currentCatchRate: Float, thrower: LivingEntity, pokemon: Pokemon) = currentCatchRate * multiplier
}