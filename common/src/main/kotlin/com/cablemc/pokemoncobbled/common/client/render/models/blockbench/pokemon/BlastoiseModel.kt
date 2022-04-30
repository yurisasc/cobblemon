package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.EarJoint
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.RangeOfMotion
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.BimanualSwingAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.BipedWalkAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.SingleBoneLookAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.BimanualFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.BipedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.EaredFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.HeadedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.PoseType
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.TransformedModelPart
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.wavefunction.sineFunction
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.withRotation
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.cablemc.pokemoncobbled.common.util.math.geometry.toRadians
import net.minecraft.client.model.*
import net.minecraft.client.render.entity.model.EntityModelLayer
import net.minecraft.util.math.Vec3d

class BlastoiseModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame, BipedFrame, BimanualFrame, EaredFrame {
    override val rootPart = registerRelevantPart("blastoise", root.getChild("blastoise"))
    val body = registerRelevantPart("body", rootPart.getChild("body"))
    override val head = registerRelevantPart("head", body.getChild("head"))
    override val rightLeg = registerRelevantPart("rightleg", body.getChild("rightleg"))
    override val leftLeg = registerRelevantPart("leftleg", body.getChild("leftleg"))
    override val rightArm = registerRelevantPart("rightarm", body.getChild("rightarm"))
    override val leftArm = registerRelevantPart("leftarm", body.getChild("leftarm"))
    private val rightEar = registerRelevantPart("rightear", head.getChild("rightear"))
    private val leftEar = registerRelevantPart("leftear", head.getChild("leftear"))
    override val leftEarJoint = EarJoint(leftEar, TransformedModelPart.Z_AXIS, RangeOfMotion(50F.toRadians(), 0F))
    override val rightEarJoint = EarJoint(rightEar, TransformedModelPart.Z_AXIS, RangeOfMotion((-50F).toRadians(), 0F))
    private val tail = registerRelevantPart("tail", body.getChild("tail"))

    override val portraitScale = 1.35F
    override val portraitTranslation = Vec3d(-0.2, 1.25, 0.0)

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
                tail.rotation(
                    function = sineFunction(
                        amplitude = 0.5F,
                        period = 5F
                    ),
                    axis = TransformedModelPart.Y_AXIS,
                    timeVariable = { _, _, ageInTicks -> ageInTicks / 20 },
                )
            ),
            transformedParts = arrayOf(
                leftArm.withRotation(2, 70f.toRadians()),
                rightArm.withRotation(2, (-70f).toRadians())
            )
        )
    }

    companion object {
        val LAYER_LOCATION = EntityModelLayer(cobbledResource("blastoise"), "main")
        fun createBodyLayer(): TexturedModelData {
            val meshdefinition = ModelData()
            val partdefinition = meshdefinition.root
            val blastoise = partdefinition.addChild(
                "blastoise",
                ModelPartBuilder.create(),
                ModelTransform.pivot(0.0f, 24.0f, 0.0f)
            )
            val body = blastoise.addChild(
                "body",
                ModelPartBuilder.create().uv(0, 0)
                    .cuboid(-9.0f, -9.9128f, -6.7786f, 18.0f, 22.0f, 13.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, -17.0872f, 0.2786f)
            )
            val body_r1 = body.addChild(
                "body_r1",
                ModelPartBuilder.create().uv(22, 53)
                    .cuboid(-1.4167f, -1.5f, -6.0f, 3.0f, 3.0f, 4.0f, Dilation(0.0f))
                    .uv(0, 0).cuboid(-0.9167f, -1.0f, -2.0f, 2.0f, 2.0f, 4.0f, Dilation(0.0f))
                    .uv(49, 0).cuboid(-3.6667f, -2.5f, 1.0f, 7.0f, 5.0f, 6.0f, Dilation(0.0f)),
                ModelTransform.of(9.742f, -11.8479f, 3.5464f, -0.9599f, 0.0436f, 0.7418f)
            )
            val body_r2 = body.addChild(
                "body_r2",
                ModelPartBuilder.create().uv(60, 57)
                    .cuboid(-3.3333f, -2.5f, 1.0f, 7.0f, 5.0f, 6.0f, Dilation(0.0f))
                    .uv(62, 19).cuboid(-1.5833f, -1.5f, -6.0f, 3.0f, 3.0f, 4.0f, Dilation(0.0f))
                    .uv(52, 57).cuboid(-1.0833f, -1.0f, -2.0f, 2.0f, 2.0f, 4.0f, Dilation(0.0f)),
                ModelTransform.of(-9.742f, -11.8479f, 3.5464f, -0.9599f, -0.0436f, -0.7418f)
            )
            val tail = body.addChild(
                "tail",
                ModelPartBuilder.create().uv(114, 11)
                    .cuboid(0.0f, -4.0f, 0.0f, 0.0f, 6.0f, 7.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 10.0872f, 6.2214f)
            )
            val head = body.addChild(
                "head",
                ModelPartBuilder.create().uv(62, 11)
                    .cuboid(-4.5f, -4.5f, -7.5f, 9.0f, 5.0f, 3.0f, Dilation(0.0f))
                    .uv(0, 35).cuboid(-5.0f, -8.5f, -4.5f, 10.0f, 9.0f, 9.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, -8.4128f, -3.2786f)
            )
            val rightear = head.addChild(
                "rightear",
                ModelPartBuilder.create().uv(30, 60)
                    .cuboid(-2.0f, -3.0f, 0.0f, 4.0f, 3.0f, 0.0f, Dilation(0.02f)),
                ModelTransform.of(-3.5f, -7.75f, 1.5f, 0.0f, 0.0f, -0.5236f)
            )
            val leftear = head.addChild(
                "leftear",
                ModelPartBuilder.create().uv(0, 56)
                    .cuboid(-2.0f, -3.0f, 0.0f, 4.0f, 3.0f, 0.0f, Dilation(0.02f)),
                ModelTransform.of(3.5f, -7.75f, 1.5f, 0.0f, 0.0f, 0.5236f)
            )
            val leftarm = body.addChild(
                "leftarm",
                ModelPartBuilder.create().uv(0, 10)
                    .cuboid(12.0f, 0.0f, -1.5f, 3.0f, 0.0f, 3.0f, Dilation(0.0f))
                    .uv(38, 35).cuboid(-1.0f, -2.5f, -3.0f, 13.0f, 5.0f, 6.0f, Dilation(0.0f)),
                ModelTransform.pivot(9.0f, -5.4128f, -1.7786f)
            )
            val leftarm_r1 = leftarm.addChild(
                "leftarm_r1",
                ModelPartBuilder.create().uv(35, 39)
                    .cuboid(-1.5f, 0.0f, -1.5f, 3.0f, 0.0f, 2.0f, Dilation(0.0f)),
                ModelTransform.of(12.75f, 0.0f, -2.75f, 0.0f, 0.6109f, 0.0f)
            )
            val leftarm_r2 = leftarm.addChild(
                "leftarm_r2",
                ModelPartBuilder.create().uv(27, 42)
                    .cuboid(-1.5f, 0.0f, -0.5f, 3.0f, 0.0f, 2.0f, Dilation(0.0f)),
                ModelTransform.of(12.75f, 0.0f, 2.75f, 0.0f, -0.6109f, 0.0f)
            )
            val rightarm = body.addChild(
                "rightarm",
                ModelPartBuilder.create().uv(0, 35)
                    .cuboid(-15.0f, 0.0f, -1.5f, 3.0f, 0.0f, 3.0f, Dilation(0.0f))
                    .uv(38, 46).cuboid(-12.0f, -2.5f, -3.0f, 13.0f, 5.0f, 6.0f, Dilation(0.0f)),
                ModelTransform.pivot(-9.0f, -5.4128f, -1.7786f)
            )
            val rightarm_r1 = rightarm.addChild(
                "rightarm_r1",
                ModelPartBuilder.create().uv(47, 0)
                    .cuboid(-1.5f, 0.0f, -1.5f, 3.0f, 0.0f, 2.0f, Dilation(0.0f)),
                ModelTransform.of(-12.75f, 0.0f, -2.75f, 0.0f, -0.6109f, 0.0f)
            )
            val rightarm_r2 = rightarm.addChild(
                "rightarm_r2",
                ModelPartBuilder.create().uv(36, 46)
                    .cuboid(-1.5f, 0.0f, -0.5f, 3.0f, 0.0f, 2.0f, Dilation(0.0f)),
                ModelTransform.of(-12.75f, 0.0f, 2.75f, 0.0f, 0.6109f, 0.0f)
            )
            val leftleg = body.addChild(
                "leftleg",
                ModelPartBuilder.create().uv(29, 29)
                    .cuboid(0.0f, 4.5f, -10.0f, 0.0f, 4.0f, 6.0f, Dilation(0.0f))
                    .uv(30, 57).cuboid(-3.5f, -1.5f, -4.0f, 7.0f, 10.0f, 8.0f, Dilation(0.0f)),
                ModelTransform.of(7.0f, 8.5872f, -2.2786f, 0.0f, -0.0873f, 0.0f)
            )
            val leftleg_r1 = leftleg.addChild(
                "leftleg_r1",
                ModelPartBuilder.create().uv(0, 37)
                    .cuboid(0.0f, -1.5f, -2.0f, 0.0f, 3.0f, 4.0f, Dilation(0.0f)),
                ModelTransform.of(3.0f, 7.0f, -5.75f, 0.0f, -0.1745f, 0.0f)
            )
            val leftleg_r2 = leftleg.addChild(
                "leftleg_r2",
                ModelPartBuilder.create().uv(0, 49)
                    .cuboid(0.0f, -1.5f, -2.0f, 0.0f, 3.0f, 4.0f, Dilation(0.0f)),
                ModelTransform.of(-3.0f, 7.0f, -5.75f, 0.0f, 0.1745f, 0.0f)
            )
            val rightleg = body.addChild(
                "rightleg",
                ModelPartBuilder.create().uv(0, 0)
                    .cuboid(0.0f, 4.5f, -10.0f, 0.0f, 4.0f, 6.0f, Dilation(0.0f))
                    .uv(0, 53).cuboid(-3.5f, -1.5f, -4.0f, 7.0f, 10.0f, 8.0f, Dilation(0.0f)),
                ModelTransform.of(-7.0f, 8.5872f, -2.2786f, 0.0f, 0.0873f, 0.0f)
            )
            val rightleg_r1 = rightleg.addChild(
                "rightleg_r1",
                ModelPartBuilder.create().uv(0, 34)
                    .cuboid(0.0f, -1.5f, -2.0f, 0.0f, 3.0f, 4.0f, Dilation(0.0f)),
                ModelTransform.of(-3.0f, 7.0f, -5.75f, 0.0f, 0.1745f, 0.0f)
            )
            val rightleg_r2 = rightleg.addChild(
                "rightleg_r2",
                ModelPartBuilder.create().uv(29, 35)
                    .cuboid(0.0f, -1.5f, -2.0f, 0.0f, 3.0f, 4.0f, Dilation(0.0f)),
                ModelTransform.of(3.0f, 7.0f, -5.75f, 0.0f, -0.1745f, 0.0f)
            )
            return TexturedModelData.of(meshdefinition, 128, 128)
        }
    }
}