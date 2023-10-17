/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.entity

import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.api.entity.PokemonSideDelegate
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.api.scheduling.after
import com.cobblemon.mod.common.api.scheduling.lerp
import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityState
import com.cobblemon.mod.common.client.render.models.blockbench.additives.EarBounceAdditive
import com.cobblemon.mod.common.client.render.models.blockbench.animation.StatefulAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.client.MinecraftClient
import java.lang.Float.min
import kotlin.math.abs
import net.minecraft.entity.Entity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d
import org.joml.Vector3f

class PokemonClientDelegate : PoseableEntityState<PokemonEntity>(), PokemonSideDelegate {
    companion object {
        const val BEAM_SHRINK_TIME = 0.8F
        const val BEAM_EXTEND_TIME = 0.2F
        const val POKEBALL_AIR_TIME = 1.0F
    }

    lateinit var currentEntity: PokemonEntity
    var phaseTarget: Entity? = null
    var entityScaleModifier = 1F

    override fun getEntity() = currentEntity

    override fun updatePartialTicks(partialTicks: Float) {
        this.currentPartialTicks = partialTicks
    }

    var previousVerticalVelocity = 0F
    var beamStartTime = System.currentTimeMillis()
    var ballStartTime = System.currentTimeMillis()
    var ballDone = false
    var ballOffset = 0f
    var ballRotOffset = 0f
    var sendOutPosition: Vec3d? = null

    val secondsSinceBeamEffectStarted: Float
        get() = (System.currentTimeMillis() - beamStartTime) / 1000F

    val secondsSinceBallThrown: Float
        get() = (System.currentTimeMillis() - ballStartTime) / 1000F

    private val minimumFallSpeed = -0.1F
    private val intensityVelocityCap = -0.5F

    private var cryAnimation: StatefulAnimation<PokemonEntity, *>? = null

    override fun changePokemon(pokemon: Pokemon) {
        pokemon.isClient = true
        currentEntity.subscriptions.add(currentEntity.species.subscribeIncludingCurrent {
            currentPose = null
            currentEntity.pokemon.species = PokemonSpecies.getByIdentifier(Identifier(it))!! // TODO exception handling
        })

//        currentEntity.subscriptions.add(currentEntity.nickname.subscribeIncludingCurrent {
//            currentEntity.pokemon.nickname = it?.copy()
//        })

        currentEntity.subscriptions.add(currentEntity.deathEffectsStarted.subscribe {
            if (it) {
                val model = (currentModel ?: return@subscribe) as PokemonPoseableModel
                val animation = try { model.getFaintAnimation(currentEntity, this) } catch (e: Exception) { e.printStackTrace(); null } ?: return@subscribe
                statefulAnimations.add(animation)
            }
        })

        currentEntity.subscriptions.add(currentEntity.labelLevel.subscribeIncludingCurrent { if (it > 0) currentEntity.pokemon.level = it })

        currentEntity.subscriptions.add(currentEntity.phasingTargetId.subscribe {
            if (it != -1) {
                setPhaseTarget(it)
            } else {
                phaseTarget = null
            }
        })

//        pokemon.aspects = currentEntity.aspects.get()
//        currentEntity.aspects.pipe(emitWhile { pokemon == currentEntity.pokemon }).subscribe {
//            pokemon.aspects = it
//        }

        currentEntity.subscriptions.add(currentEntity.beamModeEmitter.subscribeIncludingCurrent {
            if (it == 0.toByte()) {
                // Do nothing
            } else if (it == 1.toByte()) {
                // Scaling up out of pokeball
                entityScaleModifier = 0F
                ballStartTime = System.currentTimeMillis()
                currentEntity.isInvisible = true
                ballDone = false
                currentEntity.pokemon.getOwnerUUID()?.let{
                    currentEntity.world.getPlayerByUuid(it)?.let {
                        it.swingHand(it.activeHand ?: Hand.MAIN_HAND)
                    }
                }
                lerp(POKEBALL_AIR_TIME) { ballOffset = it }
                ballRotOffset = ((Math.random()) * currentEntity.world.random.nextBetween(-25, 25)).toFloat()
                after(seconds = POKEBALL_AIR_TIME){
                    beamStartTime = System.currentTimeMillis()
                    ballDone = true
                    currentEntity.playSound(CobblemonSounds.POKE_BALL_OPEN, 1F, 1F)
                    val client = MinecraftClient.getInstance()
                    if (client.soundManager.get(CobblemonSounds.POKE_BALL_OPEN.id) != null) {
                        currentEntity.owner?.let {
                            client.world?.playSound(client.player, it.x, it.y, it.z, SoundEvent.of(CobblemonSounds.POKE_BALL_OPEN.id), SoundCategory.PLAYERS, 1f, 1f)
                        }
                    }
                    after(seconds = BEAM_EXTEND_TIME) {
                        lerp(BEAM_SHRINK_TIME) { entityScaleModifier = it }
                        currentEntity.isInvisible = false
                        after(seconds = POKEBALL_AIR_TIME*2){
                            ballOffset = 0f
                            ballRotOffset = 0f
                            sendOutPosition = null
                        }
                    }
                }
            } else {
                // Scaling down into pokeball
                entityScaleModifier = 1F
                beamStartTime = System.currentTimeMillis()
                ballOffset = 0f
                ballRotOffset = 0f
                sendOutPosition = null
                after(seconds = BEAM_EXTEND_TIME) {
                    lerp(BEAM_SHRINK_TIME) {
                        entityScaleModifier = (1 - it)
                    }
                }
            }
        })
    }

    override fun initialize(entity: PokemonEntity) {
        this.currentEntity = entity
        this.age = entity.age
    }

    override fun tick(entity: PokemonEntity) {
        val downSpeed = entity.velocity.y
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

        updateLocatorPosition(entity.pos)
        previousVerticalVelocity = entity.velocity.y.toFloat()

        incrementAge(entity)
    }

    fun setPhaseTarget(targetId: Int) {
        this.phaseTarget = currentEntity.world.getEntityById(targetId)
    }

    override fun handleStatus(status: Byte) {
        if (status == 10.toByte()) {
            val model = (currentModel ?: return) as PokemonPoseableModel
            val animation = model.getEatAnimation(currentEntity, this) ?: return
            statefulAnimations.add(animation)
        }
    }

    override fun updatePostDeath() {
        ++currentEntity.deathTime
    }

    fun cry() {
        val model = currentModel ?: return
        if (model is PokemonPoseableModel) {
           if (cryAnimation != null && cryAnimation in statefulAnimations) {
               return
           }

            val animation = model.cryAnimation(currentEntity, this) ?: return
            statefulAnimations.add(animation)
            cryAnimation = animation
        }
    }
}