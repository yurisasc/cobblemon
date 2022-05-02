package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon

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
    val otherHead = registerRelevantPart(neck2.childNamed("head_AI"))
    override val head = registerRelevantPart(otherHead.childNamed("head"))
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
            transformedParts = emptyArray()
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
        fun createBodyLayer(): TexturedModelData {
            val meshdefinition = ModelData()
            val partdefinition = meshdefinition.root

            val charizard = partdefinition.addChild(
                "charizard",
                ModelPartBuilder.create(),
                ModelTransform.pivot(0.0f, 24.0f, 0.0f)
            )

            val body = charizard.addChild(
                "body",
                ModelPartBuilder.create().uv(46, 48)
                    .cuboid(-5.5f, -7.5f, -4.5f, 11.0f, 7.0f, 9.0f, Dilation(0.0f))
                    .uv(0, 48).cuboid(-6.5f, -0.5f, -5.0f, 13.0f, 9.0f, 10.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, -13.5f, -0.5f)
            )

            val neck = body.addChild(
                "neck",
                ModelPartBuilder.create().uv(25, 86)
                    .cuboid(-2.0f, -4.0f, -2.0f, 4.0f, 5.0f, 4.0f, Dilation(0.02f)),
                ModelTransform.pivot(0.0f, -7.5f, 0.0f)
            )

            val neck2 = neck.addChild(
                "neck2",
                ModelPartBuilder.create().uv(41, 86)
                    .cuboid(-2.0f, -5.5f, -2.0f, 4.0f, 5.0f, 4.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, -3.5f, 0.0f)
            )

            val head_AI = neck2.addChild(
                "head_AI",
                ModelPartBuilder.create(),
                ModelTransform.of(0.0f, -4.5f, 0.0f, -0.3927f, 0.0f, 0.0f)
            )

            val head = head_AI.addChild(
                "head",
                ModelPartBuilder.create().uv(66, 34)
                    .cuboid(-3.0f, -6.0f, -3.5f, 6.0f, 6.0f, 7.0f, Dilation(0.0f))
                    .uv(18, 77).cuboid(-2.5f, -5.0f, -9.5f, 5.0f, 3.0f, 6.0f, Dilation(0.0f)),
                ModelTransform.of(0.0f, 0.0761f, -0.3827f, 0.3927f, 0.0f, 0.0f)
            )

            val horn_left_r1 = head.addChild(
                "horn_left_r1",
                ModelPartBuilder.create().uv(52, 27)
                    .cuboid(-0.5124f, -0.9867f, 0.0207f, 1.0f, 2.0f, 6.0f, Dilation(0.0f)),
                ModelTransform.of(2.0f, -4.75f, 3.25f, 0.0873f, 0.0873f, 0.0f)
            )

            val horn_right_r1 = head.addChild(
                "horn_right_r1",
                ModelPartBuilder.create().uv(31, 67)
                    .cuboid(-0.4876f, -0.9867f, 0.0207f, 1.0f, 2.0f, 6.0f, Dilation(0.0f)),
                ModelTransform.of(-2.0f, -4.75f, 3.25f, 0.0873f, -0.0873f, 0.0f)
            )

            val eyes =
                head.addChild("eyes", ModelPartBuilder.create(), ModelTransform.pivot(0.0f, -4.0f, -1.5f))

            val eyelid_left_r1 = eyes.addChild(
                "eyelid_left_r1",
                ModelPartBuilder.create().uv(93, 48).mirrored()
                    .cuboid(-1.15f, -1.0f, -1.5f, 1.0f, 2.0f, 3.0f, Dilation(0.04f)).mirrored(false)
                    .uv(93, 48).cuboid(-5.95f, -1.0f, -1.5f, 1.0f, 2.0f, 3.0f, Dilation(0.04f))
                    .uv(66, 33).mirrored().cuboid(-0.05f, -1.0f, -1.5f, 0.0f, 2.0f, 3.0f, Dilation(0.02f))
                    .mirrored(false)
                    .uv(66, 33).cuboid(-6.05f, -1.0f, -1.5f, 0.0f, 2.0f, 3.0f, Dilation(0.02f)),
                ModelTransform.of(3.05f, -0.25f, 0.0f, 0.1745f, 0.0f, 0.0f)
            )

            val jaw = head.addChild(
                "jaw",
                ModelPartBuilder.create().uv(77, 47)
                    .cuboid(-2.5f, -1.0f, -5.75f, 5.0f, 2.0f, 6.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, -1.0f, -3.5f)
            )

            val tail = body.addChild(
                "tail",
                ModelPartBuilder.create().uv(35, 64)
                    .cuboid(-3.0f, -3.175f, -2.0f, 6.0f, 6.0f, 13.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 5.625f, 5.0f)
            )

            val tail2 = tail.addChild(
                "tail2",
                ModelPartBuilder.create().uv(66, 0)
                    .cuboid(-2.0f, -2.6f, -1.0f, 4.0f, 5.0f, 13.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 0.375f, 11.0f)
            )

            val tail3 = tail2.addChild(
                "tail3",
                ModelPartBuilder.create().uv(26, 33)
                    .cuboid(-1.0f, -2.15f, -1.0f, 2.0f, 4.0f, 11.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 0.5f, 12.0f)
            )

            val fire = tail3.addChild(
                "fire",
                ModelPartBuilder.create().uv(52, 29)
                    .cuboid(0.0f, -12.0f, -3.5f, 0.0f, 12.0f, 7.0f, Dilation(0.02f)),
                ModelTransform.pivot(0.0f, -2.0f, 9.5f)
            )

            val fire2_r1 = fire.addChild(
                "fire2_r1",
                ModelPartBuilder.create().uv(52, 29)
                    .cuboid(0.0f, -12.0f, -3.5f, 0.0f, 12.0f, 7.0f, Dilation(0.02f)),
                ModelTransform.of(0.0f, 0.0f, 0.0f, 0.0f, 1.5708f, 0.0f)
            )

            val arm_left = body.addChild(
                "arm_left",
                ModelPartBuilder.create().uv(17, 67).mirrored()
                    .cuboid(0.0f, -1.0f, -1.5f, 6.0f, 2.0f, 3.0f, Dilation(0.0f)).mirrored(false),
                ModelTransform.pivot(5.5f, -6.5f, 0.0f)
            )

            val forearm_left = arm_left.addChild(
                "forearm_left",
                ModelPartBuilder.create().uv(83, 18).mirrored()
                    .cuboid(0.0f, -1.0f, -1.5f, 5.0f, 2.0f, 3.0f, Dilation(0.0f)).mirrored(false),
                ModelTransform.pivot(6.0f, 0.0f, 0.0f)
            )

            val hand_left = forearm_left.addChild(
                "hand_left",
                ModelPartBuilder.create(),
                ModelTransform.pivot(5.0f, -0.5f, 0.0f)
            )

            val finger_left1 = hand_left.addChild(
                "finger_left1",
                ModelPartBuilder.create().uv(0, 56)
                    .cuboid(0.0f, -0.5f, -0.5f, 3.0f, 1.0f, 1.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 0.0f, -1.0f)
            )

            val claw_left1_r1 = finger_left1.addChild(
                "claw_left1_r1",
                ModelPartBuilder.create().uv(35, 48)
                    .cuboid(0.0f, 0.001f, -0.4965f, 1.0f, 0.0f, 1.0f, Dilation(0.02f)),
                ModelTransform.of(3.0f, 0.0f, 0.0f, -2.0944f, 0.0f, 0.0f)
            )

            val finger_left2 = hand_left.addChild(
                "finger_left2",
                ModelPartBuilder.create().uv(36, 55)
                    .cuboid(0.0f, -0.5f, -0.5f, 3.0f, 1.0f, 1.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 0.0f, 0.0f)
            )

            val claw_left2_r1 = finger_left2.addChild(
                "claw_left2_r1",
                ModelPartBuilder.create().uv(5, 48)
                    .cuboid(0.0f, -0.008f, -0.5065f, 1.0f, 0.0f, 1.0f, Dilation(0.02f)),
                ModelTransform.of(3.0f, 0.0f, 0.0f, -2.0944f, 0.0f, 0.0f)
            )

            val finger_left3 = hand_left.addChild(
                "finger_left3",
                ModelPartBuilder.create().uv(44, 55)
                    .cuboid(0.0f, -0.5f, -0.5f, 3.0f, 1.0f, 1.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 0.0f, 1.0f)
            )

            val claw_left3_r1 = finger_left3.addChild(
                "claw_left3_r1",
                ModelPartBuilder.create().uv(7, 48)
                    .cuboid(0.0f, -0.017f, -0.5065f, 1.0f, 0.0f, 1.0f, Dilation(0.02f)),
                ModelTransform.of(3.0f, 0.0f, 0.0f, -2.0944f, 0.0f, 0.0f)
            )

            val arm_right = body.addChild(
                "arm_right",
                ModelPartBuilder.create().uv(17, 67)
                    .cuboid(-6.0f, -1.0f, -1.5f, 6.0f, 2.0f, 3.0f, Dilation(0.0f)),
                ModelTransform.pivot(-5.5f, -6.5f, 0.0f)
            )

            val forearm_right = arm_right.addChild(
                "forearm_right",
                ModelPartBuilder.create().uv(83, 18)
                    .cuboid(-5.0f, -1.0f, -1.5f, 5.0f, 2.0f, 3.0f, Dilation(0.0f)),
                ModelTransform.pivot(-6.0f, 0.0f, 0.0f)
            )

            val hand_right = forearm_right.addChild(
                "hand_right",
                ModelPartBuilder.create(),
                ModelTransform.pivot(-5.0f, -0.5f, 0.0f)
            )

            val finger_right1 = hand_right.addChild(
                "finger_right1",
                ModelPartBuilder.create().uv(66, 4)
                    .cuboid(-3.0f, -0.5f, -0.5f, 3.0f, 1.0f, 1.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 0.0f, -1.0f)
            )

            val claw_right1_r1 = finger_right1.addChild(
                "claw_right1_r1",
                ModelPartBuilder.create().uv(7, 49)
                    .cuboid(-1.0f, 0.001f, -0.4965f, 1.0f, 0.0f, 1.0f, Dilation(0.02f)),
                ModelTransform.of(-3.0f, 0.0f, 0.0f, -2.0944f, 0.0f, 0.0f)
            )

            val finger_right2 = hand_right.addChild(
                "finger_right2",
                ModelPartBuilder.create().uv(66, 0)
                    .cuboid(-3.0f, -0.5f, -0.5f, 3.0f, 1.0f, 1.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 0.0f, 0.0f)
            )

            val claw_right2_r1 = finger_right2.addChild(
                "claw_right2_r1",
                ModelPartBuilder.create().uv(37, 48)
                    .cuboid(-1.0f, -0.008f, -0.5065f, 1.0f, 0.0f, 1.0f, Dilation(0.02f)),
                ModelTransform.of(-3.0f, 0.0f, 0.0f, -2.0944f, 0.0f, 0.0f)
            )

            val finger_right3 = hand_right.addChild(
                "finger_right3",
                ModelPartBuilder.create().uv(66, 2)
                    .cuboid(-3.0f, -0.5f, -0.5f, 3.0f, 1.0f, 1.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 0.0f, 1.0f)
            )

            val claw_right3_r1 = finger_right3.addChild(
                "claw_right3_r1",
                ModelPartBuilder.create().uv(5, 49)
                    .cuboid(-1.0f, -0.017f, -0.5065f, 1.0f, 0.0f, 1.0f, Dilation(0.02f)),
                ModelTransform.of(-3.0f, 0.0f, 0.0f, -2.0944f, 0.0f, 0.0f)
            )

            val leg_left = body.addChild(
                "leg_left",
                ModelPartBuilder.create().uv(66, 18)
                    .cuboid(-2.5f, -1.9f, -3.6f, 5.0f, 9.0f, 7.0f, Dilation(0.0f)),
                ModelTransform.of(5.25f, 4.4f, 0.1f, 0.0f, -0.0873f, 0.0f)
            )

            val foot_left = leg_left.addChild(
                "foot_left",
                ModelPartBuilder.create().uv(36, 48)
                    .cuboid(-2.0f, 0.0f, -2.5f, 4.0f, 2.0f, 5.0f, Dilation(0.0f))
                    .uv(0, 47).cuboid(0.0f, 0.0f, -5.5f, 0.0f, 2.0f, 3.0f, Dilation(0.02f)),
                ModelTransform.pivot(0.0f, 7.1f, -0.1f)
            )

            val toe_left3_r1 = foot_left.addChild(
                "toe_left3_r1",
                ModelPartBuilder.create().uv(0, 45)
                    .cuboid(0.0f, -1.0f, -3.0f, 0.0f, 2.0f, 3.0f, Dilation(0.02f)),
                ModelTransform.of(1.75f, 1.0f, -2.5f, 0.0f, -0.1745f, 0.0f)
            )

            val toe_left1_r1 = foot_left.addChild(
                "toe_left1_r1",
                ModelPartBuilder.create().uv(0, 49)
                    .cuboid(0.0f, -1.0f, -3.0f, 0.0f, 2.0f, 3.0f, Dilation(0.02f)),
                ModelTransform.of(-1.75f, 1.0f, -2.5f, 0.0f, 0.1745f, 0.0f)
            )

            val leg_right = body.addChild(
                "leg_right",
                ModelPartBuilder.create().uv(0, 67)
                    .cuboid(-2.5f, -1.9f, -3.6f, 5.0f, 9.0f, 7.0f, Dilation(0.0f)),
                ModelTransform.of(-5.25f, 4.4f, 0.1f, 0.0f, 0.0873f, 0.0f)
            )

            val foot_right = leg_right.addChild(
                "foot_right",
                ModelPartBuilder.create().uv(0, 83)
                    .cuboid(-2.0f, 0.0f, -2.5f, 4.0f, 2.0f, 5.0f, Dilation(0.0f))
                    .uv(49, 45).cuboid(0.0f, 0.0f, -5.5f, 0.0f, 2.0f, 3.0f, Dilation(0.02f)),
                ModelTransform.pivot(0.0f, 7.1f, -0.1f)
            )

            val toe_right3_r1 = foot_right.addChild(
                "toe_right3_r1",
                ModelPartBuilder.create().uv(49, 47)
                    .cuboid(0.0f, -1.0f, -3.0f, 0.0f, 2.0f, 3.0f, Dilation(0.02f)),
                ModelTransform.of(1.75f, 1.0f, -2.5f, 0.0f, -0.1745f, 0.0f)
            )

            val toe_right1_r1 = foot_right.addChild(
                "toe_right1_r1",
                ModelPartBuilder.create().uv(0, 51)
                    .cuboid(0.0f, -1.0f, -3.0f, 0.0f, 2.0f, 3.0f, Dilation(0.02f)),
                ModelTransform.of(-1.75f, 1.0f, -2.5f, 0.0f, 0.1745f, 0.0f)
            )

            val wing_right = body.addChild(
                "wing_right",
                ModelPartBuilder.create().uv(57, 101)
                    .cuboid(-15.0f, -18.0f, 0.0f, 15.0f, 24.0f, 0.0f, Dilation(0.02f)),
                ModelTransform.pivot(-0.5f, -4.5f, 4.5f)
            )

            val wing_right2 = wing_right.addChild(
                "wing_right2",
                ModelPartBuilder.create().uv(20, 98)
                    .cuboid(-18.0f, -12.0f, 0.0f, 18.0f, 24.0f, 0.0f, Dilation(0.02f)),
                ModelTransform.pivot(-15.0f, -6.0f, 0.0f)
            )

            val wing_left = body.addChild(
                "wing_left",
                ModelPartBuilder.create().uv(57, 101).mirrored()
                    .cuboid(0.0f, -18.0f, 0.0f, 15.0f, 24.0f, 0.0f, Dilation(0.02f)).mirrored(false),
                ModelTransform.pivot(0.5f, -4.5f, 4.5f)
            )

            val wing_left2 = wing_left.addChild(
                "wing_left2",
                ModelPartBuilder.create().uv(20, 98).mirrored()
                    .cuboid(0.0f, -12.0f, 0.0f, 18.0f, 24.0f, 0.0f, Dilation(0.02f)).mirrored(false),
                ModelTransform.pivot(15.0f, -6.0f, 0.0f)
            )

            return TexturedModelData.of(meshdefinition, 128, 128)
        }
    }
}