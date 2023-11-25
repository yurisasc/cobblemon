/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.entity

import com.bedrockk.molang.runtime.value.DoubleValue
import com.bedrockk.molang.runtime.value.StringValue
import com.cobblemon.mod.common.api.entity.PokemonSideDelegate
import com.cobblemon.mod.common.api.molang.MoLangFunctions.addFunctions
import com.cobblemon.mod.common.api.molang.MoLangFunctions.getQueryStruct
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

    override fun changePokemon(pokemon: Pokemon) {
        pokemon.isClient = true
        currentEntity.subscriptions.add(currentEntity.species.subscribeIncludingCurrent {
            currentPose = null
            currentEntity.pokemon.species = PokemonSpecies.getByIdentifier(Identifier(it))!! // TODO exception handling
        })

        currentEntity.subscriptions.add(currentEntity.deathEffectsStarted.subscribe {
            if (it) {
                val model = (currentModel ?: return@subscribe) as PokemonPoseableModel
                val animation = model.getAnimation(this, "faint", runtime) ?: return@subscribe
                val primaryAnimation = if (animation is PrimaryAnimation<PokemonEntity>) animation else PrimaryAnimation(animation, curve = { 1F })
                after(seconds = 3F) { entityScaleModifier = 0F }
                this.addPrimaryAnimation(primaryAnimation)
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
                beamStartTime = System.currentTimeMillis()
                currentEntity.isInvisible = true
                afterOnClient(seconds = BEAM_EXTEND_TIME) {
                    lerpOnClient(BEAM_SHRINK_TIME) { entityScaleModifier = it }
                    currentEntity.isInvisible = false
                }
            } else {
                // Scaling down into pokeball
                entityScaleModifier = 1F
                beamStartTime = System.currentTimeMillis()
                afterOnClient(seconds = BEAM_EXTEND_TIME) {
                    lerpOnClient(BEAM_SHRINK_TIME) {
                        entityScaleModifier = (1 - it)
                    }
                }
            }
        })
    }

    override fun initialize(entity: PokemonEntity) {
        this.currentEntity = entity
        this.age = entity.age

        this.runtime.environment.getQueryStruct().addFunctions(mapOf(
            "in_battle" to java.util.function.Function {
                return@Function DoubleValue(currentEntity.isBattling)
            },
            "shiny" to java.util.function.Function {
                return@Function DoubleValue(currentEntity.pokemon.shiny)
            },
            "form" to java.util.function.Function {
                return@Function StringValue(currentEntity.pokemon.form.name)
            },
            "width" to java.util.function.Function {
                return@Function DoubleValue(currentEntity.boundingBox.xLength)
            },
            "height" to java.util.function.Function {
                return@Function DoubleValue(currentEntity.boundingBox.yLength)
            },
            "weight" to java.util.function.Function {
                return@Function DoubleValue(currentEntity.pokemon.species.weight.toDouble())
            }
        ))
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
           if (cryAnimation != null && (cryAnimation in statefulAnimations || cryAnimation == primaryAnimation)) {
               return
           }

            val animation = model.cryAnimation(currentEntity, this) ?: return
            if (animation is PrimaryAnimation) {
                addPrimaryAnimation(animation)
            } else {
                statefulAnimations.add(animation)
            }
            cryAnimation = animation
        }
    }
}