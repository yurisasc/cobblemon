package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.TexturedModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.SingleBoneLookAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.asTransformed
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.bedrock.animation.BedrockAnimationRepository
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.bedrock.animation.BedrockStatelessAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.childNamed
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.BiWingedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.BimanualFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.BipedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.HeadedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.getChildOf
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.PoseType
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.TransformedModelPart.Companion.Y_AXIS
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonBehaviourFlag
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.client.model.*
import net.minecraft.client.render.entity.model.EntityModelLayer
import net.minecraft.util.math.Vec3d

class CharizardModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame, BipedFrame, BimanualFrame, BiWingedFrame {
    override val rootPart: ModelPart = registerRelevantPart("charizard", root.getChild("charizard"))
    val body = registerRelevantPart(rootPart.childNamed("body"))
    val neck = registerRelevantPart(body.childNamed("neck"))
    val neck2 = registerRelevantPart(neck.childNamed("neck2"))
    override val head = registerRelevantPart(neck2.childNamed("head_AI"))
    val deeperHead = registerRelevantPart(head.childNamed("head"))
    override val rightArm = registerRelevantPart(body.childNamed("arm_right"))
    val rightForearm = registerRelevantPart(rightArm.childNamed("forearm_right"))
    val rightFinger1 = registerRelevantPart(rightForearm.childNamed("hand_right", "finger_right1"))
    val rightFinger2 = registerRelevantPart(rightForearm.childNamed("hand_right", "finger_right2"))
    val rightFinger3 = registerRelevantPart(rightForearm.childNamed("hand_right", "finger_right3"))
    override val leftArm = registerRelevantPart(body.childNamed("arm_left"))
    val leftForearm = registerRelevantPart(leftArm.childNamed("forearm_left"))
    val leftFinger1 = registerRelevantPart(leftForearm.childNamed("hand_left", "finger_left1"))
    val leftFinger2 = registerRelevantPart(leftForearm.childNamed("hand_left", "finger_left2"))
    val leftFinger3 = registerRelevantPart(leftForearm.childNamed("hand_left", "finger_left3"))
    override val rightLeg = registerRelevantPart(body.childNamed("leg_right"))
    val rightFoot = registerRelevantPart(rightLeg.childNamed("foot_right"))

    override val leftLeg = registerRelevantPart("leftleg", body.getChild("leg_left"))
    val leftFoot = registerRelevantPart(leftLeg.childNamed("foot_left"))
    override val leftWing = registerRelevantPart(rootPart.childNamed("body", "wing_left"))
    override val rightWing = registerRelevantPart("rightwing", rootPart.getChildOf("body", "wing_right"))
    val leftWing2 = registerRelevantPart(leftWing.childNamed("wing_left2"))
    val rightWing2 = registerRelevantPart(rightWing.childNamed("wing_right2"))
    private val tail = registerRelevantPart(body.childNamed("tail"))
    private val tail2 = registerRelevantPart(tail.childNamed("tail2"))
    private val tail3 = registerRelevantPart(tail2.childNamed("tail3"))
    private val fire = registerRelevantPart(tail3.childNamed("fire"))

    override val portraitScale = 1.75F
    override val portraitTranslation = Vec3d(-0.4, 1.6, 0.0)

    override val profileScale = 0.7F
    override val profileTranslation = Vec3d(0.0, 0.73, 0.0)

    override fun registerPoses() {
        registerPose(
            poseName = "standing",
            poseTypes = setOf(PoseType.NONE, PoseType.PROFILE),
            transformTicks = 10,
            condition = { !it.isMoving.get() && !it.getBehaviourFlag(PokemonBehaviourFlag.EXCITED) },
            idleAnimations = arrayOf( SingleBoneLookAnimation(this),
                BedrockStatelessAnimation(
                    this,
                    BedrockAnimationRepository.getAnimation("charizard.animation.json","animation.charizard.ground_idle")
                )
            ),
            transformedParts = arrayOf()
        )

        registerPose(
            poseType = PoseType.WALK,
            transformTicks = 10,
            condition = { it.isMoving.get() && !it.getBehaviourFlag(PokemonBehaviourFlag.EXCITED) },
            idleAnimations = arrayOf(SingleBoneLookAnimation(this),
                BedrockStatelessAnimation(
                    this,
                    BedrockAnimationRepository.getAnimation("charizard.animation.json","animation.charizard.ground_idle")
                ),
                BedrockStatelessAnimation(
                    this,
                    BedrockAnimationRepository.getAnimation("charizard.animation.json","animation.charizard.ground_walk")
                )
            ),
            transformedParts = emptyArray()
        )

        registerPose(
            poseType = PoseType.FLY,
            transformTicks = 10,
            condition = { !it.isMoving.get() && it.getBehaviourFlag(PokemonBehaviourFlag.EXCITED) },
            idleAnimations = arrayOf( SingleBoneLookAnimation(this),
                BedrockStatelessAnimation(
                    this,
                    BedrockAnimationRepository.getAnimation("charizard.animation.json","animation.charizard.air_idle")
                )
            ),
            transformedParts = arrayOf(rootPart.asTransformed().addPosition(Y_AXIS, -2F))
        )

        registerPose(
            poseType = PoseType.SWIM,
            transformTicks = 10,
            condition = { it.isMoving.get() && it.getBehaviourFlag(PokemonBehaviourFlag.EXCITED) },
            idleAnimations = arrayOf(
                SingleBoneLookAnimation(this),
                BedrockStatelessAnimation(
                    this,
                    BedrockAnimationRepository.getAnimation("charizard.animation.json","animation.charizard.air_fly")
                )
            ),
            transformedParts = arrayOf(rootPart.asTransformed().addPosition(Y_AXIS, 6F))
        )
    }

    companion object {
        val LAYER_LOCATION = EntityModelLayer(cobbledResource("charizard"), "main")
        fun createBodyLayer() = TexturedModel.from("charizard")!!.create()!!
    }
}