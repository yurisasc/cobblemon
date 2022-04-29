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
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.*
import net.minecraft.world.phys.Vec3d
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
    override val portraitTranslation = Vec3d0.0, -0.33, 0.0)

    override val profileScale = 1.0F
    override val profileTranslation = Vec3d0.0, 0.0, 0.0)

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
        val LAYER_LOCATION = ModelLayerLocation(cobbledResource("raticate"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition: PartDefinition = meshdefinition.root
            val raticate: PartDefinition = partdefinition.addOrReplaceChild(
                "raticate",
                CubeListBuilder.create(),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )
            val body: PartDefinition = raticate.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-4.5f, -4.5f, -3.5f, 9.0f, 9.0f, 7.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -5.5f, 0.0f)
            )
            val head: PartDefinition = body.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(0, 16)
                    .addBox(-4.0f, -7.25f, -4.0f, 8.0f, 8.0f, 7.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -3.75f, -1.0f)
            )
            val cube_r1: PartDefinition = head.addOrReplaceChild(
                "cube_r1",
                CubeListBuilder.create().texOffs(0, 31).mirror()
                    .addBox(-3.5f, -3.5f, 0.25f, 7.0f, 7.0f, 0.0f, CubeDeformation(0.02f)).mirror(false),
                PartPose.offsetAndRotation(7.25f, -1.75f, -2.75f, 0.0f, -0.2618f, 0.0873f)
            )
            val cube_r2: PartDefinition = head.addOrReplaceChild(
                "cube_r2",
                CubeListBuilder.create().texOffs(0, 31)
                    .addBox(-3.5f, -3.5f, 0.25f, 7.0f, 7.0f, 0.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(-7.25f, -1.75f, -2.75f, 0.0f, 0.2618f, -0.0873f)
            )
            val cube_r3: PartDefinition = head.addOrReplaceChild(
                "cube_r3",
                CubeListBuilder.create().texOffs(30, 23)
                    .addBox(-4.0f, -1.5572f, 0.484f, 7.0f, 2.0f, 0.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(0.5f, -7.8928f, 0.016f, -0.8727f, 0.0f, 0.0f)
            )
            val cube_r4: PartDefinition = head.addOrReplaceChild(
                "cube_r4",
                CubeListBuilder.create().texOffs(7, 61)
                    .addBox(-1.0f, -1.0038f, -0.5128f, 2.0f, 2.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -1.2462f, -3.5872f, 0.0873f, 0.0f, 0.0f)
            )
            val cube_r5: PartDefinition = head.addOrReplaceChild(
                "cube_r5",
                CubeListBuilder.create().texOffs(0, 61)
                    .addBox(-1.0f, -0.9962f, -0.5128f, 2.0f, 2.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -3.2538f, -3.5872f, -0.0873f, 0.0f, 0.0f)
            )
            val eye: PartDefinition =
                head.addOrReplaceChild("eye", CubeListBuilder.create(), PartPose.offset(0.0f, -5.5f, -0.75f))
            val cube_r6: PartDefinition = eye.addOrReplaceChild(
                "cube_r6",
                CubeListBuilder.create().texOffs(58, -3)
                    .addBox(4.0f, -1.0f, -2.0f, 0.0f, 2.0f, 3.0f, CubeDeformation(0.02f))
                    .texOffs(58, -3).mirror().addBox(-4.0f, -1.0f, -2.0f, 0.0f, 2.0f, 3.0f, CubeDeformation(0.02f))
                    .mirror(false),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.2182f, 0.0f, 0.0f)
            )
            val righteyelid: PartDefinition =
                eye.addOrReplaceChild("righteyelid", CubeListBuilder.create(), PartPose.offset(0.0f, 0.0f, 0.0f))
            val cube_r7: PartDefinition = righteyelid.addOrReplaceChild(
                "cube_r7",
                CubeListBuilder.create().texOffs(23, 18).mirror()
                    .addBox(-3.9f, -1.0f, -2.0f, 1.0f, 2.0f, 3.0f, CubeDeformation(0.04f)).mirror(false),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.2182f, 0.0f, 0.0f)
            )
            val lefteyelid: PartDefinition =
                eye.addOrReplaceChild("lefteyelid", CubeListBuilder.create(), PartPose.offset(0.0f, 0.0f, 0.0f))
            val cube_r8: PartDefinition = lefteyelid.addOrReplaceChild(
                "cube_r8",
                CubeListBuilder.create().texOffs(23, 18)
                    .addBox(2.9f, -1.0f, -2.0f, 1.0f, 2.0f, 3.0f, CubeDeformation(0.04f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.2182f, 0.0f, 0.0f)
            )
            val leftear: PartDefinition = head.addOrReplaceChild(
                "leftear",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(3.75f, -5.0f, 1.25f, 0.0f, 0.0f, -0.0873f)
            )
            val cube_r9: PartDefinition = leftear.addOrReplaceChild(
                "cube_r9",
                CubeListBuilder.create().texOffs(32, 10)
                    .addBox(-4.25f, -5.25f, 2.25f, 6.0f, 6.0f, 0.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(4.5f, 1.5f, 1.0f, 0.1745f, -0.5236f, 0.0f)
            )
            val rightear: PartDefinition = head.addOrReplaceChild(
                "rightear",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(-3.75f, -5.0f, 1.25f, 0.0f, 0.0f, 0.0873f)
            )
            val cube_r10: PartDefinition = rightear.addOrReplaceChild(
                "cube_r10",
                CubeListBuilder.create().texOffs(32, 10).mirror()
                    .addBox(-1.75f, -5.25f, 2.25f, 6.0f, 6.0f, 0.0f, CubeDeformation(0.02f)).mirror(false),
                PartPose.offsetAndRotation(-4.5f, 1.5f, 1.0f, 0.1745f, 0.5236f, 0.0f)
            )
            val tail: PartDefinition = body.addOrReplaceChild(
                "tail",
                CubeListBuilder.create().texOffs(14, 31)
                    .addBox(-2.5f, -1.5f, 0.0f, 3.0f, 3.0f, 5.0f, CubeDeformation(0.02f)),
                PartPose.offset(1.0f, 2.5f, 3.5f)
            )
            val tail2: PartDefinition = tail.addOrReplaceChild(
                "tail2",
                CubeListBuilder.create().texOffs(14, 39)
                    .addBox(-1.0f, -1.5f, -1.0f, 2.0f, 2.0f, 5.0f, CubeDeformation(0.02f))
                    .texOffs(14, 46).addBox(-1.0f, -1.5f, 4.0f, 2.0f, 2.0f, 5.0f, CubeDeformation(0.02f)),
                PartPose.offset(-1.0f, 0.5f, 6.0f)
            )
            val tail3: PartDefinition = tail2.addOrReplaceChild(
                "tail3",
                CubeListBuilder.create().texOffs(14, 53)
                    .addBox(-0.5f, -1.0f, 3.0f, 1.0f, 1.0f, 5.0f, CubeDeformation(0.02f)),
                PartPose.offset(0.0f, 0.0f, 6.0f)
            )
            val lefthand: PartDefinition = body.addOrReplaceChild(
                "lefthand",
                CubeListBuilder.create().texOffs(32, 33)
                    .addBox(-1.0f, -0.5f, -2.0f, 2.0f, 1.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(4.0f, -0.5f, -3.25f)
            )
            val cube_r11: PartDefinition = lefthand.addOrReplaceChild(
                "cube_r11",
                CubeListBuilder.create().texOffs(0, 20)
                    .addBox(0.0f, -0.5f, -1.0101f, 0.0f, 1.0f, 2.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(-0.0081f, 0.0717f, -2.9438f, 0.0873f, 0.0f, 0.0f)
            )
            val cube_r12: PartDefinition = lefthand.addOrReplaceChild(
                "cube_r12",
                CubeListBuilder.create().texOffs(0, 19)
                    .addBox(0.9105f, -0.5f, -1.1554f, 0.0f, 1.0f, 2.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(-0.0081f, 0.0717f, -2.9438f, 0.0873f, -0.1745f, 0.0f)
            )
            val cube_r13: PartDefinition = lefthand.addOrReplaceChild(
                "cube_r13",
                CubeListBuilder.create().texOffs(0, 18)
                    .addBox(-0.9105f, -0.5f, -1.1554f, 0.0f, 1.0f, 2.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(-0.0081f, 0.0717f, -2.9438f, 0.0873f, 0.1745f, 0.0f)
            )
            val righthand: PartDefinition = body.addOrReplaceChild(
                "righthand",
                CubeListBuilder.create().texOffs(26, 32)
                    .addBox(-1.0f, -0.5f, -2.0f, 2.0f, 1.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(-4.0f, -0.5f, -3.25f)
            )
            val cube_r14: PartDefinition = righthand.addOrReplaceChild(
                "cube_r14",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(0.0f, -0.5f, -1.0101f, 0.0f, 1.0f, 2.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(0.0081f, 0.0717f, -2.9438f, 0.0873f, 0.0f, 0.0f)
            )
            val cube_r15: PartDefinition = righthand.addOrReplaceChild(
                "cube_r15",
                CubeListBuilder.create().texOffs(0, 16)
                    .addBox(-0.9105f, -0.5f, -1.1554f, 0.0f, 1.0f, 2.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(0.0081f, 0.0717f, -2.9438f, 0.0873f, 0.1745f, 0.0f)
            )
            val cube_r16: PartDefinition = righthand.addOrReplaceChild(
                "cube_r16",
                CubeListBuilder.create().texOffs(0, 17)
                    .addBox(0.9105f, -0.5f, -1.1554f, 0.0f, 1.0f, 2.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(0.0081f, 0.0717f, -2.9438f, 0.0873f, -0.1745f, 0.0f)
            )
            val leftfoot: PartDefinition = body.addOrReplaceChild(
                "leftfoot",
                CubeListBuilder.create().texOffs(25, 26)
                    .addBox(-1.5f, 0.0f, -3.25f, 3.0f, 1.0f, 5.0f, CubeDeformation(0.0f))
                    .texOffs(0, 14).addBox(0.0f, 0.0f, -6.25f, 0.0f, 1.0f, 3.0f, CubeDeformation(0.02f)),
                PartPose.offset(2.75f, 4.5f, 0.75f)
            )
            val cube_r17: PartDefinition = leftfoot.addOrReplaceChild(
                "cube_r17",
                CubeListBuilder.create().texOffs(0, 13)
                    .addBox(0.0f, -0.5f, -1.5f, 0.0f, 1.0f, 3.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(-1.5095f, 0.5f, -4.7274f, 0.0f, 0.1745f, 0.0f)
            )
            val cube_r18: PartDefinition = leftfoot.addOrReplaceChild(
                "cube_r18",
                CubeListBuilder.create().texOffs(0, 3)
                    .addBox(0.0f, -0.5f, -1.5f, 0.0f, 1.0f, 3.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(1.5095f, 0.5f, -4.7274f, 0.0f, -0.1745f, 0.0f)
            )
            val rightfoot: PartDefinition = body.addOrReplaceChild(
                "rightfoot",
                CubeListBuilder.create().texOffs(25, 0)
                    .addBox(-1.5f, 0.0f, -3.25f, 3.0f, 1.0f, 5.0f, CubeDeformation(0.0f))
                    .texOffs(0, 1).addBox(0.0f, 0.0f, -6.25f, 0.0f, 1.0f, 3.0f, CubeDeformation(0.02f)),
                PartPose.offset(-2.75f, 4.5f, 0.75f)
            )
            val cube_r19: PartDefinition = rightfoot.addOrReplaceChild(
                "cube_r19",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(0.0f, -0.5f, -1.5f, 0.0f, 1.0f, 3.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(1.5095f, 0.5f, -4.7274f, 0.0f, -0.1745f, 0.0f)
            )
            val cube_r20: PartDefinition = rightfoot.addOrReplaceChild(
                "cube_r20",
                CubeListBuilder.create().texOffs(0, 2)
                    .addBox(0.0f, -0.5f, -1.5f, 0.0f, 1.0f, 3.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(-1.5095f, 0.5f, -4.7274f, 0.0f, 0.1745f, 0.0f)
            )
            return LayerDefinition.create(meshdefinition, 64, 64)
        }
    }
}
