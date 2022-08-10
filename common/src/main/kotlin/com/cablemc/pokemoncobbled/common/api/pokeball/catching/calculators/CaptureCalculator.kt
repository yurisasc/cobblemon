package com.cablemc.pokemoncobbled.common.api.pokeball.catching.calculators

import com.cablemc.pokemoncobbled.common.api.pokeball.catching.CaptureContext
import com.cablemc.pokemoncobbled.common.api.pokeball.catching.modifiers.GuaranteedModifier
import com.cablemc.pokemoncobbled.common.pokeball.PokeBall
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.entity.LivingEntity

/**
 * Used to process Pokémon captures.
 * This interface is here with the intention that several capture calculators can be created,
 * i.e. supporting an earlier generation capture system.
 *
 * @author landonjw
 * @since November 30, 2021
 */
interface CaptureCalculator {

    fun processCapture(thrower: LivingEntity, pokeBall: PokeBall, target: Pokemon, host: Pokemon?) : CaptureContext
}