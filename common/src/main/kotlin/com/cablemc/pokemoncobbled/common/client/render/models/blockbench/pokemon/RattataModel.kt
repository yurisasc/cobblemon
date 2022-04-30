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
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.cablemc.pokemoncobbled.common.util.math.geometry.toRadians
import net.minecraft.client.model.*
import net.minecraft.client.render.entity.model.EntityModelLayer
import net.minecraft.util.math.Vec3d

class RattataModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame, QuadrupedFrame, EaredFrame {
    override val rootPart = registerRelevantPart("rattata", root.getChild("rattata"))
    override val foreLeftLeg = registerRelevantPart("leftfrontleg", rootPart.getChildOf("body", "leftfrontleg"))
    override val hindLeftLeg = registerRelevantPart("leftbackleg", rootPart.getChildOf("body", "leftbackleg"))
    override val hindRightLeg = registerRelevantPart("rightbackleg", rootPart.getChildOf("body", "rightbackleg"))
    override val foreRightLeg = registerRelevantPart("rightfrontleg", rootPart.getChildOf("body", "rightfrontleg"))
    override val head = registerRelevantPart("head", rootPart.getChildOf("body", "head"))
    val eyes = registerRelevantPart("eyes", rootPart.getChildOf("body", "head", "eyes"))
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

    val tail = registerRelevantPart("tail", rootPart.getChildOf("body", "tail"))
    val tail2 = registerRelevantPart("tail2", rootPart.getChildOf("body", "tail", "tail2"))
    override val portraitScale = 2.5F
    override val portraitTranslation = Vec3d(-0.25, -2.03, 0.0)

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
                QuadrupedWalkAnimation(this),
                SingleBoneLookAnimation(this)
            ),
            transformedParts = arrayOf()
        )
    }

    companion object {
        val LAYER_LOCATION = EntityModelLayer(cobbledResource("rattata"), "main")
        fun createBodyLayer(): TexturedModelData {
            val meshdefinition = ModelData()
            val partdefinition = meshdefinition.root
            val rattata =
                partdefinition.addChild(
                    "rattata",
                    ModelPartBuilder.create(),
                    ModelTransform.pivot(0.0f, 24.0f, 0.0f)
                )
            val body = rattata.addChild(
                "body",
                ModelPartBuilder.create().uv(0, 0)
                    .cuboid(-2.5f, -2.0f, -4.0f, 5.0f, 4.0f, 8.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, -4.0f, 0.0f)
            )
            val head = body.addChild(
                "head",
                ModelPartBuilder.create().uv(0, 12)
                    .cuboid(-2.5f, -3.25f, -3.75f, 5.0f, 5.0f, 4.0f, Dilation(0.02f))
                    .uv(16, 23).cuboid(-1.5f, -1.0f, -5.75f, 3.0f, 2.0f, 2.0f, Dilation(0.0f))
                    .uv(0, 12).cuboid(-1.0f, 0.5f, -5.25f, 2.0f, 2.0f, 0.0f, Dilation(0.02f)),
                ModelTransform.pivot(0.0f, -1.0f, -3.5f)
            )
            val cube_r1 = head.addChild(
                "cube_r1",
                ModelPartBuilder.create().uv(18, 7)
                    .cuboid(-2.5f, -0.5f, 0.25f, 5.0f, 1.0f, 0.0f, Dilation(0.02f)),
                ModelTransform.of(-5.0f, 0.25f, -1.75f, 0.0f, 0.2618f, 0.0873f)
            )
            val cube_r2 = head.addChild(
                "cube_r2",
                ModelPartBuilder.create().uv(24, 0)
                    .cuboid(-2.5f, -0.5f, 0.25f, 5.0f, 1.0f, 0.0f, Dilation(0.02f)),
                ModelTransform.of(5.0f, 0.25f, -1.75f, 0.0f, -0.2618f, -0.0873f)
            )
            val eyes = head.addChild("eyes", ModelPartBuilder.create(), ModelTransform.pivot(0.0f, -1.25f, -2.5f))
            val cube_r3 = eyes.addChild(
                "cube_r3",
                ModelPartBuilder.create().uv(0, 12)
                    .cuboid(0.0f, -1.0f, -1.0f, 0.0f, 2.0f, 2.0f, Dilation(0.02f))
                    .uv(0, 12).mirrored().cuboid(-5.05f, -1.0f, -1.0f, 0.0f, 2.0f, 2.0f, Dilation(0.02f))
                    .mirrored(false),
                ModelTransform.of(2.525f, 0.0f, 0.0f, 0.0436f, 0.0f, 0.0f)
            )
            val leftear =
                head.addChild("leftear", ModelPartBuilder.create(), ModelTransform.pivot(2.25f, -2.25f, -1.0f))
            val cube_r4 = leftear.addChild(
                "cube_r4",
                ModelPartBuilder.create().uv(24, 12)
                    .cuboid(0.25f, -10.0f, -3.5f, 4.0f, 4.0f, 0.0f, Dilation(0.02f)),
                ModelTransform.of(-2.0f, 7.25f, 3.5f, 0.1187f, -0.5168f, -0.1507f)
            )
            val rightear =
                head.addChild("rightear", ModelPartBuilder.create(), ModelTransform.pivot(-2.25f, -2.25f, -1.0f))
            val cube_r5 = rightear.addChild(
                "cube_r5",
                ModelPartBuilder.create().uv(0, 23)
                    .cuboid(-4.25f, -10.0f, -3.5f, 4.0f, 4.0f, 0.0f, Dilation(0.02f)),
                ModelTransform.of(2.0f, 7.25f, 3.5f, 0.1189f, 0.5187f, 0.1509f)
            )
            val tail = body.addChild(
                "tail",
                ModelPartBuilder.create().uv(0, 19)
                    .cuboid(0.0f, 0.0f, 3.0f, 0.0f, 1.0f, 3.0f, Dilation(0.02f))
                    .uv(0, 18).cuboid(0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 3.0f, Dilation(0.02f)),
                ModelTransform.pivot(0.0f, -1.0f, 4.0f)
            )
            val tail2 = tail.addChild(
                "tail2",
                ModelPartBuilder.create().uv(14, 7)
                    .cuboid(0.0f, -3.0f, 1.0f, 0.0f, 4.0f, 5.0f, Dilation(0.02f)),
                ModelTransform.pivot(0.0f, 0.0f, 5.0f)
            )
            val leftbackleg = body.addChild(
                "leftbackleg",
                ModelPartBuilder.create().uv(0, 0)
                    .cuboid(-1.0f, -1.0f, -1.0f, 2.0f, 5.0f, 2.0f, Dilation(0.0f)),
                ModelTransform.pivot(1.75f, 0.0f, 2.75f)
            )
            val leftfrontleg = body.addChild(
                "leftfrontleg",
                ModelPartBuilder.create().uv(18, 16)
                    .cuboid(-1.0f, -1.0f, -1.0f, 2.0f, 5.0f, 2.0f, Dilation(0.0f)),
                ModelTransform.pivot(1.75f, 0.0f, -2.5f)
            )
            val rightbackleg = body.addChild(
                "rightbackleg",
                ModelPartBuilder.create().uv(18, 0)
                    .cuboid(-1.0f, -1.0f, -1.0f, 2.0f, 5.0f, 2.0f, Dilation(0.0f)),
                ModelTransform.pivot(-1.75f, 0.0f, 2.75f)
            )
            val rightfrontleg = body.addChild(
                "rightfrontleg",
                ModelPartBuilder.create().uv(8, 21)
                    .cuboid(-1.0f, -1.0f, -1.0f, 2.0f, 5.0f, 2.0f, Dilation(0.0f)),
                ModelTransform.pivot(-1.75f, 0.0f, -2.5f)
            )
            return TexturedModelData.of(meshdefinition, 64, 64)
        }
    }
}
