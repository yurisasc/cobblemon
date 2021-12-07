package com.cablemc.pokemoncobbled.client.entity

import com.cablemc.pokemoncobbled.client.render.models.blockbench.PoseableEntityState
import com.cablemc.pokemoncobbled.client.render.models.blockbench.additives.EarBounceAdditive
import com.cablemc.pokemoncobbled.common.api.entity.EntitySideDelegate
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonSpecies
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import net.minecraft.util.Mth.abs
import java.lang.Float.min

class PokemonClientDelegate : PoseableEntityState<PokemonEntity>(), EntitySideDelegate<PokemonEntity> {

    // Put any client-only variables or functions in here. This delegate is 1-1 with the entity on the client side
    var animTick = 0F
    var previousVerticalVelocity = 0F
    override fun initialize(entity: PokemonEntity) {
        entity.dexNumber.subscribeIncludingCurrent {
            currentPose = null
            entity.pokemon.species = PokemonSpecies.getByPokedexNumber(it)!! // TODO exception handling
        }
        entity.scaleModifier.subscribe {
            entity.pokemon.scaleModifier = it // TODO exception handling
        }
    }

    private val minimumFallSpeed = -0.1F
    private val intensityVelocityCap = -0.5F

    override fun tick(entity: PokemonEntity) {
        val downSpeed = entity.deltaMovement.y
        if (downSpeed > previousVerticalVelocity && downSpeed > minimumFallSpeed) {
            // Stopped falling
            val highestFallVelocity = previousVerticalVelocity
            if (abs(highestFallVelocity) > abs(minimumFallSpeed)) {
                val intensity = abs(min(highestFallVelocity / intensityVelocityCap, 1F))
                if (additives.none { it is EarBounceAdditive }) {
                    additives.add(EarBounceAdditive(intensity, 18))
                }
            }
        }

        previousVerticalVelocity = entity.deltaMovement.y.toFloat()
    }
}