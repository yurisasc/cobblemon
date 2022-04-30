package com.cablemc.pokemoncobbled.common.client.render.models.blockbench

import com.cablemc.pokemoncobbled.common.PokemonCobbled.LOGGER
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.PoseTransitionAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.RotationFunctionStatelessAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.StatelessAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.TranslationFunctionStatelessAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.ModelFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.Pose
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.PoseType
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.TransformedModelPart
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.wavefunction.WaveFunction
import net.minecraft.client.model.ModelPart
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.entity.model.EntityModel
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.Entity
import net.minecraft.util.Identifier

/**
 * A model that can be posed and animated using [StatelessAnimation]s and [StatefulAnimation]s. This
 * requires poses to be registered and should implement any [ModelFrame] interfaces that apply to this
 * model. Implementing the render functions is possible but not necessary.
 *
 * @author Hiroku
 * @since December 5th, 2021
 */
abstract class PoseableEntityModel<T : Entity>(
    renderTypeFunc: (Identifier) -> RenderLayer = RenderLayer::getEntityCutout
) : EntityModel<T>(renderTypeFunc), ModelFrame {
    var currentEntity: T? = null

    val poses = mutableMapOf<PoseType, Pose<T, out ModelFrame>>()
    /**
     * A list of [TransformedModelPart] that are relevant to any frame or animation.
     * This allows the original rotations to be reset.
     */
    val relevantParts = mutableListOf<TransformedModelPart>()
    val relevantPartsByName = mutableMapOf<String, TransformedModelPart>()
    /** Registers the different poses this model is capable of ahead of time. Should use [registerPose] religiously. */
    abstract fun registerPoses()
    /** Gets the [PoseableEntityState] for an entity. */
    abstract fun getState(entity: T): PoseableEntityState<T>

    fun scaleForPart(part: ModelPart, value: Float) = (relevantParts.find { it.modelPart == part }?.changeFactor ?: 1F) * value

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
        condition: (T) -> Boolean = { true },
        transformTicks: Int = 20,
        idleAnimations: Array<StatelessAnimation<T, out F>>,
        transformedParts: Array<TransformedModelPart>
    ) {
        poses[poseType] = Pose(poseType, condition, transformTicks, idleAnimations, transformedParts)
    }

    fun registerRelevantPart(name: String, part: ModelPart): ModelPart {
        val transformedPart = part.asTransformed()
        relevantParts.add(transformedPart)
        relevantPartsByName[name] = transformedPart
        return part
    }

    fun registerRelevantPart(pairing: Pair<String, ModelPart>) = registerRelevantPart(pairing.first, pairing.second)

    override fun render(stack: MatrixStack, buffer: VertexConsumer, packedLight: Int, packedOverlay: Int, r: Float, g: Float, b: Float, a: Float) {
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
    fun setupAnimStateless(poseType: PoseType, limbSwing: Float = 0F, limbSwingAmount: Float = 0F, headYaw: Float = 0F, headPitch: Float = 0F, ageInTicks: Float = 0F) {
        currentEntity = null
        setDefault()
        val pose = poses[poseType] ?: poses.values.first()
        pose.transformedParts.forEach { it.apply() }
        pose.idleStateless(this, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch)
    }

    override fun setAngles(entity: T, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float, pNetHeadYaw: Float, pHeadPitch: Float) {
        currentEntity = entity
        setDefault()
        val state = getState(entity)
        state.preRender()
        state.currentModel = this
        var poseType = state.getPose()
        var pose = poses[poseType]

        if (poseType == null || pose == null || !pose.condition(entity)) {
            val previousPose = pose
            val desirablePose = poses.values.firstOrNull { it.condition(entity) } ?: run {
                LOGGER.error("Could not get any suitable pose for ${this::class.simpleName}!")
                return@run Pose(PoseType.NONE, { true }, 0, emptyArray(), emptyArray())
            }
            val desirablePoseType = desirablePose.poseType

            // If this condition matches then it just no longer fits this pose
            if (pose != null && poseType != null) {
                if (state.statefulAnimations.none { it is PoseTransitionAnimation<*> }) {
                    if (previousPose != null && pose.transformTicks > 0) {
                        state.statefulAnimations.add(
                            PoseTransitionAnimation(
                                beforePose = previousPose,
                                afterPose = desirablePose,
                                durationTicks = pose.transformTicks
                            )
                        )
                    } else {
                        getState(entity).setPose(desirablePoseType)
                    }
                }
            } else {
                pose = desirablePose
                poseType = desirablePoseType
                getState(entity).setPose(desirablePoseType)
            }
        }

        applyPose(poseType)
        state.statefulAnimations.removeIf { !it.run(entity, this) }
        pose.idleStateful(entity, this, limbSwing, limbSwingAmount, ageInTicks, pNetHeadYaw, pHeadPitch)
        getState(entity).applyAdditives(entity, this)
    }

    fun ModelPart.translation(
        function: WaveFunction,
        axis: Int,
        timeVariable: (state: PoseableEntityState<T>?, limbSwing: Float, ageInTicks: Float) -> Float?
    ) = TranslationFunctionStatelessAnimation<T>(
        part = this,
        function = function,
        axis = axis,
        timeVariable = timeVariable,
        frame = this@PoseableEntityModel
    )

    fun ModelPart.rotation(
        function: WaveFunction,
        axis: Int,
        timeVariable: (state: PoseableEntityState<T>?, limbSwing: Float, ageInTicks: Float) -> Float?
    ) = RotationFunctionStatelessAnimation(
        part = this,
        function = function,
        axis = axis,
        timeVariable = timeVariable,
        frame = this@PoseableEntityModel
    )
}