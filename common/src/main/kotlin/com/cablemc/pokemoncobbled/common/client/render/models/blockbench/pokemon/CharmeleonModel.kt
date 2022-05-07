package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.*
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.BimanualFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.BipedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.HeadedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.PoseType
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.withRotation
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.cablemc.pokemoncobbled.common.util.math.geometry.toRadians
import net.minecraft.client.model.*
import net.minecraft.client.render.entity.model.EntityModelLayer
import net.minecraft.util.math.Vec3d

class CharmeleonModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame, BipedFrame, BimanualFrame {

    override val rootPart = registerRelevantPart("charmeleon", root.getChild("charmeleon"))
    val body = registerRelevantPart("body", rootPart.getChild("body"))
    override val head = registerRelevantPart("head", body.getChild("neck").getChild("head"))
    override val rightLeg = registerRelevantPart("rightleg", body.getChild("rightleg"))
    override val leftLeg = registerRelevantPart("leftleg", body.getChild("leftleg"))
    override val rightArm = registerRelevantPart("rightarm", body.getChild("rightarm"))
    override val leftArm = registerRelevantPart("leftarm", body.getChild("leftarm"))

    private val tail = registerRelevantPart("tail", body.getChild("tail"))
    private val tailTip = registerRelevantPart("tail2", tail.getChild("tail2"))
    private val tailFlame = registerRelevantPart("fire", tailTip.getChild("fire"))
    private val leftHand = registerRelevantPart("lefthand", leftArm.getChild("lefthand"))
    private val rightHand = registerRelevantPart("righthand", rightArm.getChild("righthand"))

    override val portraitScale = 1.65F
    override val portraitTranslation = Vec3d(0.0, 0.7, 0.0)

    override val profileScale = 1.0F
    override val profileTranslation = Vec3d(0.0, 0.0, 0.0)

    override fun registerPoses() {
        registerPose(
            poseType = PoseType.WALK,
            condition = { true },
            idleAnimations = arrayOf(
                BipedWalkAnimation(this),
                BimanualSwingAnimation(this),
                SingleBoneLookAnimation(this),
                CascadeAnimation(
                    frame = this,
                    rootFunction = sineFunction(
                        period = 0.09f
                    ),
                    amplitudeFunction = gradualFunction(
                        base = 0.1f,
                        step = 0.1f
                    ),
                    segments = arrayOf(
                        tail,
                        tailTip
                    )
                )
            ),
            transformedParts = arrayOf(
                leftArm.withRotation(2, 70f.toRadians()),
                leftHand.withRotation(2, 10f.toRadians()),
                rightArm.withRotation(2, (-70f).toRadians()),
                rightHand.withRotation(2, (-10f).toRadians()),
                tailTip.withRotation(0, 35f.toRadians()),
                tailFlame.withRotation(0, (-35f).toRadians())
            )
        )
    }

    companion object {
        val LAYER_LOCATION = EntityModelLayer(cobbledResource("charmeleon"), "main")
        fun createBodyLayer(): TexturedModelData {
            val meshdefinition = ModelData()
            val partdefinition = meshdefinition.root
            val charmeleon = partdefinition.addChild(
                "charmeleon",
                ModelPartBuilder.create(),
                ModelTransform.pivot(0.0f, 24.0f, 0.0f)
            )
            val body = charmeleon.addChild(
                "body",
                ModelPartBuilder.create().uv(22, 17)
                    .cuboid(-4.5f, -6.5f, -3.0f, 9.0f, 13.0f, 6.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, -10.5f, 0.0f)
            )
            val neck = body.addChild(
                "neck",
                ModelPartBuilder.create().uv(0, 44)
                    .cuboid(-2.0f, -4.0f, -2.0f, 4.0f, 4.0f, 4.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, -6.5f, 0.0f)
            )
            val head = neck.addChild(
                "head",
                ModelPartBuilder.create().uv(42, 36)
                    .cuboid(-2.5f, -4.0f, -8.0f, 5.0f, 4.0f, 4.0f, Dilation(0.0f))
                    .uv(0, 0).cuboid(-4.0f, -7.0f, -4.0f, 8.0f, 7.0f, 7.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, -3.0f, 0.0f)
            )
            val head_r1 = head.addChild(
                "head_r1",
                ModelPartBuilder.create().uv(39, 0)
                    .cuboid(-1.5f, -1.0f, -3.0f, 3.0f, 2.0f, 6.0f, Dilation(0.0f)),
                ModelTransform.of(0.0f, -7.25f, 4.25f, 0.4363f, 0.0f, 0.0f)
            )
            val eyes = head.addChild("eyes", ModelPartBuilder.create(), ModelTransform.pivot(0.0f, -4.0f, -2.25f))
            val eyes_r1 = eyes.addChild(
                "eyes_r1",
                ModelPartBuilder.create().uv(58, 0).mirrored()
                    .cuboid(0.0f, -1.0f, -1.5f, 0.0f, 2.0f, 3.0f, Dilation(0.0f)).mirrored(false)
                    .uv(58, 0).cuboid(-8.05f, -1.0f, -1.5f, 0.0f, 2.0f, 3.0f, Dilation(0.0f)),
                ModelTransform.of(4.025f, 0.0f, 0.0f, 0.2618f, 0.0f, 0.0f)
            )

            val tail = body.addChild("tail",
                ModelPartBuilder.create().uv(42, 52).cuboid(-2.0F, -2.5F, -1.0F, 4.0F, 5.0F, 7.0F, Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 3.75F, 3.0F)
            )

            val tail2 = tail.addChild("tail2",
                ModelPartBuilder.create().uv(8, 52).cuboid(-1.5F, -2.0F, 0.0F, 3.0F, 4.0F, 8.0F, Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.5F, 6.0F)
            )

            val fire = tail2.addChild("fire",
                ModelPartBuilder.create().uv(0, 16).cuboid(0.0F, -10.0F, -3.5F, 0.0F, 10.0F, 7.0F, Dilation(0.0F)),
                ModelTransform.pivot(0.0F, -2.0F, 7.0F)
            )

            val leftleg = body.addChild(
                "leftleg",
                ModelPartBuilder.create().uv(18, 21)
                    .cuboid(1.75f, 5.5f, -4.0f, 0.0f, 1.0f, 2.0f, Dilation(0.0f))
                    .uv(14, 21).cuboid(0.0f, 5.5f, -4.0f, 0.0f, 1.0f, 2.0f, Dilation(0.0f))
                    .uv(0, 4).cuboid(-1.75f, 5.5f, -4.0f, 0.0f, 1.0f, 2.0f, Dilation(0.0f))
                    .uv(10, 32).cuboid(-2.0f, -1.5f, -2.0f, 4.0f, 8.0f, 4.0f, Dilation(0.0f)),
                ModelTransform.pivot(3.25f, 4.0f, -0.25f)
            )
            val rightleg = body.addChild(
                "rightleg",
                ModelPartBuilder.create().uv(23, 0)
                    .cuboid(-1.75f, 5.5f, -4.0f, 0.0f, 1.0f, 2.0f, Dilation(0.0f))
                    .uv(14, 22).cuboid(0.0f, 5.5f, -4.0f, 0.0f, 1.0f, 2.0f, Dilation(0.0f))
                    .uv(18, 22).cuboid(1.75f, 5.5f, -4.0f, 0.0f, 1.0f, 2.0f, Dilation(0.0f))
                    .uv(26, 36).cuboid(-2.0f, -1.5f, -2.0f, 4.0f, 8.0f, 4.0f, Dilation(0.0f)),
                ModelTransform.pivot(-3.25f, 4.0f, -0.25f)
            )
            val leftarm = body.addChild(
                "leftarm",
                ModelPartBuilder.create().uv(46, 8)
                    .cuboid(0.0f, -1.0f, -1.5f, 5.0f, 2.0f, 3.0f, Dilation(0.0f)),
                ModelTransform.pivot(4.5f, -5.25f, 0.0f)
            )
            val lefthand = leftarm.addChild(
                "lefthand",
                ModelPartBuilder.create().uv(28, 10)
                    .cuboid(0.0f, -1.0f, -2.0f, 7.0f, 2.0f, 4.0f, Dilation(0.0f))
                    .uv(0, 5).cuboid(7.0f, 0.0f, 1.0f, 2.0f, 0.0f, 1.0f, Dilation(0.0f))
                    .uv(0, 4).cuboid(7.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, Dilation(0.0f))
                    .uv(0, 3).cuboid(7.0f, -0.25f, -0.5f, 2.0f, 0.0f, 1.0f, Dilation(0.0f)),
                ModelTransform.pivot(5.0f, 0.0f, 0.0f)
            )
            val rightarm = body.addChild(
                "rightarm",
                ModelPartBuilder.create().uv(42, 44)
                    .cuboid(-5.0f, -1.0f, -1.5f, 5.0f, 2.0f, 3.0f, Dilation(0.0f)),
                ModelTransform.pivot(-4.5f, -5.25f, 0.0f)
            )
            val righthand = rightarm.addChild(
                "righthand",
                ModelPartBuilder.create().uv(23, 0)
                    .cuboid(-7.0f, -1.0f, -2.0f, 7.0f, 2.0f, 4.0f, Dilation(0.0f))
                    .uv(0, 2).cuboid(-9.0f, 0.0f, 1.0f, 2.0f, 0.0f, 1.0f, Dilation(0.0f))
                    .uv(0, 1).cuboid(-9.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, Dilation(0.0f))
                    .uv(0, 0).cuboid(-9.0f, -0.25f, -0.5f, 2.0f, 0.0f, 1.0f, Dilation(0.0f)),
                ModelTransform.pivot(-5.0f, 0.0f, 0.0f)
            )
            return TexturedModelData.of(meshdefinition, 64, 64)
        }
    }
}