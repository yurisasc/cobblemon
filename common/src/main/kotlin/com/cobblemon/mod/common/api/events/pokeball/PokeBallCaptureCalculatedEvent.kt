package com.cobblemon.mod.common.api.events.pokeball

import com.cobblemon.mod.common.api.pokeball.catching.CaptureContext
import com.cobblemon.mod.common.entity.pokeball.EmptyPokeBallEntity
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.server.network.ServerPlayerEntity

/**
 * Event fired when a Poké Ball has completed its capture calculation and is about to begin shaking
 * or breaking free. The result of the capture can be changed by replacing [captureResult].
 *
 * @author Hiroku
 * @since August 20th, 2023
 */
class PokeBallCaptureCalculatedEvent(
    val thrower: LivingEntity,
    val pokemonEntity: PokemonEntity,
    val pokeBallEntity: EmptyPokeBallEntity,
    var captureResult: CaptureContext
) {
    fun ifPlayer(action: PokeBallCaptureCalculatedEvent.(player: ServerPlayerEntity) -> Unit) {
        if (thrower is ServerPlayerEntity) {
            action(this, thrower)
        }
    }
}