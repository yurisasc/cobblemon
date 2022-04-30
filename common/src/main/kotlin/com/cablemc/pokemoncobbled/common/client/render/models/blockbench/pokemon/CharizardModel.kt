package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.addPosition
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
    val neck = registerRelevantPart(body.childNamed("neck1"))
    val neck2 = registerRelevantPart(neck.childNamed("neck2"))
    override val head = registerRelevantPart(neck2.childNamed("head"))
    override val rightArm = registerRelevantPart(body.childNamed("rightarm"))
    val rightForearm = registerRelevantPart(rightArm.childNamed("rightforearm"))
    val rightFinger1 = registerRelevantPart(rightForearm.childNamed("righthand", "rightfinger"))
    val rightFinger2 = registerRelevantPart(rightForearm.childNamed("righthand", "rightfinger2"))
    val rightFinger3 = registerRelevantPart(rightForearm.childNamed("righthand", "rightfinger3"))
    override val leftArm = registerRelevantPart(body.childNamed("leftarm"))
    val leftForearm = registerRelevantPart(leftArm.childNamed("leftforearm"))
    val leftFinger1 = registerRelevantPart(leftForearm.childNamed("lefthand", "leftfinger4"))
    val leftFinger2 = registerRelevantPart(leftForearm.childNamed("lefthand", "leftfinger5"))
    val leftFinger3 = registerRelevantPart(leftForearm.childNamed("lefthand", "leftfinger6"))
    override val rightLeg = registerRelevantPart(body.childNamed("rightleg"))
    val rightFoot = registerRelevantPart(rightLeg.childNamed("rightfoot"))

    override val leftLeg = registerRelevantPart("leftleg", body.getChild("leftleg"))
    val leftFoot = registerRelevantPart(leftLeg.childNamed("leftfoot"))
    override val leftWing = registerRelevantPart(rootPart.childNamed("body", "leftwing"))
    override val rightWing = registerRelevantPart("rightwing", rootPart.getChildOf("body", "rightwing"))
    val leftWing2 = registerRelevantPart(leftWing.childNamed("leftwing2"))
    val rightWing2 = registerRelevantPart(rightWing.childNamed("rightwing2"))
    private val tail = registerRelevantPart(body.childNamed("tail"))
    private val tail2 = registerRelevantPart(tail.childNamed("tail2"))
    private val tail3 = registerRelevantPart(tail2.childNamed("tail3"))
    private val fire = registerRelevantPart(tail3.childNamed("fire"))

    override val portraitScale = 1.75F
    override val portraitTranslation = Vec3d(-0.1, 1.6, 0.0)

    override val profileScale = 1.0F
    override val profileTranslation = Vec3d(0.0, 0.0, 0.0)

    override fun registerPoses() {
        registerPose(
            poseType = PoseType.NONE,
            transformTicks = 0,
            condition = { !it.isMoving.get() && it.getBehaviourFlag(PokemonBehaviourFlag.EXCITED) },
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
            transformTicks = 0,
            condition = { it.isMoving.get() && it.getBehaviourFlag(PokemonBehaviourFlag.EXCITED) },
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
            transformTicks = 20,
            condition = { !it.isMoving.get() && !it.getBehaviourFlag(PokemonBehaviourFlag.EXCITED) },
            idleAnimations = arrayOf( SingleBoneLookAnimation(this),
                BedrockStatelessAnimation(
                    this,
                    BedrockAnimationRepository.getAnimation("charizard.animation.json","animation.charizard.air_idle")
                )
            ),
            transformedParts = arrayOf(rootPart.asTransformed().addPosition(Y_AXIS, -12F))
        )

        registerPose(
            poseType = PoseType.SWIM,
            transformTicks = 20,
            condition = { it.isMoving.get() && !it.getBehaviourFlag(PokemonBehaviourFlag.EXCITED) },
            idleAnimations = arrayOf( SingleBoneLookAnimation(this),
                BedrockStatelessAnimation(
                    this,
                    BedrockAnimationRepository.getAnimation("charizard.animation.json","animation.charizard.air_fly")
                )
            ),
            transformedParts = arrayOf(rootPart.asTransformed().addPosition(Y_AXIS, -18F))
        )


//        registerPose(
//            poseType = PoseType.WALK,
//            condition = { true },
//            idleAnimations = arrayOf(
//                BipedWalkAnimation(this),
//                BimanualSwingAnimation(this),
//                SingleBoneLookAnimation(this),
//                CascadeAnimation(
//                    frame = this,
//                    rootFunction = cosineFunction(
//                        period = 0.05f
//                    ),
//                    amplitudeFunction = gradualFunction(
//                        base = 0.1f,
//                        step = 0.1f
//                    ),
//                    segments = arrayOf(
//                        tail,
//                        tail2,
//                        tail3
//                    )
//                ),
//                leftWing.rotation(
//                    function = sineFunction(
//                        amplitude = 0.2F,
//                        period = 5F
//                    ),
//                    axis = TransformedModelPart.Y_AXIS,
//                    timeVariable = { _, _, ageInTicks -> ageInTicks / 20 },
//                ),
//                rightWing.rotation(
//                    function = sineFunction(
//                        amplitude = -0.2F,
//                        period = 5F
//                    ),
//                    axis = TransformedModelPart.Y_AXIS,
//                    timeVariable = { _, _, ageInTicks -> ageInTicks / 20 },
//                )
//            ),
//            transformedParts = arrayOf(
//                leftArm.withRotation(2, 70f.toRadians()),
//                rightArm.withRotation(2, (-70f).toRadians()),
//                leftWing.withRotation(1, (-15F).toRadians()),
//                rightWing.withRotation(1, (15F).toRadians()),
//                leftWing2.withRotation(1, 30f.toRadians()),
//                rightWing2.withRotation(1, (-30f).toRadians()),
//                tail2.withRotation(0, (35f).toRadians()),
//                fire.withRotation(0, (-35f).toRadians()),
//            )
//        )
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

            val neck1 = body.addChild(
                "neck1",
                ModelPartBuilder.create().uv(25, 86)
                    .cuboid(-2.0f, -4.0f, -2.0f, 4.0f, 5.0f, 4.0f, Dilation(0.1f)),
                ModelTransform.pivot(0.0f, -7.5f, 0.0f)
            )

            val neck2 = neck1.addChild(
                "neck2",
                ModelPartBuilder.create().uv(41, 86)
                    .cuboid(-2.0f, -5.5f, -2.0f, 4.0f, 5.0f, 4.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, -3.5f, 0.0f)
            )

            val head = neck2.addChild(
                "head",
                ModelPartBuilder.create().uv(18, 77)
                    .cuboid(-2.5f, -5.0f, -9.45f, 5.0f, 3.0f, 6.0f, Dilation(0.0f))
                    .uv(66, 34).cuboid(-3.0f, -6.0f, -3.45f, 6.0f, 6.0f, 7.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, -4.5f, -0.05f)
            )

            val head_r1 = head.addChild(
                "head_r1",
                ModelPartBuilder.create().uv(66, 33).mirrored()
                    .cuboid(-0.05f, -1.0f, -1.5f, 0.0f, 2.0f, 3.0f, Dilation(0.02f)).mirrored(false)
                    .uv(66, 33).cuboid(-6.05f, -1.0f, -1.5f, 0.0f, 2.0f, 3.0f, Dilation(0.02f)),
                ModelTransform.of(3.05f, -4.25f, -1.45f, 0.1745f, 0.0f, 0.0f)
            )

            val head_r2 = head.addChild(
                "head_r2",
                ModelPartBuilder.create().uv(31, 67)
                    .cuboid(-0.5f, -1.0f, -3.0f, 1.0f, 2.0f, 6.0f, Dilation(0.0f)),
                ModelTransform.of(-2.25f, -5.0f, 6.3f, 0.0873f, -0.0873f, 0.0f)
            )

            val head_r3 = head.addChild(
                "head_r3",
                ModelPartBuilder.create().uv(52, 27)
                    .cuboid(-0.5f, -1.0f, -3.0f, 1.0f, 2.0f, 6.0f, Dilation(0.0f)),
                ModelTransform.of(2.25f, -5.0f, 6.3f, 0.0873f, 0.0873f, 0.0f)
            )

            val jaw = head.addChild(
                "jaw",
                ModelPartBuilder.create().uv(77, 47)
                    .cuboid(-2.5f, -1.0f, -5.5f, 5.0f, 2.0f, 6.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, -1.0f, -3.7f)
            )

            val righteyelid =
                head.addChild("righteyelid", ModelPartBuilder.create(), ModelTransform.pivot(0.0f, 0.0f, 0.0f))

            val head_r4 = righteyelid.addChild(
                "head_r4",
                ModelPartBuilder.create().uv(93, 48)
                    .cuboid(0.15f, -1.0f, -1.5f, 1.0f, 2.0f, 3.0f, Dilation(0.04f)),
                ModelTransform.of(-3.05f, -4.25f, -1.45f, 0.1745f, 0.0f, 0.0f)
            )

            val lefteyelid =
                head.addChild("lefteyelid", ModelPartBuilder.create(), ModelTransform.pivot(0.0f, 0.0f, 0.0f))

            val head_r5 = lefteyelid.addChild(
                "head_r5",
                ModelPartBuilder.create().uv(93, 48).mirrored()
                    .cuboid(-1.15f, -1.0f, -1.5f, 1.0f, 2.0f, 3.0f, Dilation(0.04f)).mirrored(false),
                ModelTransform.of(3.05f, -4.25f, -1.45f, 0.1745f, 0.0f, 0.0f)
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
                    .cuboid(-37.0f, -21.0f, -3.5f, 0.0f, 12.0f, 7.0f, Dilation(0.02f)),
                ModelTransform.of(0.0f, 9.0f, -37.0f, 0.0f, 1.5708f, 0.0f)
            )

            val leftarm = body.addChild(
                "leftarm",
                ModelPartBuilder.create().uv(17, 67).mirrored()
                    .cuboid(0.0f, -1.0f, -1.5f, 6.0f, 2.0f, 3.0f, Dilation(0.0f)).mirrored(false),
                ModelTransform.pivot(5.5f, -6.5f, 0.0f)
            )

            val leftforearm = leftarm.addChild(
                "leftforearm",
                ModelPartBuilder.create().uv(83, 18).mirrored()
                    .cuboid(-1.0f, -1.0f, -1.5f, 5.0f, 2.0f, 3.0f, Dilation(0.0f)).mirrored(false),
                ModelTransform.pivot(7.0f, 0.0f, 0.0f)
            )

            val lefthand =
                leftforearm.addChild("lefthand", ModelPartBuilder.create(), ModelTransform.pivot(4.5f, -0.5f, 0.0f))

            val leftfinger4 = lefthand.addChild(
                "leftfinger4",
                ModelPartBuilder.create().uv(0, 56)
                    .cuboid(-0.5f, -0.5f, -0.5f, 3.0f, 1.0f, 1.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 0.0f, -1.0f)
            )

            val leftfinger4_r1 = leftfinger4.addChild(
                "leftfinger4_r1",
                ModelPartBuilder.create().uv(35, 48)
                    .cuboid(21.5f, 11.55f, -17.5f, 1.0f, 0.0f, 1.0f, Dilation(0.02f)),
                ModelTransform.of(-19.0f, 20.5f, 1.5f, -2.0944f, 0.0f, 0.0f)
            )

            val leftfinger5 = lefthand.addChild(
                "leftfinger5",
                ModelPartBuilder.create().uv(44, 55)
                    .cuboid(-0.5f, -0.5f, -0.5f, 3.0f, 1.0f, 1.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 0.0f, 1.0f)
            )

            val leftfinger5_r1 = leftfinger5.addChild(
                "leftfinger5_r1",
                ModelPartBuilder.create().uv(7, 48)
                    .cuboid(21.5f, 9.8f, -18.51f, 1.0f, 0.0f, 1.0f, Dilation(0.02f)),
                ModelTransform.of(-19.0f, 20.5f, -0.5f, -2.0944f, 0.0f, 0.0f)
            )

            val leftfinger6 = lefthand.addChild(
                "leftfinger6",
                ModelPartBuilder.create().uv(36, 55)
                    .cuboid(-0.5f, -0.5f, -0.5f, 3.0f, 1.0f, 1.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 0.0f, 0.0f)
            )

            val leftfinger6_r1 = leftfinger6.addChild(
                "leftfinger6_r1",
                ModelPartBuilder.create().uv(5, 48)
                    .cuboid(21.5f, 10.675f, -18.01f, 1.0f, 0.0f, 1.0f, Dilation(0.02f)),
                ModelTransform.of(-19.0f, 20.5f, 0.5f, -2.0944f, 0.0f, 0.0f)
            )

            val rightarm = body.addChild(
                "rightarm",
                ModelPartBuilder.create().uv(17, 67)
                    .cuboid(-6.0f, -1.0f, -1.5f, 6.0f, 2.0f, 3.0f, Dilation(0.0f)),
                ModelTransform.pivot(-5.5f, -6.5f, 0.0f)
            )

            val rightforearm = rightarm.addChild(
                "rightforearm",
                ModelPartBuilder.create().uv(83, 18)
                    .cuboid(-4.0f, -1.0f, -1.5f, 5.0f, 2.0f, 3.0f, Dilation(0.0f)),
                ModelTransform.pivot(-7.0f, 0.0f, 0.0f)
            )

            val righthand = rightforearm.addChild(
                "righthand",
                ModelPartBuilder.create(),
                ModelTransform.pivot(-4.5f, -0.5f, 0.0f)
            )

            val rightfinger = righthand.addChild(
                "rightfinger",
                ModelPartBuilder.create().uv(66, 4)
                    .cuboid(-2.5f, -0.5f, -0.5f, 3.0f, 1.0f, 1.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 0.0f, -1.0f)
            )

            val rightfinger_r1 = rightfinger.addChild(
                "rightfinger_r1",
                ModelPartBuilder.create().uv(7, 49)
                    .cuboid(-22.5f, 11.55f, -17.5f, 1.0f, 0.0f, 1.0f, Dilation(0.02f)),
                ModelTransform.of(19.0f, 20.5f, 1.5f, -2.0944f, 0.0f, 0.0f)
            )

            val rightfinger2 = righthand.addChild(
                "rightfinger2",
                ModelPartBuilder.create().uv(66, 2)
                    .cuboid(-2.5f, -0.5f, -0.5f, 3.0f, 1.0f, 1.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 0.0f, 1.0f)
            )

            val rightfinger2_r1 = rightfinger2.addChild(
                "rightfinger2_r1",
                ModelPartBuilder.create().uv(5, 49)
                    .cuboid(-22.5f, 9.8f, -18.51f, 1.0f, 0.0f, 1.0f, Dilation(0.02f)),
                ModelTransform.of(19.0f, 20.5f, -0.5f, -2.0944f, 0.0f, 0.0f)
            )

            val rightfinger3 = righthand.addChild(
                "rightfinger3",
                ModelPartBuilder.create().uv(66, 0)
                    .cuboid(-2.5f, -0.5f, -0.5f, 3.0f, 1.0f, 1.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 0.0f, 0.0f)
            )

            val rightfinger3_r1 = rightfinger3.addChild(
                "rightfinger3_r1",
                ModelPartBuilder.create().uv(37, 48)
                    .cuboid(-22.5f, 10.675f, -18.01f, 1.0f, 0.0f, 1.0f, Dilation(0.02f)),
                ModelTransform.of(19.0f, 20.5f, 0.5f, -2.0944f, 0.0f, 0.0f)
            )

            val leftleg = body.addChild(
                "leftleg",
                ModelPartBuilder.create().uv(66, 18)
                    .cuboid(-2.5f, -1.9f, -3.6f, 5.0f, 9.0f, 7.0f, Dilation(0.0f)),
                ModelTransform.of(5.25f, 4.4f, 0.1f, 0.0f, -0.0873f, 0.0f)
            )

            val leftfoot = leftleg.addChild(
                "leftfoot",
                ModelPartBuilder.create().uv(0, 49)
                    .cuboid(-1.75f, 0.0f, -5.5f, 0.0f, 2.0f, 3.0f, Dilation(0.02f))
                    .uv(0, 47).cuboid(0.0f, 0.0f, -5.5f, 0.0f, 2.0f, 3.0f, Dilation(0.02f))
                    .uv(0, 45).cuboid(1.75f, 0.0f, -5.5f, 0.0f, 2.0f, 3.0f, Dilation(0.02f))
                    .uv(36, 48).cuboid(-2.0f, 0.0f, -2.5f, 4.0f, 2.0f, 5.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 7.1f, -0.1f)
            )

            val rightleg = body.addChild(
                "rightleg",
                ModelPartBuilder.create().uv(0, 67)
                    .cuboid(-2.5f, -1.9f, -3.6f, 5.0f, 9.0f, 7.0f, Dilation(0.0f)),
                ModelTransform.of(-5.25f, 4.4f, 0.1f, 0.0f, 0.0873f, 0.0f)
            )

            val rightfoot = rightleg.addChild(
                "rightfoot",
                ModelPartBuilder.create().uv(49, 47)
                    .cuboid(1.75f, 0.0f, -5.5f, 0.0f, 2.0f, 3.0f, Dilation(0.02f))
                    .uv(49, 45).cuboid(0.0f, 0.0f, -5.5f, 0.0f, 2.0f, 3.0f, Dilation(0.02f))
                    .uv(0, 51).cuboid(-1.75f, 0.0f, -5.5f, 0.0f, 2.0f, 3.0f, Dilation(0.02f))
                    .uv(0, 83).cuboid(-2.0f, 0.0f, -2.5f, 4.0f, 2.0f, 5.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 7.1f, -0.1f)
            )

            val rightwing = body.addChild(
                "rightwing",
                ModelPartBuilder.create().uv(57, 101)
                    .cuboid(-15.0f, -18.0f, 0.0f, 15.0f, 24.0f, 0.0f, Dilation(0.02f)),
                ModelTransform.pivot(-0.5f, -4.5f, 4.5f)
            )

            val rightwing2 = rightwing.addChild(
                "rightwing2",
                ModelPartBuilder.create().uv(20, 98)
                    .cuboid(-18.0f, -12.0f, 0.0f, 18.0f, 24.0f, 0.0f, Dilation(0.02f)),
                ModelTransform.pivot(-15.0f, -6.0f, 0.0f)
            )

            val leftwing = body.addChild(
                "leftwing",
                ModelPartBuilder.create().uv(57, 101).mirrored()
                    .cuboid(0.0f, -18.0f, 0.0f, 15.0f, 24.0f, 0.0f, Dilation(0.02f)).mirrored(false),
                ModelTransform.pivot(0.5f, -4.5f, 4.5f)
            )

            val leftwing2 = leftwing.addChild(
                "leftwing2",
                ModelPartBuilder.create().uv(20, 98).mirrored()
                    .cuboid(0.0f, -12.0f, 0.0f, 18.0f, 24.0f, 0.0f, Dilation(0.02f)).mirrored(false),
                ModelTransform.pivot(15.0f, -6.0f, 0.0f)
            )

            return TexturedModelData.of(meshdefinition, 128, 128)
        }
    }
}