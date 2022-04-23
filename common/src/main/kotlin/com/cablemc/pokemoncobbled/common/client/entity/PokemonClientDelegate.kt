package com.cablemc.pokemoncobbled.common.client.entity

import com.cablemc.pokemoncobbled.common.api.entity.PokemonSideDelegate
import com.cablemc.pokemoncobbled.common.api.pokemon.PokemonSpecies
import com.cablemc.pokemoncobbled.common.api.scheduling.after
import com.cablemc.pokemoncobbled.common.api.scheduling.lerp
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.PoseableEntityState
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.additives.EarBounceAdditive
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemoncobbled.common.pokemon.Pokemon
import net.minecraft.util.Mth.abs
import net.minecraft.world.entity.Entity
import java.lang.Float.min

class PokemonClientDelegate : PoseableEntityState<PokemonEntity>(), PokemonSideDelegate {
    companion object {
        const val BEAM_SHRINK_TIME = 0.7F
        const val BEAM_EXTEND_TIME = 0.3F
    }

    lateinit var entity: PokemonEntity
    var phaseTarget: Entity? = null
    var entityScaleModifier = 1F

    var animTick = 0F
    var previousVerticalVelocity = 0F

    var beamStartTime = System.currentTimeMillis()

    val secondsSinceBeamEffectStarted: Float
        get() = (System.currentTimeMillis() - beamStartTime) / 1000F

    private val minimumFallSpeed = -0.1F
    private val intensityVelocityCap = -0.5F
    override fun changePokemon(pokemon: Pokemon) {
        entity.dexNumber.subscribeIncludingCurrent {
            currentPose = null
            entity.pokemon.species = PokemonSpecies.getByPokedexNumber(it)!! // TODO exception handling
        }

        entity.shiny.subscribeIncludingCurrent { entity.pokemon.shiny = it }
        entity.phasingTargetId.subscribe {
            if (it != -1) {
                setPhaseTarget(it)
            } else {
                phaseTarget = null
            }
        }

        entity.beamModeEmitter.subscribeIncludingCurrent {
            if (it == 0.toByte()) {
                // Do nothing
            } else if (it == 1.toByte()) {
                // Scaling up out of pokeball
                entityScaleModifier = 0F
                beamStartTime = System.currentTimeMillis()
                entity.isInvisible = true
                after(seconds = BEAM_EXTEND_TIME) {
                    lerp(BEAM_SHRINK_TIME) { entityScaleModifier = it }
                    entity.isInvisible = false
                }
            } else {
                // Scaling down into pokeball
                entityScaleModifier = 1F
                beamStartTime = System.currentTimeMillis()
                after(seconds = BEAM_EXTEND_TIME) {
                    lerp(BEAM_SHRINK_TIME) {
                        entityScaleModifier = (1 - it)
                    }
                }
            }
        }
    }

    override fun initialize(entity: PokemonEntity) {
        this.entity = entity
    }

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

    fun setPhaseTarget(targetId: Int) {
        this.phaseTarget = entity.level.getEntity(targetId)
    }
}