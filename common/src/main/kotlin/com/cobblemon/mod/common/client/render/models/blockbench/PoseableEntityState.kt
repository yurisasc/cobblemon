/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench

import com.bedrockk.molang.runtime.MoLangRuntime
import com.bedrockk.molang.runtime.struct.VariableStruct
import com.bedrockk.molang.runtime.value.DoubleValue
import com.bedrockk.molang.runtime.value.MoValue
import com.bedrockk.molang.runtime.value.StringValue
import com.cobblemon.mod.common.Cobblemon.LOGGER
import com.cobblemon.mod.common.api.molang.MoLangFunctions.addFunction
import com.cobblemon.mod.common.api.molang.MoLangFunctions.addFunctions
import com.cobblemon.mod.common.api.molang.MoLangFunctions.getQueryStruct
import com.cobblemon.mod.common.api.molang.ObjectValue
import com.cobblemon.mod.common.api.text.text
import com.cobblemon.mod.common.client.particle.BedrockParticleEffectRepository
import com.cobblemon.mod.common.client.particle.ParticleStorm
import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.scheduling.Schedulable
import com.cobblemon.mod.common.client.render.MatrixWrapper
import com.cobblemon.mod.common.client.render.models.blockbench.animation.PrimaryAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.animation.StatefulAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.animation.StatelessAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.bedrock.animation.BedrockParticleKeyframe
import com.cobblemon.mod.common.client.render.models.blockbench.bedrock.animation.BedrockStatefulAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.bedrock.animation.BedrockStatelessAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.frame.ModelFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pose.Pose
import com.cobblemon.mod.common.client.render.models.blockbench.quirk.ModelQuirk
import com.cobblemon.mod.common.client.render.models.blockbench.quirk.QuirkData
import com.cobblemon.mod.common.entity.Poseable
import com.cobblemon.mod.common.util.asExpression
import com.cobblemon.mod.common.util.asIdentifierDefaultingNamespace
import com.cobblemon.mod.common.util.resolve
import java.util.concurrent.ConcurrentLinkedQueue
import net.minecraft.client.MinecraftClient
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.util.math.Vec3d

/**
 * Represents the entity-specific state for a poseable model. The implementation is responsible for
 * handling all the state for an entity's model, and needs to be conscious of the fact that the
 * model may change without this state changing.
 *
 * @author Hiroku
 * @since December 5th, 2021
 */
abstract class PoseableEntityState<T : Entity> : Schedulable {
    var currentModel: PoseableEntityModel<T>? = null
    var currentPose: String? = null
    var primaryAnimation: PrimaryAnimation<T>? = null
    val statefulAnimations: MutableList<StatefulAnimation<T, *>> = mutableListOf()
    val quirks = mutableMapOf<ModelQuirk<T, *>, QuirkData<T>>()
    val poseParticles = mutableListOf<BedrockParticleKeyframe>()
    val runtime = MoLangRuntime().also { runtime ->
        val reusableAnimTime = DoubleValue(0.0) // This gets called 500 million times so use a mutable value for runtime
        runtime.environment.getQueryStruct().addFunctions(mapOf(
            "anim_time" to java.util.function.Function { return@Function reusableAnimTime.also { it.value = animationSeconds.toDouble() } },
            "bedrock_stateful" to java.util.function.Function { params ->
                val group = params.getString(0)
                val animation = params.getString(1)
                var preventsIdle = false
                var posePauser = false
                for (i in 2 until params.params.size) {
                    val value = params.getString(i).lowercase()
                    if (value.equals("prevents_idle")) {
                        preventsIdle = true
                    } else if (value == "pauses_pose") {
                        posePauser = true
                    }
                }
                val anim = currentModel?.bedrockStateful(group, animation) ?: return@Function ObjectValue<Int>(0)
                anim.setPreventsIdle(preventsIdle)
                anim.isPosePauserAnimation(posePauser)
                return@Function ObjectValue(anim).addFunction(
                    "prevents_idle", java.util.function.Function { anim.setPreventsIdle(it.getString(0).toBoolean()) }
                ).addFunction(
                    "pauses_pose", java.util.function.Function { anim.isPosePauserAnimation = it.getString(0).toBoolean() }
                )
            },
            "pose_type" to java.util.function.Function { return@Function StringValue((getEntity() as Poseable).getPoseType().name) },
            "pose" to java.util.function.Function { _ -> return@Function StringValue(currentPose ?: "") },
            "say" to java.util.function.Function { params -> MinecraftClient.getInstance().player?.sendMessage(params.getString(0).text()) ?: Unit },
            "sound" to java.util.function.Function { params ->
                val entity = getEntity() ?: return@Function Unit
                if (params.get<MoValue>(0) !is StringValue) {
                    return@Function Unit
                }
                val soundEvent = SoundEvent.of(params.getString(0).asIdentifierDefaultingNamespace())
                if (soundEvent != null) {
                    val volume = if (params.contains(1)) params.getDouble(1).toFloat() else 1F
                    val pitch = if (params.contains(2)) params.getDouble(2).toFloat() else 1F
                    MinecraftClient.getInstance().soundManager.play(
                        PositionedSoundInstance(soundEvent, SoundCategory.NEUTRAL, volume, pitch, entity.world.random, entity.x, entity.y, entity.z)
                    )
                }
            },
            "random" to java.util.function.Function { params ->
                val options = mutableListOf<MoValue>()
                var index = 0
                while (params.contains(index)) {
                    options.add(params.get(index))
                    index++
                }
                return@Function options.random() // Can throw an exception if they specified no args. They'd be idiots though.
            },
            "animation" to java.util.function.Function { params ->
                val animationParameter = params.get<MoValue>(0)
                val animation = if (animationParameter is ObjectValue<*>) {
                    animationParameter.obj as BedrockStatefulAnimation<T>
                } else {
                    currentModel?.getAnimation(this, animationParameter.asString(), runtime)
                }
                if (animation != null) {
                    statefulAnimations.add(animation)
                }
                return@Function Unit
            },
            "particle" to java.util.function.Function { params ->
                val particlesParam = params.get<MoValue>(0)
                val particles = mutableListOf<String>()
                when (particlesParam) {
                    is StringValue -> particles.add(particlesParam.value)
                    is VariableStruct -> particles.addAll(particlesParam.map.values.map { it.asString() })
                    else -> return@Function Unit
                }

                val effectIds = particles.map { it.asIdentifierDefaultingNamespace() }
                for (effectId in effectIds) {
                    val locator = if (params.params.size > 1) params.getString(1) else "root"
                    val effect = BedrockParticleEffectRepository.getEffect(effectId) ?: run {
                        LOGGER.error("Unable to find a particle effect with id $effectId")
                        return@Function Unit
                    }

                    val entity = getEntity() ?: return@Function Unit
                    val world = entity.world as ClientWorld
                    val matrixWrapper = locatorStates[locator] ?: locatorStates["root"]!!

                    val particleRuntime = MoLangRuntime()
                    particleRuntime.environment.structs["query"] = runtime.environment.getQueryStruct()

                    for (index in 2 until params.params.size) {
                        val variableName = params.getString(index) ?: continue
                        particleRuntime.environment.setSimpleVariable(
                            variableName,
                            runtime.resolve("v.$variableName".asExpression())
                        )
                    }

                    val storm = ParticleStorm(
                        effect = effect,
                        matrixWrapper = matrixWrapper,
                        world = world,
                        runtime = particleRuntime,
                        sourceVelocity = { entity.velocity },
                        sourceAlive = { !entity.isRemoved },
                        sourceVisible = { !entity.isInvisible }
                    )

                    storm.spawn()
                }
            }
        ))
    }

    val allStatefulAnimations: List<StatefulAnimation<T, *>> get() = statefulAnimations + quirks.flatMap { it.value.animations }

    protected var age = 0
    protected var currentPartialTicks = 0F

    var primaryOverridePortion = 1F

    abstract fun getEntity(): T?
    fun getPartialTicks() = currentPartialTicks
    open fun updateAge(age: Int) {
        this.age = age
    }

    open fun incrementAge(entity: T) {
        val previousAge = age
        updateAge(age + 1)
        runEffects(entity, previousAge, age)
    }

    abstract fun updatePartialTicks(partialTicks: Float)
    open fun reset() {
        updateAge(0)
    }

    val animationSeconds: Float get() = (age + getPartialTicks()) / 20F

    var timeEnteredPose = 0F

    val locatorStates = mutableMapOf<String, MatrixWrapper>()

    val renderQueue = ConcurrentLinkedQueue<() -> Unit>()

    /**
     * Scans through the set of animations provided and begins playing the first one that is registered
     * on the entity. The goal is to have most-specific animations first and more generic ones last, so
     * where detailed animations exist they will be used and where they don't there is still a fallback.
     *
     * E.g. ['thunderbolt', 'electric', 'special']
     */
    fun addFirstAnimation(animation: Set<String>) {
        val model = currentModel ?: return
        val animation = animation.firstNotNullOfOrNull { model.getAnimation(this, it, runtime) } ?: return
        statefulAnimations.add(animation)
    }

    fun isPosedIn(vararg poses: Pose<T, in ModelFrame>) = poses.any { it.poseName == currentPose }
    fun isNotPosedIn(vararg poses: Pose<T, in ModelFrame>) = poses.none { it.poseName == currentPose }

    fun preRender() {
        while (renderQueue.peek() != null) {
            val action = renderQueue.poll()
            action()
        }
    }

    fun doLater(action: () -> Unit) {
        renderQueue.offer(action)
    }

    fun getPose(): String? {
        return currentPose
    }

    fun setPose(pose: String) {
        currentPose = pose
        primaryOverridePortion = 1F
        val model = currentModel
        if (model != null) {
            val poseImpl = model.getPose(pose) ?: return
            poseParticles.removeIf { particle -> poseImpl.idleAnimations.filterIsInstance<BedrockStatelessAnimation<*>>().flatMap { it.particleKeyFrames }.none(particle::isSameAs) }
            poseImpl.onTransitionedInto(this)
            val entity = getEntity()
            if (entity != null) {
                poseImpl.idleAnimations
                    .filterIsInstance<BedrockStatelessAnimation<*>>()
                    .flatMap { it.particleKeyFrames }
                    .filter { particle -> particle.seconds == 0F && poseParticles.none(particle::isSameAs) }
                    .forEach { it.run(entity, this) }
            }
        }
    }

    fun setStatefulAnimations(vararg animations: StatefulAnimation<T, out ModelFrame>) {
        statefulAnimations.clear()
        statefulAnimations.addAll(animations)
    }

    fun updateLocatorPosition(position: Vec3d) {
        locatorStates.values.toList().forEach { it.updatePosition(position) }
    }

    fun addStatefulAnimation(animation: StatefulAnimation<T, *>, whenComplete: (state: PoseableEntityState<T>) -> Unit = {}) {
        this.statefulAnimations.add(animation)
        val duration = animation.duration
        if (duration > 0F) {
            after(seconds = (duration * 20F).toInt() / 20F) {
                whenComplete(this)
            }
        }
    }

    fun addPrimaryAnimation(primaryAnimation: PrimaryAnimation<T>) {
        this.primaryAnimation = primaryAnimation
        this.statefulAnimations.clear()
        this.quirks.clear()
        this.primaryOverridePortion = 1F
        primaryAnimation.started = animationSeconds
    }

    fun runEffects(entity: T, previousAge: Int, newAge: Int) {
        val previousSeconds = previousAge / 20F
        val currentSeconds = newAge / 20F

        currentModel?.let { model ->
            val pose = currentPose?.let(model::getPose)
            allStatefulAnimations.forEach { it.applyEffects(entity, this, previousSeconds, currentSeconds) }
            primaryAnimation?.animation?.applyEffects(entity, this, previousSeconds, currentSeconds)
            pose?.idleAnimations?.filter { shouldIdleRun(it, 0.5F) }
        }
    }

    fun shouldIdleRun(idleAnimation: StatelessAnimation<T, *>, requiredIntensity: Float): Boolean {
        val primaryAnimation = primaryAnimation
        return if (primaryAnimation != null) {
            !primaryAnimation.prevents(idleAnimation) && this.primaryOverridePortion >= requiredIntensity
        } else {
            true
        }
    }

    fun getIdleIntensity(idleAnimation: StatelessAnimation<T, *>): Float {
        val primaryAnimation = primaryAnimation
        return if (primaryAnimation != null && primaryAnimation.prevents(idleAnimation)) {
            this.primaryOverridePortion
        } else {
            1F
        }
    }
}