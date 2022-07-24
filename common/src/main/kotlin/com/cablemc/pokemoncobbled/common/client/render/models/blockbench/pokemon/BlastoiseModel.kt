package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.EarJoint
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.PoseableEntityState
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.RangeOfMotion
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.StatefulAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.asTransformed
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.*
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.Pose
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.PoseType
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.TransformedModelPart
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.TransformedModelPart.Companion.Y_AXIS
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonBehaviourFlag
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemoncobbled.common.util.math.geometry.toRadians
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class BlastoiseModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame, BipedFrame, BimanualFrame, EaredFrame {
    override val rootPart = root.registerChildWithAllChildren("blastoise")
    override val head = getPart("head")
    override val rightArm = getPart("arm_right")
    override val leftArm = getPart("arm_left")
    override val rightLeg = getPart("leg_right")
    override val leftLeg = getPart("leg_left")
    private val rightEar = getPart("ear_right")
    private val leftEar = getPart("ear_left")
    override val leftEarJoint = EarJoint(leftEar, TransformedModelPart.Z_AXIS, RangeOfMotion(50F.toRadians(), 0F))
    override val rightEarJoint = EarJoint(rightEar, TransformedModelPart.Z_AXIS, RangeOfMotion((-50F).toRadians(), 0F))
//    private val tail = getPart("tail")
//test
    override val portraitScale = 1.35F
    override val portraitTranslation = Vec3d(-0.2, 1.25, 0.0)

    override val profileScale = 1.0F
    override val profileTranslation = Vec3d(0.0, 0.0, 0.0)

    lateinit var standing: PokemonPose
    lateinit var swimIdle: PokemonPose
    lateinit var swimMove: PokemonPose
    lateinit var walk: PokemonPose

    override fun registerPoses() {
        standing = registerPose(
            poseName = "standing",
            poseTypes = setOf(PoseType.NONE, PoseType.PROFILE, PoseType.PORTRAIT),
            transformTicks = 10,
            condition = { !it.isMoving.get() && !it.isSubmergedInWater },
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("blastoise", "ground_idle")
            )
        )

        swimIdle = registerPose(
            poseName = "swim_idle",
            poseTypes = setOf(PoseType.SWIM),
            transformTicks = 10,
            condition = { it.isSubmergedInWater && !it.isMoving.get() },
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("blastoise", "water_idle")
            )
        )

        swimMove = registerPose(
            poseName = "swim_move",
            poseTypes = setOf(PoseType.SWIM),
            transformTicks = 10,
            condition = { it.isSubmergedInWater && it.isMoving.get() },
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("blastoise", "water_swim")
            )
        )

        walk = registerPose(
            poseType = PoseType.WALK,
            transformTicks = 10,
            condition = { it.isMoving.get() && !it.isSubmergedInWater },
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("blastoise", "ground_walk")
            )
        )
    }

    override fun getFaintAnimation(
        pokemonEntity: PokemonEntity,
        state: PoseableEntityState<PokemonEntity>
    ) = if (state.isNotPosedIn(swimMove, swimIdle)) bedrockStateful("blastoise", "faint") else null
}