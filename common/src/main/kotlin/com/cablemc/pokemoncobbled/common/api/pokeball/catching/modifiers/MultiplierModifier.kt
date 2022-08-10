package com.cablemc.pokemoncobbled.common.api.pokeball.catching.modifiers

import com.cablemc.pokemoncobbled.common.api.pokeball.catching.CatchRateModifier
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.entity.LivingEntity
import java.util.function.Predicate

class MultiplierModifier(private val multiplier: Float, private val condition: Predicate<Pokemon>?) : CatchRateModifier {
    override fun modifyCatchRate(currentCatchRate: Float, thrower: LivingEntity, pokemon: Pokemon, host: Pokemon?): Float {
        return if(condition?.test(pokemon) != false) {
            currentCatchRate * multiplier
        } else {
            currentCatchRate
        }
    }
}