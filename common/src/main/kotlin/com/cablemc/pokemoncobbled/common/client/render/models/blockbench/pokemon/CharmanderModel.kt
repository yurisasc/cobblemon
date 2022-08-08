package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.PoseableEntityState
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.asTransformed
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.BimanualFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.BipedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.HeadedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.TransformedModelPart.Companion.X_AXIS
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.TransformedModelPart.Companion.Y_AXIS
import com.cablemc.pokemoncobbled.common.entity.PoseType
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonBehaviourFlag
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class CharmanderModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame, BipedFrame, BimanualFrame {
    override val rootPart = root.registerChildWithAllChildren("charmander")
    override val head = getPart("head")
    override val rightArm = getPart("arm_right")
    override val leftArm = getPart("arm_left")
    override val rightLeg = getPart("leg_right")
    override val leftLeg = getPart("leg_left")
    val fire = getPart("fire")
    val tail1 = getPart("tail")
    val tail2 = getPart("tail2")
    val body = getPart("body")
    val torso = getPart("torso")
    val jaw = getPart("jaw")
    val rightEyelid = getPart("eyelid_right")
    val leftEyelid = getPart("eyelid_left")

    override val portraitScale = 1.5F
    override val portraitTranslation = Vec3d(0.05, 0.3, 0.0)

    override val profileScale = 0.9F
    override val profileTranslation = Vec3d(0.0, 0.35, 0.0)

    lateinit var sleep: PokemonPose
    lateinit var standing: PokemonPose
    lateinit var walk: PokemonPose

    override fun registerPoses() {
        sleep = registerPose(
            poseType = PoseType.SLEEP,
            condition = { it.getBehaviourFlag(PokemonBehaviourFlag.RESTING) },
            transformTicks = 10,
            idleAnimations = emptyArray(),
            transformedParts = arrayOf(
                head.asTransformed().addRotationDegrees(-63.5, 14.5, -26.5),
                body.asTransformed().addRotationDegrees(X_AXIS, 82.5).addPosition(Y_AXIS, 5.75),
                torso.asTransformed().addRotationDegrees(X_AXIS, 5),
                leftLeg.asTransformed().addRotationDegrees(-28.0738, -86.7652, 12.6404),
                rightLeg.asTransformed().addRotationDegrees(-47.4912, 81.43, -4.2682).addPosition(Y_AXIS, -0.5),
                leftArm.asTransformed().addRotationDegrees(-4.25, 14.75, -107.5).addPosition(Y_AXIS, 1.25),
                rightArm.asTransformed().addRotationDegrees(-14.3595, -16.2041, 70.1473).addPosition(0.5, 1.75, 0),
                tail1.asTransformed().addRotationDegrees(-109.9359, 0.1998, -1.2082).addPosition(0, 1, -1),
                tail2.asTransformed().addRotationDegrees(11.4819, -46.2281, -1.1945),
                leftEyelid.asTransformed().addPosition(0.1, 0, -0.1),
                rightEyelid.asTransformed().addPosition(-0.1, 0, -0.1),
                jaw.asTransformed().addRotationDegrees(X_AXIS, 5)
            )
        )

        standing = registerPose(
            poseName = "standing",
            poseTypes = setOf(PoseType.NONE, PoseType.STAND, PoseType.PROFILE, PoseType.PORTRAIT),
            transformTicks = 5,
            condition = { !it.isMoving.get() && !it.getBehaviourFlag(PokemonBehaviourFlag.RESTING)},
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("charmander", "ground_idle")
            )
        )

        walk = registerPose(
            poseType = PoseType.WALK,
            transformTicks = 5,
            condition = { it.isMoving.get() },
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("charmander", "ground_idle"),
                bedrock("charmander", "ground_walk")
            )
        )
    }

    override fun getFaintAnimation(
        pokemonEntity: PokemonEntity,
        state: PoseableEntityState<PokemonEntity>
    ) = if (state.isNotPosedIn(sleep)) bedrockStateful("charmander", "faint") else null
}