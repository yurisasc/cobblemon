/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench

import com.bedrockk.molang.runtime.MoLangRuntime
import com.bedrockk.molang.runtime.struct.QueryStruct
import com.bedrockk.molang.runtime.struct.VariableStruct
import com.bedrockk.molang.runtime.value.DoubleValue
import com.bedrockk.molang.runtime.value.MoValue
import com.bedrockk.molang.runtime.value.StringValue
import com.cobblemon.mod.common.Cobblemon.LOGGER
import com.cobblemon.mod.common.api.molang.MoLangFunctions.addFunction
import com.cobblemon.mod.common.api.molang.MoLangFunctions.addFunctions
import com.cobblemon.mod.common.api.molang.MoLangFunctions.getQueryStruct
import com.cobblemon.mod.common.api.molang.MoLangFunctions.setup
import com.cobblemon.mod.common.api.molang.ObjectValue
import com.cobblemon.mod.common.api.scheduling.Schedulable
import com.cobblemon.mod.common.client.ClientMoLangFunctions.setupClient
import com.cobblemon.mod.common.client.particle.BedrockParticleEffectRepository
import com.cobblemon.mod.common.client.particle.ParticleStorm
import com.cobblemon.mod.common.client.render.MatrixWrapper
import com.cobblemon.mod.common.client.render.models.blockbench.animation.PrimaryAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.animation.StatefulAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.animation.StatelessAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.bedrock.animation.BedrockParticleKeyframe
import com.cobblemon.mod.common.client.render.models.blockbench.bedrock.animation.BedrockStatefulAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.bedrock.animation.BedrockStatelessAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.pose.Pose
import com.cobblemon.mod.common.client.render.models.blockbench.quirk.ModelQuirk
import com.cobblemon.mod.common.client.render.models.blockbench.quirk.QuirkData
import com.cobblemon.mod.common.entity.Poseable
import com.cobblemon.mod.common.util.asIdentifierDefaultingNamespace
import java.util.concurrent.ConcurrentLinkedQueue
import net.minecraft.client.MinecraftClient
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.util.math.Vec3d

abstract class PosableState : Schedulable {
    var currentModel: PosableModel? = null
        set(value) {
            field = value
            if (value != null) {
                val entity = getEntity() as? Poseable ?: return
                entity.struct.addFunctions(value.functions.functions)
                runtime.environment.getQueryStruct().addFunctions(value.functions.functions)
            }
        }
    var currentPose: String? = null
    var primaryAnimation: PrimaryAnimation? = null
    val statefulAnimations: MutableList<StatefulAnimation> = mutableListOf()
    val quirks = mutableMapOf<ModelQuirk<*>, QuirkData>()
    val poseParticles = mutableListOf<BedrockParticleKeyframe>()

    private val reusableAnimTime = DoubleValue(0.0) // This gets called 500 million times so use a mutable value for runtime


    /*
    val runtime = MoLangRuntime().setup().setupClient().also { runtime ->
        val reusableAnimTime = DoubleValue(0.0) // This gets called 500 million times so use a mutable value for runtime
        runtime.environment.getQueryStruct().addFunctions(mapOf(
            "anim_time" to java.util.function.Function { return@Function reusableAnimTime.also { it.value = animationSeconds.toDouble() } },
            "pose_type" to java.util.function.Function { return@Function StringValue((getEntity() as Poseable).getCurrentPoseType().name) },
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
            "play_animation" to java.util.function.Function { params ->
                val animationParameter = params.get<MoValue>(0)
                val animation = if (animationParameter is ObjectValue<*>) {
                    animationParameter.obj as BedrockStatefulAnimation<T>
                } else {
                    currentModel?.getAnimation(this, animationParameter.asString(), runtime)
                }
                if (animation != null) {
                    if (animation is PrimaryAnimation<T>) {
                        addPrimaryAnimation(animation)
                    } else {
                        addStatefulAnimation(animation)
                    }
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

                    val particleRuntime = MoLangRuntime().setup().setupClient()
                    particleRuntime.environment.getQueryStruct().addFunction("entity") { runtime.environment.getQueryStruct() }

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
     */
    val functions = QueryStruct(hashMapOf())
        .addFunction("anim_time") {
            reusableAnimTime.value = animationSeconds.toDouble()
            reusableAnimTime
        }
        .addFunction("pose") { StringValue(currentPose ?: "") }
        .addFunction("sound") { params ->
            val entity = getEntity() ?: return@addFunction Unit
            if (params.get<MoValue>(0) !is StringValue) {
                return@addFunction Unit
            }
            val soundEvent = SoundEvent.of(params.getString(0).asIdentifierDefaultingNamespace())
            if (soundEvent != null) {
                val volume = if (params.contains(1)) params.getDouble(1).toFloat() else 1F
                val pitch = if (params.contains(2)) params.getDouble(2).toFloat() else 1F
                MinecraftClient.getInstance().soundManager.play(
                    PositionedSoundInstance(soundEvent, SoundCategory.NEUTRAL, volume, pitch, entity.world.random, entity.x, entity.y, entity.z)
                )
            }
        }
        .addFunction("play_animation") { params ->
            val animationParameter = params.get<MoValue>(0)
            val animation = if (animationParameter is ObjectValue<*>) {
                animationParameter.obj as BedrockStatefulAnimation
            } else {
                currentModel?.getAnimation(this, animationParameter.asString(), runtime)
            }
            if (animation != null) {
                if (animation is PrimaryAnimation) {
                    addPrimaryAnimation(animation)
                } else {
                    addStatefulAnimation(animation)
                }
            }
            return@addFunction Unit
        }
        .addFunction("particle") { params ->
            val particlesParam = params.get<MoValue>(0)
            val particles = mutableListOf<String>()
            when (particlesParam) {
                is StringValue -> particles.add(particlesParam.value)
                is VariableStruct -> particles.addAll(particlesParam.map.values.map { it.asString() })
                else -> return@addFunction Unit
            }

            val effectIds = particles.map { it.asIdentifierDefaultingNamespace() }
            for (effectId in effectIds) {
                val locator = if (params.params.size > 1) params.getString(1) else "root"
                val effect = BedrockParticleEffectRepository.getEffect(effectId) ?: run {
                    LOGGER.error("Unable to find a particle effect with id $effectId")
                    return@addFunction Unit
                }

                val entity = getEntity() ?: return@addFunction Unit
                val world = entity.world as ClientWorld
                val matrixWrapper = locatorStates[locator] ?: locatorStates["root"]!!

                val particleRuntime = MoLangRuntime().setup().setupClient()
                particleRuntime.environment.getQueryStruct().addFunction("entity") { runtime.environment.getQueryStruct() }

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

    val runtime: MoLangRuntime = MoLangRuntime().setup().setupClient().also {
        it.environment.getQueryStruct().addFunctions(functions.functions)
    }

    val allStatefulAnimations: List<StatefulAnimation> get() = statefulAnimations + quirks.flatMap { it.value.animations }

    protected var age = 0
    protected var currentPartialTicks = 0F

    var primaryOverridePortion = 1F

    abstract fun getEntity(): Entity?
    fun getPartialTicks() = currentPartialTicks
    open fun updateAge(age: Int) {
        this.age = age
    }

    open fun incrementAge(entity: Entity) {
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
        if (animation is PrimaryAnimation) {
            addPrimaryAnimation(animation)
        } else {
            addStatefulAnimation(animation)
        }
    }

    fun isPosedIn(vararg poses: Pose) = poses.any { it.poseName == currentPose }
    fun isNotPosedIn(vararg poses: Pose) = poses.none { it.poseName == currentPose }

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
            poseParticles.removeIf { particle -> poseImpl.idleAnimations.filterIsInstance<BedrockStatelessAnimation>().flatMap { it.particleKeyFrames }.none(particle::isSameAs) }
            poseImpl.onTransitionedInto(this)
            val entity = getEntity()
            if (entity != null) {
                poseImpl.idleAnimations
                    .filterIsInstance<BedrockStatelessAnimation>()
                    .flatMap { it.particleKeyFrames }
                    .filter { particle -> particle.seconds == 0F && poseParticles.none(particle::isSameAs) }
                    .forEach { it.run(entity, this) }
            }
        }
    }

    fun setStatefulAnimations(vararg animations: StatefulAnimation) {
        statefulAnimations.clear()
        statefulAnimations.addAll(animations)
    }

    fun updateLocatorPosition(position: Vec3d) {
        locatorStates.values.toList().forEach { it.updatePosition(position) }
    }

    fun addStatefulAnimation(animation: StatefulAnimation, whenComplete: (state: PosableState) -> Unit = {}) {
        this.statefulAnimations.add(animation)
        val duration = animation.duration
        if (duration > 0F) {
            after(seconds = (duration * 20F).toInt() / 20F) {
                whenComplete(this)
            }
        }
    }

    fun addPrimaryAnimation(primaryAnimation: PrimaryAnimation) {
        this.primaryAnimation = primaryAnimation
        this.statefulAnimations.clear()
        this.quirks.clear()
        this.primaryOverridePortion = 1F
        primaryAnimation.started = animationSeconds
    }

    fun runEffects(entity: Entity, previousAge: Int, newAge: Int) {
        val previousSeconds = previousAge / 20F
        val currentSeconds = newAge / 20F

        currentModel?.let { model ->
            val pose = currentPose?.let(model::getPose)
            allStatefulAnimations.forEach { it.applyEffects(entity, this, previousSeconds, currentSeconds) }
            primaryAnimation?.animation?.applyEffects(entity, this, previousSeconds, currentSeconds)
            pose?.idleAnimations?.filter { shouldIdleRun(it, 0.5F) }
        }
    }

    fun shouldIdleRun(idleAnimation: StatelessAnimation, requiredIntensity: Float): Boolean {
        val primaryAnimation = primaryAnimation
        return if (primaryAnimation != null) {
            !primaryAnimation.prevents(idleAnimation) || this.primaryOverridePortion > requiredIntensity
        } else {
            true
        }
    }

    fun getIdleIntensity(idleAnimation: StatelessAnimation): Float {
        val primaryAnimation = primaryAnimation
        return if (primaryAnimation != null && primaryAnimation.prevents(idleAnimation)) {
            this.primaryOverridePortion
        } else {
            1F
        }
    }
}