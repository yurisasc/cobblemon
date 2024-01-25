/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.entity

import com.cobblemon.mod.common.api.entity.PokemonSideDelegate
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies
import com.cobblemon.mod.common.api.scheduling.SchedulingTracker
import com.cobblemon.mod.common.api.scheduling.afterOnClient
import com.cobblemon.mod.common.api.scheduling.lerpOnClient
import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityState
import com.cobblemon.mod.common.client.render.models.blockbench.animation.PrimaryAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.animation.StatefulAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.pokemon.Pokemon
import net.minecraft.entity.Entity
import net.minecraft.entity.data.TrackedData
import net.minecraft.util.Identifier

class PokemonClientDelegate : PoseableEntityState<PokemonEntity>(), PokemonSideDelegate {
    companion object {
        const val BEAM_SHRINK_TIME = 0.8F
        const val BEAM_EXTEND_TIME = 0.2F
    }

    override val schedulingTracker: SchedulingTracker
        get() = currentEntity.schedulingTracker
    lateinit var currentEntity: PokemonEntity
    var phaseTarget: Entity? = null
    var entityScaleModifier = 1F

    override fun getEntity() = currentEntity

    override fun updatePartialTicks(partialTicks: Float) {
        this.currentPartialTicks = partialTicks
        schedulingTracker.update(0F)
    }

    var beamStartTime = System.currentTimeMillis()

    val secondsSinceBeamEffectStarted: Float
        get() = (System.currentTimeMillis() - beamStartTime) / 1000F

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
                    val primaryAnimation = PrimaryAnimation(animation)
                    after(seconds = 3F) { entityScaleModifier = 0F }
                    this.addPrimaryAnimation(primaryAnimation)
                }
            } else if (data == PokemonEntity.BEAM_MODE) {
                val beamMode = currentEntity.beamMode
                when (beamMode) {
                    0 -> { /* Do nothing */ }
                    1 -> {
                        // Scaling up out of pokeball
                        entityScaleModifier = 0F
                        beamStartTime = System.currentTimeMillis()
                        currentEntity.isInvisible = true
                        afterOnClient(seconds = BEAM_EXTEND_TIME) {
                            lerpOnClient(BEAM_SHRINK_TIME) { entityScaleModifier = it }
                            currentEntity.isInvisible = false
                        }
                    }
                    else -> {
                        // Scaling down into pokeball
                        entityScaleModifier = 1F
                        beamStartTime = System.currentTimeMillis()
                        afterOnClient(seconds = BEAM_EXTEND_TIME) {
                            val afterPoint2 = System.currentTimeMillis()
                    lerpOnClient(BEAM_SHRINK_TIME) {
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
            } else if(data == PokemonEntity.SEAT_UPDATER) {
                currentEntity.riding.seats = currentEntity.dataTracker.get(PokemonEntity.SEAT_UPDATER).map { it.create(currentEntity) }
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
        updateLocatorPosition(entity.pos)
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