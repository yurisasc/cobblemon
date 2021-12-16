package com.cablemc.pokemoncobbled.client.render.models.blockbench

import com.cablemc.pokemoncobbled.client.render.models.blockbench.animation.*
import com.cablemc.pokemoncobbled.client.render.models.blockbench.frame.ModelFrame
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pose.Pose
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pose.PoseType
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pose.TransformedModelPart
import com.cablemc.pokemoncobbled.client.render.models.blockbench.wavefunction.LineFunction
import com.cablemc.pokemoncobbled.client.render.pokemon.PokemonRenderer.Companion.DELTA_TICKS
import com.cablemc.pokemoncobbled.mod.PokemonCobbledMod.LOGGER
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.world.entity.Entity

/**
 * A model that can be posed and animated using [StatelessAnimation]s and [StatefulAnimation]s. This
 * requires poses to be registered and should implement any [ModelFrame] interfaces that apply to this
 * model. Implementing the render functions is possible but not necessary.
 *
 * @author Hiroku
 * @since December 5th, 2021
 */
abstract class PoseableEntityModel<T : Entity> : EntityModel<T>(), ModelFrame {
    val poses = mutableMapOf<PoseType, Pose<T, out ModelFrame>>()
    /**
     * A list of [TransformedModelPart] that are relevant to any frame or animation.
     * This allows the original rotations to be reset.
     */
    val relevantParts = mutableListOf<TransformedModelPart>()
    /** Registers the different poses this model is capable of ahead of time. Should use [registerPose] religiously. */
    abstract fun registerPoses()
    /** Gets the [PoseableEntityState] for an entity. */
    abstract fun getState(entity: T): PoseableEntityState<T>

    /**
     * Registers a pose for this model.
     *
     * @param poseType The type of pose it is, as a [PoseType]
     * @param condition The condition for this pose to apply
     * @param idleAnimations The stateless animations to use as idles unless a [StatefulAnimation] prevents it.
     * @param transformedParts All the transformed forms of parts of the body that define this pose.
     */
    fun <F : ModelFrame> registerPose(
        poseType: PoseType,
        condition: (T) -> Boolean,
        idleAnimations: Array<StatelessAnimation<T, out F>>,
        transformedParts: Array<TransformedModelPart>
    ) {
        poses[poseType] = Pose(poseType, condition, idleAnimations, transformedParts)
    }

    fun registerRelevantPart(part: ModelPart): ModelPart {
        relevantParts.add(part.asTransformed())
        return part
    }

    override fun renderToBuffer(stack: PoseStack, buffer: VertexConsumer, packedLight: Int, packedOverlay: Int, r: Float, g: Float, b: Float, a: Float) {
        rootPart.render(stack, buffer, packedLight, packedOverlay, r, g, b, a)
    }

    /** Applies the given pose type to the model, if there is a matching pose. */
    fun applyPose(pose: PoseType) = poses[pose]?.transformedParts?.forEach { it.apply() }
    /** Puts the model back to its original location and rotations. */
    fun setDefault() = relevantParts.forEach { it.applyDefaults() }

    /**
     * Sets up the angles and positions for the model knowing that there is no state. Is given a pose type to use,
     * and optionally things like limb swinging and head rotations.
     */
    fun setupAnimStateless(poseType: PoseType, limbSwing: Float = 0F, limbSwingAmount: Float = 0F, headYaw: Float = 0F, headPitch: Float = 0F) {
        setDefault()
        val pose = poses[poseType] ?: poses.values.first()
        pose.idleStateless(this, limbSwing, limbSwingAmount, 0F, headYaw, headPitch)
    }

    override fun setupAnim(entity: T, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float, pNetHeadYaw: Float, pHeadPitch: Float) {
        setDefault()
        val state = getState(entity)
        state.animationTick += DELTA_TICKS
        state.currentModel = this
        var poseType = state.getPose()
        var pose = poses[poseType]
        if (poseType == null || pose == null || !pose.condition(entity)) {
            if (poseType == null || pose == null || state.statefulAnimations.none { it is PoseTransitionAnimation }) {
                val previousPose = pose
                pose = poses.values.firstOrNull { it.condition(entity) } ?: run {
                    LOGGER.error("Could not get any suitable pose for ${this::class.simpleName}!")
                    return@run Pose(PoseType.NONE, { true }, emptyArray(), emptyArray())
                }
                poseType = pose.poseType

                if (previousPose != null) {
                    state.statefulAnimations.add(
                        PoseTransitionAnimation(
                            frame = this,
                            beforePose = previousPose,
                            afterPose = pose,
                            durationTicks = 30
                        )
                    )
                } else {
                    getState(entity).setPose(poseType)
                }
            }
        }

        applyPose(poseType)
        pose.idleStateful(entity, this, limbSwing, limbSwingAmount, ageInTicks, pNetHeadYaw, pHeadPitch)
        getState(entity).applyAdditives(entity, this)
        state.statefulAnimations.removeIf { !it.run(entity, this) }
    }

    fun ModelPart.translation(
        function: LineFunction,
        axis: Int
    ) = TranslationFunctionStatelessAnimation<T>(
        part = this,
        function = function,
        axis = axis,
        frame = this@PoseableEntityModel
    )

    fun ModelPart.rotation(
        function: LineFunction,
        axis: Int
    ) = RotationFunctionStatelessAnimation<T>(
        part = this,
        function = function,
        axis = axis,
        frame = this@PoseableEntityModel
    )
}