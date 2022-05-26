package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.EarJoint
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.RangeOfMotion
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.QuadrupedWalkAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.SingleBoneLookAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.EaredFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.HeadedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.QuadrupedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.getChildOf
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.PoseType
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.TransformedModelPart.Companion.Z_AXIS
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonBehaviourFlag
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.cablemc.pokemoncobbled.common.util.math.geometry.toRadians
import net.minecraft.client.model.*
import net.minecraft.client.render.entity.model.EntityModelLayer
import net.minecraft.util.math.Vec3d

class RattataModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame, QuadrupedFrame, EaredFrame {
    override val rootPart = root.registerChildWithSpecificChildren("rattata", listOf("head", "body", "whisker_right", "whisker_left", "ear_right", "ear_left", "tail", "tail2", "leg_front_left", "leg_front_right", "leg_back_left", "leg_back_right", "tail3"
    ))
    override val foreLeftLeg = getPart("leg_front_left")
    override val foreRightLeg = getPart("leg_front_right")
    override val hindLeftLeg = getPart("leg_back_left")
    override val hindRightLeg = getPart("leg_back_right")
    override val head = getPart("head")
    override val leftEarJoint: EarJoint = EarJoint(
            getPart("ear_left"),
            Z_AXIS,
            RangeOfMotion(
                    0F.toRadians(),
                    -20F.toRadians()
            )
    )
    override val rightEarJoint: EarJoint = EarJoint(
            getPart("ear_right"),
            Z_AXIS,
            RangeOfMotion(
                    0F.toRadians(),
                    20F.toRadians()
            )
    )
    override val portraitScale = 2.5F
    override val portraitTranslation = Vec3d(-0.25, -2.03, 0.0)

    override val profileScale = 1.0F
    override val profileTranslation = Vec3d(0.0, 0.0, 0.0)

    init {
        registerPoses()
    }

    override fun registerPoses() {
        registerPose(
                poseName = "standing",
                poseTypes = setOf(PoseType.NONE, PoseType.PROFILE),
                transformTicks = 10,
                condition = { !it.isMoving.get() },
                idleAnimations = arrayOf(
                        singleBoneLook(),
                        bedrock("rattata", "ground_idle")
                ),
                transformedParts = arrayOf()
        )
        registerPose(
                poseType = PoseType.WALK,
                transformTicks = 10,
                condition = { it.isMoving.get() },
                idleAnimations = arrayOf(
                        singleBoneLook(),
                        bedrock("rattata", "ground_idle"),
                        bedrock("rattata", "ground_walk")
                ),
                transformedParts = emptyArray()
        )
    }
}
