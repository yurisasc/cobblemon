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

class WartortleModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame, BipedFrame, BimanualFrame, EaredFrame {
    override val rootPart = registerRelevantPart("wartortle", root.getChild("wartortle"))
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

    override val portraitScale = 1.6F
    override val portraitTranslation = Vec3d(-0.05, 0.40, 0.0)
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
                                        amplitude = 0.4F,
                                        period = 5F
                                ),
                                axis = TransformedModelPart.Y_AXIS,
                                timeVariable = { _, _, ageInTicks -> ageInTicks / 20 },
                        )
                ),
                transformedParts = arrayOf(
                        leftArm.withRotation(2, 70f.toRadians()),
                        rightArm.withRotation(2, (-70f).toRadians()),
                )
        )
    }

    companion object {
        val LAYER_LOCATION = EntityModelLayer(cobbledResource("wartortle"), "main")
        fun createBodyLayer(): TexturedModelData {
            val meshdefinition = ModelData()
            val partdefinition = meshdefinition.root
            val wartortle = partdefinition.addChild(
                "wartortle",
                ModelPartBuilder.create(),
                ModelTransform.pivot(0.0f, 24.0f, 0.0f)
            )
            val body = wartortle.addChild(
                "body",
                ModelPartBuilder.create().uv(0, 0)
                    .cuboid(-5.0f, -6.0f, -4.0f, 10.0f, 12.0f, 8.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, -11.0f, 0.0f)
            )
            val tail = body.addChild(
                "tail",
                ModelPartBuilder.create().uv(0, 7)
                    .cuboid(0.0f, -13.0f, 0.0f, 0.0f, 16.0f, 13.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 3.0f, 4.0f)
            )
            val leftarm = body.addChild(
                "leftarm",
                ModelPartBuilder.create().uv(28, 0)
                    .cuboid(-1.0f, -2.0f, -2.0f, 8.0f, 4.0f, 4.0f, Dilation(0.0f)),
                ModelTransform.of(5.0f, -3.5f, -1.5f, 0.0057f, 0.0001f, -0.0001f)
            )
            val rightarm = body.addChild(
                "rightarm",
                ModelPartBuilder.create().uv(22, 35)
                    .cuboid(-7.0f, -2.0f, -2.0f, 8.0f, 4.0f, 4.0f, Dilation(0.0f)),
                ModelTransform.pivot(-5.0f, -3.5f, -1.5f)
            )
            val leftleg = body.addChild(
                "leftleg",
                ModelPartBuilder.create().uv(36, 8)
                    .cuboid(-2.0f, -1.0f, -2.0f, 4.0f, 7.0f, 4.0f, Dilation(0.0f)),
                ModelTransform.pivot(3.5f, 5.0f, -1.0f)
            )
            val rightleg = body.addChild(
                "rightleg",
                ModelPartBuilder.create().uv(0, 36)
                    .cuboid(-2.0f, -1.0f, -2.0f, 4.0f, 7.0f, 4.0f, Dilation(0.0f)),
                ModelTransform.pivot(-3.5f, 5.0f, -1.0f)
            )
            val head = body.addChild(
                "head",
                ModelPartBuilder.create().uv(26, 20)
                    .cuboid(-4.5f, -7.0f, -4.0f, 9.0f, 7.0f, 8.0f, Dilation(0.0f)),
                ModelTransform.of(0.0f, -6.0f, -1.5f, 0.0003f, 0.0003f, -0.0076f)
            )
            val head_r1 = head.addChild(
                "head_r1",
                ModelPartBuilder.create().uv(26, 20).mirrored()
                    .cuboid(-1.5f, -2.0f, 0.0f, 3.0f, 4.0f, 0.0f, Dilation(0.0f)).mirrored(false),
                ModelTransform.of(2.75f, -4.0f, -4.05f, 0.0f, 0.0f, -0.0873f)
            )
            val head_r2 = head.addChild(
                "head_r2",
                ModelPartBuilder.create().uv(26, 20)
                    .cuboid(-1.5f, -2.0f, 0.0f, 3.0f, 4.0f, 0.0f, Dilation(0.0f)),
                ModelTransform.of(-2.75f, -4.0f, -4.05f, 0.0f, 0.0f, 0.0873f)
            )
            val leftear = head.addChild(
                "leftear",
                ModelPartBuilder.create().uv(16, 38)
                    .cuboid(0.0493f, -8.9483f, -0.1887f, 0.0f, 9.0f, 5.0f, Dilation(0.0f)),
                ModelTransform.of(4.475f, -4.925f, -1.975f, -0.2224f, 0.0756f, 0.0693f)
            )
            val rightear = head.addChild(
                "rightear",
                ModelPartBuilder.create().uv(26, 38)
                    .cuboid(-0.0493f, -8.9483f, -0.1887f, 0.0f, 9.0f, 5.0f, Dilation(0.0f)),
                ModelTransform.of(-4.475f, -4.925f, -1.975f, -0.2224f, -0.0756f, -0.0693f)
            )
            return TexturedModelData.of(meshdefinition, 64, 64)
        }
    }
}