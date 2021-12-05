package com.cablemc.pokemoncobbled.client.render.models.blockbench

import com.cablemc.pokemoncobbled.client.render.models.blockbench.animation.StatelessAnimation
import com.cablemc.pokemoncobbled.client.render.models.blockbench.frame.ModelFrame
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pose.Pose
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pose.PoseType
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pose.RegisteredPose
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pose.TransformedModelPart
import com.cablemc.pokemoncobbled.mod.PokemonCobbledMod.LOGGER
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.world.entity.Entity

abstract class PoseableEntityModel<T : Entity, F : ModelFrame> : EntityModel<T>() {
    val poses = mutableListOf<RegisteredPose<T, F>>()
    val relevantParts = mutableListOf<TransformedModelPart>()

    abstract val rootPart: ModelPart
    abstract val frame: F
    abstract fun registerPoses()
    abstract fun getState(entity: T): PoseableEntityState<T>

    fun registerPose(pose: Pose<T, F>, idleAnimations: List<StatelessAnimation<T, F>>, transformedParts: List<TransformedModelPart>) {
        poses.add(RegisteredPose(pose, frame, idleAnimations.toTypedArray(), transformedParts.toTypedArray()))
    }

    fun registerRelevantPart(part: ModelPart): ModelPart {
        relevantParts.add(part.asTransformed())
        return part
    }

    fun applyPose(pose: Pose<T, F>) = poses.find { it.pose == pose }?.transformedParts?.forEach { it.apply() }

    override fun renderToBuffer(stack: PoseStack, buffer: VertexConsumer, packedLight: Int, packedOverlay: Int, r: Float, g: Float, b: Float, a: Float) {
        rootPart.render(stack, buffer, packedLight, packedOverlay, r, g, b, a)
    }

    fun setDefault() = relevantParts.forEach { it.applyDefaults() }

    override fun setupAnim(entity: T, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float, pNetHeadYaw: Float, pHeadPitch: Float) {
        setDefault()

        var pose = getState(entity).getPose<F>()
        if (pose == null || !pose.fits(entity)) {
            pose = poses.firstOrNull { it.pose.fits(entity) }?.pose ?: run {
                LOGGER.error("Could not get any suitable pose for ${this::class.simpleName}!")
                return@run object : Pose<T, F> {
                    override val poseType: PoseType = PoseType.WALK
                    override fun fits(entity: T) = true
                }
            }
            // TODO animate between poses? Ideally yes, it would probably look pretty sweet
            getState(entity).setPose(pose)
        }

        applyPose(pose)
        val idles = poses.find { it.pose == pose }?.idleAnimations ?: arrayOf(noAnimation())

        idles.forEach { it.setAngles(entity, frame, pose, limbSwing, limbSwingAmount, ageInTicks) }
        getState(entity).applyAdditives(entity, this)
    }

    fun <T : Entity, F : ModelFrame> noAnimation() = object : StatelessAnimation<T, F> {
        override fun setAngles(entity: T, frame: F, pose: Pose<T, F>, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float) {}
    }
}