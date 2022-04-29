package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.*
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.bedrock.animation.BedrockAnimationRepository
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.bedrock.animation.BedrockStatelessAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.childNamed
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.BiWingedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.BimanualFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.BipedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.HeadedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.getChildOf
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.PoseType
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.TransformedModelPart
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.wavefunction.sineFunction
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.withRotation
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonBehaviourFlag
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.cablemc.pokemoncobbled.common.util.math.geometry.toRadians
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.world.phys.Vec3d

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
    override val portraitTranslation = Vec3d-0.1, 1.6, 0.0)

    override val profileScale = 1.0F
    override val profileTranslation = Vec3d0.0, 0.0, 0.0)

    override fun registerPoses() {
        registerPose(
            poseType = PoseType.NONE,
            condition = { !it.isMoving.get() && !it.getBehaviourFlag(PokemonBehaviourFlag.EXCITED) },
            idleAnimations = arrayOf(
                BedrockStatelessAnimation(
                    this,
                    BedrockAnimationRepository.getAnimation("charizard.animation.json","animation.charizard.ground_idle")
                )
            ),
            transformedParts = emptyArray()
        )

        registerPose(
            poseType = PoseType.WALK,
            condition = { it.isMoving.get() && !it.getBehaviourFlag(PokemonBehaviourFlag.EXCITED) },
            idleAnimations = arrayOf(
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
            condition = { !it.isMoving.get() && it.getBehaviourFlag(PokemonBehaviourFlag.EXCITED) },
            idleAnimations = arrayOf(
                BedrockStatelessAnimation(
                    this,
                    BedrockAnimationRepository.getAnimation("charizard.animation.json","animation.charizard.air_idle")
                ),
            ),
            transformedParts = emptyArray()
        )

        registerPose(
            poseType = PoseType.SWIM,
            condition = { it.isMoving.get() && it.getBehaviourFlag(PokemonBehaviourFlag.EXCITED) },
            idleAnimations = arrayOf(
                BedrockStatelessAnimation(
                    this,
                    BedrockAnimationRepository.getAnimation("charizard.animation.json","animation.charizard.air_idle")
                ),
                BedrockStatelessAnimation(
                    this,
                    BedrockAnimationRepository.getAnimation("charizard.animation.json","animation.charizard.air_fly")
                )
            ),
            transformedParts = emptyArray()
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
        val LAYER_LOCATION = ModelLayerLocation(cobbledResource("charizard"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root

            val charizard = partdefinition.addOrReplaceChild(
                "charizard",
                CubeListBuilder.create(),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )

            val body = charizard.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(46, 48)
                    .addBox(-5.5f, -7.5f, -4.5f, 11.0f, 7.0f, 9.0f, CubeDeformation(0.0f))
                    .texOffs(0, 48).addBox(-6.5f, -0.5f, -5.0f, 13.0f, 9.0f, 10.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -13.5f, -0.5f)
            )

            val neck1 = body.addOrReplaceChild(
                "neck1",
                CubeListBuilder.create().texOffs(25, 86)
                    .addBox(-2.0f, -4.0f, -2.0f, 4.0f, 5.0f, 4.0f, CubeDeformation(0.1f)),
                PartPose.offset(0.0f, -7.5f, 0.0f)
            )

            val neck2 = neck1.addOrReplaceChild(
                "neck2",
                CubeListBuilder.create().texOffs(41, 86)
                    .addBox(-2.0f, -5.5f, -2.0f, 4.0f, 5.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -3.5f, 0.0f)
            )

            val head = neck2.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(18, 77)
                    .addBox(-2.5f, -5.0f, -9.45f, 5.0f, 3.0f, 6.0f, CubeDeformation(0.0f))
                    .texOffs(66, 34).addBox(-3.0f, -6.0f, -3.45f, 6.0f, 6.0f, 7.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -4.5f, -0.05f)
            )

            val head_r1 = head.addOrReplaceChild(
                "head_r1",
                CubeListBuilder.create().texOffs(66, 33).mirror()
                    .addBox(-0.05f, -1.0f, -1.5f, 0.0f, 2.0f, 3.0f, CubeDeformation(0.02f)).mirror(false)
                    .texOffs(66, 33).addBox(-6.05f, -1.0f, -1.5f, 0.0f, 2.0f, 3.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(3.05f, -4.25f, -1.45f, 0.1745f, 0.0f, 0.0f)
            )

            val head_r2 = head.addOrReplaceChild(
                "head_r2",
                CubeListBuilder.create().texOffs(31, 67)
                    .addBox(-0.5f, -1.0f, -3.0f, 1.0f, 2.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-2.25f, -5.0f, 6.3f, 0.0873f, -0.0873f, 0.0f)
            )

            val head_r3 = head.addOrReplaceChild(
                "head_r3",
                CubeListBuilder.create().texOffs(52, 27)
                    .addBox(-0.5f, -1.0f, -3.0f, 1.0f, 2.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(2.25f, -5.0f, 6.3f, 0.0873f, 0.0873f, 0.0f)
            )

            val jaw = head.addOrReplaceChild(
                "jaw",
                CubeListBuilder.create().texOffs(77, 47)
                    .addBox(-2.5f, -1.0f, -5.5f, 5.0f, 2.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -1.0f, -3.7f)
            )

            val righteyelid =
                head.addOrReplaceChild("righteyelid", CubeListBuilder.create(), PartPose.offset(0.0f, 0.0f, 0.0f))

            val head_r4 = righteyelid.addOrReplaceChild(
                "head_r4",
                CubeListBuilder.create().texOffs(93, 48)
                    .addBox(0.15f, -1.0f, -1.5f, 1.0f, 2.0f, 3.0f, CubeDeformation(0.04f)),
                PartPose.offsetAndRotation(-3.05f, -4.25f, -1.45f, 0.1745f, 0.0f, 0.0f)
            )

            val lefteyelid =
                head.addOrReplaceChild("lefteyelid", CubeListBuilder.create(), PartPose.offset(0.0f, 0.0f, 0.0f))

            val head_r5 = lefteyelid.addOrReplaceChild(
                "head_r5",
                CubeListBuilder.create().texOffs(93, 48).mirror()
                    .addBox(-1.15f, -1.0f, -1.5f, 1.0f, 2.0f, 3.0f, CubeDeformation(0.04f)).mirror(false),
                PartPose.offsetAndRotation(3.05f, -4.25f, -1.45f, 0.1745f, 0.0f, 0.0f)
            )

            val tail = body.addOrReplaceChild(
                "tail",
                CubeListBuilder.create().texOffs(35, 64)
                    .addBox(-3.0f, -3.175f, -2.0f, 6.0f, 6.0f, 13.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 5.625f, 5.0f)
            )

            val tail2 = tail.addOrReplaceChild(
                "tail2",
                CubeListBuilder.create().texOffs(66, 0)
                    .addBox(-2.0f, -2.6f, -1.0f, 4.0f, 5.0f, 13.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.375f, 11.0f)
            )

            val tail3 = tail2.addOrReplaceChild(
                "tail3",
                CubeListBuilder.create().texOffs(26, 33)
                    .addBox(-1.0f, -2.15f, -1.0f, 2.0f, 4.0f, 11.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.5f, 12.0f)
            )

            val fire = tail3.addOrReplaceChild(
                "fire",
                CubeListBuilder.create().texOffs(52, 29)
                    .addBox(0.0f, -12.0f, -3.5f, 0.0f, 12.0f, 7.0f, CubeDeformation(0.02f)),
                PartPose.offset(0.0f, -2.0f, 9.5f)
            )

            val fire2_r1 = fire.addOrReplaceChild(
                "fire2_r1",
                CubeListBuilder.create().texOffs(52, 29)
                    .addBox(-37.0f, -21.0f, -3.5f, 0.0f, 12.0f, 7.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(0.0f, 9.0f, -37.0f, 0.0f, 1.5708f, 0.0f)
            )

            val leftarm = body.addOrReplaceChild(
                "leftarm",
                CubeListBuilder.create().texOffs(17, 67).mirror()
                    .addBox(0.0f, -1.0f, -1.5f, 6.0f, 2.0f, 3.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offset(5.5f, -6.5f, 0.0f)
            )

            val leftforearm = leftarm.addOrReplaceChild(
                "leftforearm",
                CubeListBuilder.create().texOffs(83, 18).mirror()
                    .addBox(-1.0f, -1.0f, -1.5f, 5.0f, 2.0f, 3.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offset(7.0f, 0.0f, 0.0f)
            )

            val lefthand =
                leftforearm.addOrReplaceChild("lefthand", CubeListBuilder.create(), PartPose.offset(4.5f, -0.5f, 0.0f))

            val leftfinger4 = lefthand.addOrReplaceChild(
                "leftfinger4",
                CubeListBuilder.create().texOffs(0, 56)
                    .addBox(-0.5f, -0.5f, -0.5f, 3.0f, 1.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, -1.0f)
            )

            val leftfinger4_r1 = leftfinger4.addOrReplaceChild(
                "leftfinger4_r1",
                CubeListBuilder.create().texOffs(35, 48)
                    .addBox(21.5f, 11.55f, -17.5f, 1.0f, 0.0f, 1.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(-19.0f, 20.5f, 1.5f, -2.0944f, 0.0f, 0.0f)
            )

            val leftfinger5 = lefthand.addOrReplaceChild(
                "leftfinger5",
                CubeListBuilder.create().texOffs(44, 55)
                    .addBox(-0.5f, -0.5f, -0.5f, 3.0f, 1.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 1.0f)
            )

            val leftfinger5_r1 = leftfinger5.addOrReplaceChild(
                "leftfinger5_r1",
                CubeListBuilder.create().texOffs(7, 48)
                    .addBox(21.5f, 9.8f, -18.51f, 1.0f, 0.0f, 1.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(-19.0f, 20.5f, -0.5f, -2.0944f, 0.0f, 0.0f)
            )

            val leftfinger6 = lefthand.addOrReplaceChild(
                "leftfinger6",
                CubeListBuilder.create().texOffs(36, 55)
                    .addBox(-0.5f, -0.5f, -0.5f, 3.0f, 1.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val leftfinger6_r1 = leftfinger6.addOrReplaceChild(
                "leftfinger6_r1",
                CubeListBuilder.create().texOffs(5, 48)
                    .addBox(21.5f, 10.675f, -18.01f, 1.0f, 0.0f, 1.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(-19.0f, 20.5f, 0.5f, -2.0944f, 0.0f, 0.0f)
            )

            val rightarm = body.addOrReplaceChild(
                "rightarm",
                CubeListBuilder.create().texOffs(17, 67)
                    .addBox(-6.0f, -1.0f, -1.5f, 6.0f, 2.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offset(-5.5f, -6.5f, 0.0f)
            )

            val rightforearm = rightarm.addOrReplaceChild(
                "rightforearm",
                CubeListBuilder.create().texOffs(83, 18)
                    .addBox(-4.0f, -1.0f, -1.5f, 5.0f, 2.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offset(-7.0f, 0.0f, 0.0f)
            )

            val righthand = rightforearm.addOrReplaceChild(
                "righthand",
                CubeListBuilder.create(),
                PartPose.offset(-4.5f, -0.5f, 0.0f)
            )

            val rightfinger = righthand.addOrReplaceChild(
                "rightfinger",
                CubeListBuilder.create().texOffs(66, 4)
                    .addBox(-2.5f, -0.5f, -0.5f, 3.0f, 1.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, -1.0f)
            )

            val rightfinger_r1 = rightfinger.addOrReplaceChild(
                "rightfinger_r1",
                CubeListBuilder.create().texOffs(7, 49)
                    .addBox(-22.5f, 11.55f, -17.5f, 1.0f, 0.0f, 1.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(19.0f, 20.5f, 1.5f, -2.0944f, 0.0f, 0.0f)
            )

            val rightfinger2 = righthand.addOrReplaceChild(
                "rightfinger2",
                CubeListBuilder.create().texOffs(66, 2)
                    .addBox(-2.5f, -0.5f, -0.5f, 3.0f, 1.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 1.0f)
            )

            val rightfinger2_r1 = rightfinger2.addOrReplaceChild(
                "rightfinger2_r1",
                CubeListBuilder.create().texOffs(5, 49)
                    .addBox(-22.5f, 9.8f, -18.51f, 1.0f, 0.0f, 1.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(19.0f, 20.5f, -0.5f, -2.0944f, 0.0f, 0.0f)
            )

            val rightfinger3 = righthand.addOrReplaceChild(
                "rightfinger3",
                CubeListBuilder.create().texOffs(66, 0)
                    .addBox(-2.5f, -0.5f, -0.5f, 3.0f, 1.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )

            val rightfinger3_r1 = rightfinger3.addOrReplaceChild(
                "rightfinger3_r1",
                CubeListBuilder.create().texOffs(37, 48)
                    .addBox(-22.5f, 10.675f, -18.01f, 1.0f, 0.0f, 1.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(19.0f, 20.5f, 0.5f, -2.0944f, 0.0f, 0.0f)
            )

            val leftleg = body.addOrReplaceChild(
                "leftleg",
                CubeListBuilder.create().texOffs(66, 18)
                    .addBox(-2.5f, -1.9f, -3.6f, 5.0f, 9.0f, 7.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(5.25f, 4.4f, 0.1f, 0.0f, -0.0873f, 0.0f)
            )

            val leftfoot = leftleg.addOrReplaceChild(
                "leftfoot",
                CubeListBuilder.create().texOffs(0, 49)
                    .addBox(-1.75f, 0.0f, -5.5f, 0.0f, 2.0f, 3.0f, CubeDeformation(0.02f))
                    .texOffs(0, 47).addBox(0.0f, 0.0f, -5.5f, 0.0f, 2.0f, 3.0f, CubeDeformation(0.02f))
                    .texOffs(0, 45).addBox(1.75f, 0.0f, -5.5f, 0.0f, 2.0f, 3.0f, CubeDeformation(0.02f))
                    .texOffs(36, 48).addBox(-2.0f, 0.0f, -2.5f, 4.0f, 2.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 7.1f, -0.1f)
            )

            val rightleg = body.addOrReplaceChild(
                "rightleg",
                CubeListBuilder.create().texOffs(0, 67)
                    .addBox(-2.5f, -1.9f, -3.6f, 5.0f, 9.0f, 7.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-5.25f, 4.4f, 0.1f, 0.0f, 0.0873f, 0.0f)
            )

            val rightfoot = rightleg.addOrReplaceChild(
                "rightfoot",
                CubeListBuilder.create().texOffs(49, 47)
                    .addBox(1.75f, 0.0f, -5.5f, 0.0f, 2.0f, 3.0f, CubeDeformation(0.02f))
                    .texOffs(49, 45).addBox(0.0f, 0.0f, -5.5f, 0.0f, 2.0f, 3.0f, CubeDeformation(0.02f))
                    .texOffs(0, 51).addBox(-1.75f, 0.0f, -5.5f, 0.0f, 2.0f, 3.0f, CubeDeformation(0.02f))
                    .texOffs(0, 83).addBox(-2.0f, 0.0f, -2.5f, 4.0f, 2.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 7.1f, -0.1f)
            )

            val rightwing = body.addOrReplaceChild(
                "rightwing",
                CubeListBuilder.create().texOffs(57, 101)
                    .addBox(-15.0f, -18.0f, 0.0f, 15.0f, 24.0f, 0.0f, CubeDeformation(0.02f)),
                PartPose.offset(-0.5f, -4.5f, 4.5f)
            )

            val rightwing2 = rightwing.addOrReplaceChild(
                "rightwing2",
                CubeListBuilder.create().texOffs(20, 98)
                    .addBox(-18.0f, -12.0f, 0.0f, 18.0f, 24.0f, 0.0f, CubeDeformation(0.02f)),
                PartPose.offset(-15.0f, -6.0f, 0.0f)
            )

            val leftwing = body.addOrReplaceChild(
                "leftwing",
                CubeListBuilder.create().texOffs(57, 101).mirror()
                    .addBox(0.0f, -18.0f, 0.0f, 15.0f, 24.0f, 0.0f, CubeDeformation(0.02f)).mirror(false),
                PartPose.offset(0.5f, -4.5f, 4.5f)
            )

            val leftwing2 = leftwing.addOrReplaceChild(
                "leftwing2",
                CubeListBuilder.create().texOffs(20, 98).mirror()
                    .addBox(0.0f, -12.0f, 0.0f, 18.0f, 24.0f, 0.0f, CubeDeformation(0.02f)).mirror(false),
                PartPose.offset(15.0f, -6.0f, 0.0f)
            )

            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}