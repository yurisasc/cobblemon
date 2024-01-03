/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.entity

import com.cobblemon.mod.common.CobblemonSounds
import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.entity.PokemonSideDelegate
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.api.scheduling.ClientTaskTracker
import com.cobblemon.mod.common.api.scheduling.afterOnClient
import com.cobblemon.mod.common.api.scheduling.lerpOnClient
import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityState
import com.cobblemon.mod.common.client.render.models.blockbench.additives.EarBounceAdditive
import com.cobblemon.mod.common.client.render.models.blockbench.animation.StatefulAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.client.render.pokemon.PokemonRenderer.Companion.ease
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.util.MovingSoundInstance
import net.minecraft.client.MinecraftClient
import java.lang.Float.min
import kotlin.math.abs
import net.minecraft.entity.Entity
import net.minecraft.entity.data.TrackedData
import net.minecraft.particle.ParticleTypes
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d

class PokemonClientDelegate : PoseableEntityState<PokemonEntity>(), PokemonSideDelegate {
    companion object {
        const val BEAM_SHRINK_TIME = 0.4F
        const val BEAM_EXTEND_TIME = 0.2F
        const val POKEBALL_AIR_TIME = 0.5F
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
    var sendOutOffset: Vec3d? = null
    var playedSendOutSound: Boolean = false
    var playedThrowingSound: Boolean = false

    val secondsSinceBeamEffectStarted: Float
        get() = (System.currentTimeMillis() - beamStartTime) / 1000F

    val secondsSinceBallThrown: Float
        get() = (System.currentTimeMillis() - ballStartTime) / 1000F

    private val minimumFallSpeed = -0.1F
    private val intensityVelocityCap = -0.5F

    private var cryAnimation: StatefulAnimation<PokemonEntity, *>? = null

    override fun onTrackedDataSet(data: TrackedData<*>) {
        super.onTrackedDataSet(data)
        if (this::currentEntity.isInitialized) {
            if (data == PokemonEntity.SPECIES) {
                val identifier = Identifier(currentEntity.dataTracker.get(PokemonEntity.SPECIES))
                currentPose = null
                currentEntity.pokemon.species = PokemonSpecies.getByIdentifier(identifier)!! // TODO exception handling
            } else if (data == PokemonEntity.DYING_EFFECTS_STARTED) {
                val isDying = currentEntity.dataTracker.get(PokemonEntity.DYING_EFFECTS_STARTED)
                if (isDying) {
                    val model = (currentModel ?: return) as PokemonPoseableModel
                    val animation = try {
                        model.getFaintAnimation(currentEntity, this)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    } ?: return
                    statefulAnimations.add(animation)
                }
            } else if (data == PokemonEntity.BEAM_MODE) {
                val beamMode = currentEntity.beamMode
                when (beamMode) {
                    0 -> { /* Do nothing */ }
                    1 -> {
                        // Scaling up out of pokeball
                        playedSendOutSound = false
                        entityScaleModifier = 0F
                        beamStartTime = System.currentTimeMillis()
                        ballStartTime = System.currentTimeMillis()
                        currentEntity.isInvisible = true
                        ballDone = false
                        var soundPos = currentEntity.pos
                        currentEntity.pokemon.getOwnerUUID()?.let{
                            currentEntity.world.getPlayerByUuid(it)?.let {
                                val offset = it.pos.subtract(currentEntity.pos.add(0.0, 2.0 - (ballOffset.toDouble()/10f), 0.0)).normalize().multiply(-ease(ballOffset.toDouble()))
                                with(it.pos.subtract(currentEntity.pos)) {
                                    var newOffset = offset.multiply(2.0)
                                    val distance = it.pos.distanceTo(currentEntity.pos)
                                    newOffset = newOffset.multiply((distance / 10.0) * 5)
                                    soundPos = currentEntity.pos.add(newOffset)
                                }
                                it.swingHand(it.activeHand ?: Hand.MAIN_HAND)
                            }
                        }
                        val client = MinecraftClient.getInstance()
                        val sound = MovingSoundInstance(SoundEvent.of(CobblemonSounds.POKE_BALL_TRAIL.id), SoundCategory.PLAYERS, { sendOutPosition?.add(sendOutOffset) }, 0.1f, 1f, false, 20, 0)
                        if(!playedThrowingSound){
                            client.soundManager.play(sound)
                            playedThrowingSound = true
                        }
                        lerp(POKEBALL_AIR_TIME) { ballOffset = it }
                        ballRotOffset = ((Math.random()) * currentEntity.world.random.nextBetween(-15, 15)).toFloat()
                        after(seconds = POKEBALL_AIR_TIME){
                            beamStartTime = System.currentTimeMillis()
                            ballDone = true
                            if (client.soundManager.get(CobblemonSounds.POKE_BALL_OPEN.id) != null && !playedSendOutSound) {
                                currentEntity.owner?.let {
                                    client.world?.playSound(client.player, soundPos.x, soundPos.y, soundPos.z, SoundEvent.of(CobblemonSounds.POKE_BALL_SEND_OUT.id), SoundCategory.PLAYERS, 0.6f, 1f)
                                    playedSendOutSound = true
                                    /// create end rod particles in a 0.1 radius around the soundPos with a count of 50 and a random velocity of 0.1
                                    sendOutPosition?.let{
                                        val newPos = it.add(sendOutOffset)
                                        for(i in 0..50) {
                                            client.particleManager.addParticle(ParticleTypes.END_ROD, newPos!!.x + (Math.random() * 0.1) - 0.05, newPos!!.y + (Math.random() * 0.1) - 0.05, newPos!!.z + (Math.random() * 0.1) - 0.05,
                                                Math.random() * 0.4 - 0.2, Math.random() * 0.4 - 0.2, Math.random() * 0.4 - 0.2)
                                        }
                                    }
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
                    }
                    else -> {
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
                }
            } else if (data == PokemonEntity.LABEL_LEVEL) {
                currentEntity.dataTracker.get(PokemonEntity.LABEL_LEVEL)
                    .takeIf { it > 0 }
                    ?.let { currentEntity.pokemon.level = it }
            } else if (data == PokemonEntity.PHASING_TARGET_ID) {
                val phasingTargetId = currentEntity.dataTracker.get(PokemonEntity.PHASING_TARGET_ID)
                if (phasingTargetId != -1) {
                    setPhaseTarget(phasingTargetId)
                } else {
                    phaseTarget = null
                }
            }
        }
    }

    override fun changePokemon(pokemon: Pokemon) {
        pokemon.isClient = true
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