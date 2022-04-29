package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.EarJoint
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.RangeOfMotion
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.SingleBoneLookAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.EaredFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.HeadedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.getChildOf
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.PoseType
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.TransformedModelPart.Companion.Z_AXIS
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.cablemc.pokemoncobbled.common.util.math.geometry.toRadians
import net.minecraft.client.model.*
import net.minecraft.client.render.entity.model.EntityModelLayer
import net.minecraft.util.math.Vec3d
class RaticateModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame, EaredFrame {
    override val rootPart = registerRelevantPart("raticate", root.getChild("raticate"))
    override val head = registerRelevantPart("head", rootPart.getChildOf("body", "head"))
    override val leftEarJoint: EarJoint = EarJoint(
        registerRelevantPart("leftear", rootPart.getChildOf("body", "head", "leftear")),
        Z_AXIS,
        RangeOfMotion(
            0F.toRadians(),
            -20F.toRadians()
        )
    )
    override val rightEarJoint: EarJoint = EarJoint(
        registerRelevantPart("rightear", rootPart.getChildOf("body", "head", "rightear")),
        Z_AXIS,
        RangeOfMotion(
            0F.toRadians(),
            20F.toRadians()
        )
    )
    override val portraitScale = 1.5F
    override val portraitTranslation = Vec3d(0.0, -0.33, 0.0)

    override val profileScale = 1.0F
    override val profileTranslation = Vec3d(0.0, 0.0, 0.0)

    init {
        registerPoses()
    }

    override fun registerPoses() {
        registerPose(
            poseType = PoseType.WALK,
            condition = { true },
            idleAnimations = arrayOf(
                SingleBoneLookAnimation(this)
            ),
            transformedParts = arrayOf()
        )
    }

    companion object {
        val LAYER_LOCATION = EntityModelLayer(cobbledResource("raticate"), "main")
        fun createBodyLayer(): TexturedModelData {
            val meshdefinition = ModelData()
            val partdefinition: ModelPartData = meshdefinition.root
            val raticate = partdefinition.addChild(
                "raticate",
                ModelPartBuilder.create(),
                ModelTransform.pivot(0.0f, 24.0f, 0.0f)
            )
            val body = raticate.addChild(
                "body",
                ModelPartBuilder.create().uv(0, 0)
                    .cuboid(-4.5f, -4.5f, -3.5f, 9.0f, 9.0f, 7.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, -5.5f, 0.0f)
            )
            val head = body.addChild(
                "head",
                ModelPartBuilder.create().uv(0, 16)
                    .cuboid(-4.0f, -7.25f, -4.0f, 8.0f, 8.0f, 7.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, -3.75f, -1.0f)
            )
            val cube_r1 = head.addChild(
                "cube_r1",
                ModelPartBuilder.create().uv(0, 31).mirrored()
                    .cuboid(-3.5f, -3.5f, 0.25f, 7.0f, 7.0f, 0.0f, Dilation(0.02f)).mirrored(false),
                ModelTransform.of(7.25f, -1.75f, -2.75f, 0.0f, -0.2618f, 0.0873f)
            )
            val cube_r2 = head.addChild(
                "cube_r2",
                ModelPartBuilder.create().uv(0, 31)
                    .cuboid(-3.5f, -3.5f, 0.25f, 7.0f, 7.0f, 0.0f, Dilation(0.02f)),
                ModelTransform.of(-7.25f, -1.75f, -2.75f, 0.0f, 0.2618f, -0.0873f)
            )
            val cube_r3 = head.addChild(
                "cube_r3",
                ModelPartBuilder.create().uv(30, 23)
                    .cuboid(-4.0f, -1.5572f, 0.484f, 7.0f, 2.0f, 0.0f, Dilation(0.02f)),
                ModelTransform.of(0.5f, -7.8928f, 0.016f, -0.8727f, 0.0f, 0.0f)
            )
            val cube_r4 = head.addChild(
                "cube_r4",
                ModelPartBuilder.create().uv(7, 61)
                    .cuboid(-1.0f, -1.0038f, -0.5128f, 2.0f, 2.0f, 0.0f, Dilation(0.0f)),
                ModelTransform.of(0.0f, -1.2462f, -3.5872f, 0.0873f, 0.0f, 0.0f)
            )
            val cube_r5 = head.addChild(
                "cube_r5",
                ModelPartBuilder.create().uv(0, 61)
                    .cuboid(-1.0f, -0.9962f, -0.5128f, 2.0f, 2.0f, 0.0f, Dilation(0.0f)),
                ModelTransform.of(0.0f, -3.2538f, -3.5872f, -0.0873f, 0.0f, 0.0f)
            )
            val eye = head.addChild("eye", ModelPartBuilder.create(), ModelTransform.pivot(0.0f, -5.5f, -0.75f))
            val cube_r6 = eye.addChild(
                "cube_r6",
                ModelPartBuilder.create().uv(58, -3)
                    .cuboid(4.0f, -1.0f, -2.0f, 0.0f, 2.0f, 3.0f, Dilation(0.02f))
                    .uv(58, -3).mirrored().cuboid(-4.0f, -1.0f, -2.0f, 0.0f, 2.0f, 3.0f, Dilation(0.02f))
                    .mirrored(false),
                ModelTransform.of(0.0f, 0.0f, 0.0f, 0.2182f, 0.0f, 0.0f)
            )
            val righteyelid = eye.addChild("righteyelid", ModelPartBuilder.create(), ModelTransform.pivot(0.0f, 0.0f, 0.0f))
            val cube_r7 = righteyelid.addChild(
                "cube_r7",
                ModelPartBuilder.create().uv(23, 18).mirrored()
                    .cuboid(-3.9f, -1.0f, -2.0f, 1.0f, 2.0f, 3.0f, Dilation(0.04f)).mirrored(false),
                ModelTransform.of(0.0f, 0.0f, 0.0f, 0.2182f, 0.0f, 0.0f)
            )
            val lefteyelid =
                eye.addChild("lefteyelid", ModelPartBuilder.create(), ModelTransform.pivot(0.0f, 0.0f, 0.0f))
            val cube_r8 = lefteyelid.addChild(
                "cube_r8",
                ModelPartBuilder.create().uv(23, 18)
                    .cuboid(2.9f, -1.0f, -2.0f, 1.0f, 2.0f, 3.0f, Dilation(0.04f)),
                ModelTransform.of(0.0f, 0.0f, 0.0f, 0.2182f, 0.0f, 0.0f)
            )
            val leftear = head.addChild(
                "leftear",
                ModelPartBuilder.create(),
                ModelTransform.of(3.75f, -5.0f, 1.25f, 0.0f, 0.0f, -0.0873f)
            )
            val cube_r9 = leftear.addChild(
                "cube_r9",
                ModelPartBuilder.create().uv(32, 10)
                    .cuboid(-4.25f, -5.25f, 2.25f, 6.0f, 6.0f, 0.0f, Dilation(0.02f)),
                ModelTransform.of(4.5f, 1.5f, 1.0f, 0.1745f, -0.5236f, 0.0f)
            )
            val rightear = head.addChild(
                "rightear",
                ModelPartBuilder.create(),
                ModelTransform.of(-3.75f, -5.0f, 1.25f, 0.0f, 0.0f, 0.0873f)
            )
            val cube_r10 = rightear.addChild(
                "cube_r10",
                ModelPartBuilder.create().uv(32, 10).mirrored()
                    .cuboid(-1.75f, -5.25f, 2.25f, 6.0f, 6.0f, 0.0f, Dilation(0.02f)).mirrored(false),
                ModelTransform.of(-4.5f, 1.5f, 1.0f, 0.1745f, 0.5236f, 0.0f)
            )
            val tail = body.addChild(
                "tail",
                ModelPartBuilder.create().uv(14, 31)
                    .cuboid(-2.5f, -1.5f, 0.0f, 3.0f, 3.0f, 5.0f, Dilation(0.02f)),
                ModelTransform.pivot(1.0f, 2.5f, 3.5f)
            )
            val tail2 = tail.addChild(
                "tail2",
                ModelPartBuilder.create().uv(14, 39)
                    .cuboid(-1.0f, -1.5f, -1.0f, 2.0f, 2.0f, 5.0f, Dilation(0.02f))
                    .uv(14, 46).cuboid(-1.0f, -1.5f, 4.0f, 2.0f, 2.0f, 5.0f, Dilation(0.02f)),
                ModelTransform.pivot(-1.0f, 0.5f, 6.0f)
            )
            val tail3 = tail2.addChild(
                "tail3",
                ModelPartBuilder.create().uv(14, 53)
                    .cuboid(-0.5f, -1.0f, 3.0f, 1.0f, 1.0f, 5.0f, Dilation(0.02f)),
                ModelTransform.pivot(0.0f, 0.0f, 6.0f)
            )
            val lefthand = body.addChild(
                "lefthand",
                ModelPartBuilder.create().uv(32, 33)
                    .cuboid(-1.0f, -0.5f, -2.0f, 2.0f, 1.0f, 2.0f, Dilation(0.0f)),
                ModelTransform.pivot(4.0f, -0.5f, -3.25f)
            )
            val cube_r11 = lefthand.addChild(
                "cube_r11",
                ModelPartBuilder.create().uv(0, 20)
                    .cuboid(0.0f, -0.5f, -1.0101f, 0.0f, 1.0f, 2.0f, Dilation(0.02f)),
                ModelTransform.of(-0.0081f, 0.0717f, -2.9438f, 0.0873f, 0.0f, 0.0f)
            )
            val cube_r12 = lefthand.addChild(
                "cube_r12",
                ModelPartBuilder.create().uv(0, 19)
                    .cuboid(0.9105f, -0.5f, -1.1554f, 0.0f, 1.0f, 2.0f, Dilation(0.02f)),
                ModelTransform.of(-0.0081f, 0.0717f, -2.9438f, 0.0873f, -0.1745f, 0.0f)
            )
            val cube_r13 = lefthand.addChild(
                "cube_r13",
                ModelPartBuilder.create().uv(0, 18)
                    .cuboid(-0.9105f, -0.5f, -1.1554f, 0.0f, 1.0f, 2.0f, Dilation(0.02f)),
                ModelTransform.of(-0.0081f, 0.0717f, -2.9438f, 0.0873f, 0.1745f, 0.0f)
            )
            val righthand = body.addChild(
                "righthand",
                ModelPartBuilder.create().uv(26, 32)
                    .cuboid(-1.0f, -0.5f, -2.0f, 2.0f, 1.0f, 2.0f, Dilation(0.0f)),
                ModelTransform.pivot(-4.0f, -0.5f, -3.25f)
            )
            val cube_r14 = righthand.addChild(
                "cube_r14",
                ModelPartBuilder.create().uv(0, 0)
                    .cuboid(0.0f, -0.5f, -1.0101f, 0.0f, 1.0f, 2.0f, Dilation(0.02f)),
                ModelTransform.of(0.0081f, 0.0717f, -2.9438f, 0.0873f, 0.0f, 0.0f)
            )
            val cube_r15 = righthand.addChild(
                "cube_r15",
                ModelPartBuilder.create().uv(0, 16)
                    .cuboid(-0.9105f, -0.5f, -1.1554f, 0.0f, 1.0f, 2.0f, Dilation(0.02f)),
                ModelTransform.of(0.0081f, 0.0717f, -2.9438f, 0.0873f, 0.1745f, 0.0f)
            )
            val cube_r16 = righthand.addChild(
                "cube_r16",
                ModelPartBuilder.create().uv(0, 17)
                    .cuboid(0.9105f, -0.5f, -1.1554f, 0.0f, 1.0f, 2.0f, Dilation(0.02f)),
                ModelTransform.of(0.0081f, 0.0717f, -2.9438f, 0.0873f, -0.1745f, 0.0f)
            )
            val leftfoot = body.addChild(
                "leftfoot",
                ModelPartBuilder.create().uv(25, 26)
                    .cuboid(-1.5f, 0.0f, -3.25f, 3.0f, 1.0f, 5.0f, Dilation(0.0f))
                    .uv(0, 14).cuboid(0.0f, 0.0f, -6.25f, 0.0f, 1.0f, 3.0f, Dilation(0.02f)),
                ModelTransform.pivot(2.75f, 4.5f, 0.75f)
            )
            val cube_r17 = leftfoot.addChild(
                "cube_r17",
                ModelPartBuilder.create().uv(0, 13)
                    .cuboid(0.0f, -0.5f, -1.5f, 0.0f, 1.0f, 3.0f, Dilation(0.02f)),
                ModelTransform.of(-1.5095f, 0.5f, -4.7274f, 0.0f, 0.1745f, 0.0f)
            )
            val cube_r18 = leftfoot.addChild(
                "cube_r18",
                ModelPartBuilder.create().uv(0, 3)
                    .cuboid(0.0f, -0.5f, -1.5f, 0.0f, 1.0f, 3.0f, Dilation(0.02f)),
                ModelTransform.of(1.5095f, 0.5f, -4.7274f, 0.0f, -0.1745f, 0.0f)
            )
            val rightfoot = body.addChild(
                "rightfoot",
                ModelPartBuilder.create().uv(25, 0)
                    .cuboid(-1.5f, 0.0f, -3.25f, 3.0f, 1.0f, 5.0f, Dilation(0.0f))
                    .uv(0, 1).cuboid(0.0f, 0.0f, -6.25f, 0.0f, 1.0f, 3.0f, Dilation(0.02f)),
                ModelTransform.pivot(-2.75f, 4.5f, 0.75f)
            )
            val cube_r19 = rightfoot.addChild(
                "cube_r19",
                ModelPartBuilder.create().uv(0, 0)
                    .cuboid(0.0f, -0.5f, -1.5f, 0.0f, 1.0f, 3.0f, Dilation(0.02f)),
                ModelTransform.of(1.5095f, 0.5f, -4.7274f, 0.0f, -0.1745f, 0.0f)
            )
            val cube_r20 = rightfoot.addChild(
                "cube_r20",
                ModelPartBuilder.create().uv(0, 2)
                    .cuboid(0.0f, -0.5f, -1.5f, 0.0f, 1.0f, 3.0f, Dilation(0.02f)),
                ModelTransform.of(-1.5095f, 0.5f, -4.7274f, 0.0f, 0.1745f, 0.0f)
            )
            return TexturedModelData.of(meshdefinition, 64, 64)
        }
    }
}
