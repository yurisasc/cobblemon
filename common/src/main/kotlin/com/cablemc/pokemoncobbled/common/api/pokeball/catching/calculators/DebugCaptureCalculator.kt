package com.cablemc.pokemoncobbled.common.api.pokeball.catching.calculators

import com.cablemc.pokemoncobbled.common.api.pokeball.catching.CaptureContext
import com.cablemc.pokemoncobbled.common.pokeball.PokeBall
import com.cablemc.pokemoncobbled.common.entity.pokemon.Pokemon
import net.minecraft.server.level.ServerPlayer

object DebugCaptureCalculator : CaptureCalculator {
    override fun processCapture(player: ServerPlayer, pokemon: Pokemon, pokeBall: PokeBall): CaptureContext {
        return CaptureContext(numberOfShakes = 3, isSuccessfulCapture = true, isCriticalCapture = false)
    }
}