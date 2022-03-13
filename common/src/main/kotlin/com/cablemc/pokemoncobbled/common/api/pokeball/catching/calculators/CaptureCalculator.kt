package com.cablemc.pokemoncobbled.common.api.pokeball.catching.calculators

import com.cablemc.pokemoncobbled.common.api.pokeball.catching.CaptureContext
import com.cablemc.pokemoncobbled.common.pokeball.PokeBall
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.server.level.ServerPlayer

/**
 * Used to process pokemon captures.
 * This interface is here with the intention that several pokemon calculators can be created,
 * ie. supporting an earlier generation capture system.
 *
 * @author landonjw
 * @since  November 30, 2021
 */
interface CaptureCalculator {
    fun processCapture(player: ServerPlayer, pokemon: Pokemon, pokeBall: PokeBall) : CaptureContext
}