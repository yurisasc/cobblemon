package com.cobblemon.mod.common.client.render.models.blockbench

import com.bedrockk.molang.runtime.MoLangRuntime
import com.cobblemon.mod.common.api.molang.MoLangFunctions.setup
import com.cobblemon.mod.common.api.scheduling.Schedulable
import com.cobblemon.mod.common.client.render.MatrixWrapper
import com.cobblemon.mod.common.client.render.models.blockbench.animation.PrimaryAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.animation.StatefulAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.animation.StatelessAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.bedrock.animation.BedrockParticleKeyframe
import com.cobblemon.mod.common.client.render.models.blockbench.bedrock.animation.BedrockStatelessAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.frame.ModelFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pose.Pose
import com.cobblemon.mod.common.client.render.models.blockbench.quirk.ModelQuirk
import com.cobblemon.mod.common.client.render.models.blockbench.quirk.QuirkData
import java.util.concurrent.ConcurrentLinkedQueue
import net.minecraft.util.math.Vec3d

abstract class PosableState : Schedulable {
    var currentModel: PosableModel? = null
    var currentPose: String? = null
    var primaryAnimation: PrimaryAnimation? = null
    val statefulAnimations: MutableList<StatefulAnimation> = mutableListOf()
    val quirks = mutableMapOf<ModelQuirk<*>, QuirkData>()
    val poseParticles = mutableListOf<BedrockParticleKeyframe>()
    val runtime = MoLangRuntime().setup().also {
        it.environment.structs["query"] = it.environment.structs["variable"]
    }

    val allStatefulAnimations: List<StatefulAnimation> get() = statefulAnimations + quirks.flatMap { it.value.animations }

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

    fun setStatefulAnimations(vararg animations: StatefulAnimation) {
        statefulAnimations.clear()
        statefulAnimations.addAll(animations)
    }

    fun updateLocatorPosition(position: Vec3d) {
        locatorStates.values.toList().forEach { it.updatePosition(position) }
    }

    fun addStatefulAnimation(animation: StatefulAnimation<T, *>, whenComplete: (state: PosableState) -> Unit = {}) {
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

    fun shouldIdleRun(idleAnimation: StatelessAnimation, requiredIntensity: Float): Boolean {
        val primaryAnimation = primaryAnimation
        return if (primaryAnimation != null) {
            !primaryAnimation.prevents(idleAnimation) && this.primaryOverridePortion >= requiredIntensity
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