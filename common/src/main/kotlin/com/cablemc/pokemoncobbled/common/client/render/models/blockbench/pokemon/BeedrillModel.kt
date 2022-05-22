package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.asTransformed
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.*
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.PoseType
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.TransformedModelPart.Companion.Y_AXIS
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonBehaviourFlag
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class BeedrillModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame, BiWingedFrame {
    override val rootPart = root.registerChildWithSpecificChildren("beedrill", listOf("body","antenna_right","antenna_right2","antenna_left","antenna_left2","wing_right","wing_right2","wing_left","wing_left2","abdomen","arm_right","arm_left","leg_right","leg_left","leg_left2","leg_right2","head_AI","head","arm_right2","arm_left2"))
    override val head = getPart("head")
    override val leftWing = getPart("wing_left")
    override val rightWing = getPart("wing_right")
    val leftWingBack = getPart("wing_left2")
    val rightWingBack = getPart("wing_right2")

    override val portraitScale = 1.5F
    override val portraitTranslation = Vec3d(0.1, 0.2, 0.0)

    override val profileScale = 1.0F
    override val profileTranslation = Vec3d(0.0, 0.0, 0.0)

    override fun registerPoses() {
        registerPose(
            poseName = "standing",
            poseTypes = setOf(PoseType.NONE, PoseType.PROFILE),
            transformTicks = 10,
            condition = { !it.isMoving.get() },
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("beedrill", "air_idle")
            ),
            transformedParts = arrayOf()
        )

        registerPose(
            poseType = PoseType.FLY,
            transformTicks = 10,
            condition = { !it.isMoving.get() },
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("beedrill", "air_idle")
            ),
            transformedParts = arrayOf(rootPart.asTransformed().addPosition(Y_AXIS, -2F))
        )

        registerPose(
            poseType = PoseType.SWIM,
            transformTicks = 10,
            condition = { it.isMoving.get() },
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("beedrill", "air_fly")
            ),
            transformedParts = arrayOf(rootPart.asTransformed().addPosition(Y_AXIS, 6F))
        )
        registerPose(
            poseType = PoseType.WALK,
            transformTicks = 10,
            condition = { it.isMoving.get() },
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("beedrill", "air_idle"),
                bedrock("beedrill", "air_fly")
            ),
            transformedParts = emptyArray()
        )
    }
}