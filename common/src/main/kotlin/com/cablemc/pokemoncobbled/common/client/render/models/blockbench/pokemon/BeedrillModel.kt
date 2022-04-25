package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.EarJoint
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.RangeOfMotion
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.QuadrupedWalkAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.SingleBoneLookAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.BiWingedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.EaredFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.HeadedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.QuadrupedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.getChildOf
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.PoseType
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.TransformedModelPart.Companion.Z_AXIS
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.cablemc.pokemoncobbled.common.util.math.geometry.toRadians
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.*
import net.minecraft.world.phys.Vec3

class BeedrillModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame {
    override val rootPart = registerRelevantPart("beedrill", root.getChild("beedrill"))
    override val head = registerRelevantPart("head", rootPart.getChildOf("body", "neck", "head"))
    override val portraitScale = 1.95F
    override val portraitTranslation = Vec3(-0.05, -0.7, 0.0)

    override val profileScale = 1.0F
    override val profileTranslation = Vec3(0.0, 0.0, 0.0)

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
        val LAYER_LOCATION = ModelLayerLocation(cobbledResource("weedle"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition: PartDefinition = meshdefinition.root
            val weedle: PartDefinition =
                partdefinition.addOrReplaceChild("weedle", CubeListBuilder.create(), PartPose.offset(0.0f, 24.0f, 0.0f))
            val body: PartDefinition = weedle.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(0, 23)
                    .addBox(-2.5f, -2.5f, -1.5f, 5.0f, 5.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -2.5f, 4.5f)
            )
            val rightleg3: PartDefinition = body.addOrReplaceChild(
                "rightleg3",
                CubeListBuilder.create().texOffs(20, 0)
                    .addBox(-1.0f, -0.5f, -1.0f, 1.0f, 2.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(-2.25f, 1.5f, 0.0f)
            )
            val leftleg3: PartDefinition = body.addOrReplaceChild(
                "leftleg3",
                CubeListBuilder.create().texOffs(0, 0).addBox(0.0f, -0.5f, -1.0f, 1.0f, 2.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(2.25f, 1.5f, 0.0f)
            )
            val tail: PartDefinition = body.addOrReplaceChild(
                "tail",
                CubeListBuilder.create().texOffs(26, 0).addBox(-2.0f, -2.0f, 0.0f, 4.0f, 4.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.5f, 1.5f)
            )
            val tail2: PartDefinition = tail.addOrReplaceChild(
                "tail2",
                CubeListBuilder.create().texOffs(23, 10)
                    .addBox(-2.0f, -2.0f, 0.0f, 4.0f, 4.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 3.0f)
            )
            val tail3: PartDefinition = tail2.addOrReplaceChild(
                "tail3",
                CubeListBuilder.create().texOffs(23, 28)
                    .addBox(-1.5f, -1.5f, 0.0f, 3.0f, 3.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.5f, 3.0f)
            )
            val tail4: PartDefinition = tail3.addOrReplaceChild(
                "tail4",
                CubeListBuilder.create().texOffs(0, 31).addBox(-1.5f, -1.5f, 0.0f, 3.0f, 3.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 3.0f)
            )
            val tail4_r1: PartDefinition = tail4.addOrReplaceChild(
                "tail4_r1",
                CubeListBuilder.create().texOffs(0, 11)
                    .addBox(0.0f, -2.5f, -1.0f, 0.0f, 5.0f, 2.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(0.0f, -3.0f, 1.75f, -0.2618f, 0.0f, 0.0f)
            )
            val neck: PartDefinition = body.addOrReplaceChild(
                "neck",
                CubeListBuilder.create().texOffs(16, 19)
                    .addBox(-2.5f, -2.5f, -4.0f, 5.0f, 5.0f, 4.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, -1.5f, -0.7418f, 0.0f, 0.0f)
            )
            val rightleg2: PartDefinition = neck.addOrReplaceChild(
                "rightleg2",
                CubeListBuilder.create().texOffs(16, 31)
                    .addBox(-1.0f, -0.5f, -1.0f, 1.0f, 2.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(-2.25f, 1.5f, -2.0f)
            )
            val leftleg2: PartDefinition = neck.addOrReplaceChild(
                "leftleg2",
                CubeListBuilder.create().texOffs(10, 31)
                    .addBox(0.0f, -0.5f, -1.0f, 1.0f, 2.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(2.25f, 1.5f, -2.0f)
            )
            val spine: PartDefinition = neck.addOrReplaceChild(
                "spine",
                CubeListBuilder.create().texOffs(0, 13)
                    .addBox(-2.5f, -2.5f, -5.0f, 5.0f, 5.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, -4.0f, -0.5236f, 0.0f, 0.0f)
            )
            val rightleg: PartDefinition = spine.addOrReplaceChild(
                "rightleg",
                CubeListBuilder.create().texOffs(32, 26)
                    .addBox(-1.0f, -0.5f, -1.0f, 1.0f, 2.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(-2.25f, 1.5f, -2.0f)
            )
            val leftleg: PartDefinition = spine.addOrReplaceChild(
                "leftleg",
                CubeListBuilder.create().texOffs(17, 14)
                    .addBox(0.0f, -0.5f, -1.0f, 1.0f, 2.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(2.25f, 1.5f, -2.0f)
            )
            val head: PartDefinition = spine.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3.5f, -3.0f, -6.0f, 7.0f, 7.0f, 6.0f, CubeDeformation(0.0f))
                    .texOffs(60, 0).addBox(1.9f, 4.0f, -4.75f, 1.0f, 0.0f, 1.0f, CubeDeformation(0.02f))
                    .texOffs(60, 0).mirror().addBox(-2.9f, 4.0f, -4.75f, 1.0f, 0.0f, 1.0f, CubeDeformation(0.02f))
                    .mirror(false)
                    .texOffs(30, 17).addBox(-1.5f, 3.5f, -3.75f, 3.0f, 2.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, -4.0f, -0.1309f, 0.0f, 0.0f)
            )
            val head_r1: PartDefinition = head.addOrReplaceChild(
                "head_r1",
                CubeListBuilder.create().texOffs(16, 23)
                    .addBox(0.0f, -1.5f, -2.5f, 0.0f, 3.0f, 5.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(0.0f, 1.25f, -8.25f, 0.0873f, 0.0f, 0.0f)
            )
            val righteyelid: PartDefinition = head.addOrReplaceChild(
                "righteyelid",
                CubeListBuilder.create().texOffs(37, 0).mirror()
                    .addBox(-4.15f, 2.9f, -5.75f, 2.0f, 1.0f, 2.0f, CubeDeformation(0.04f)).mirror(false)
                    .texOffs(37, 0).addBox(0.65f, 2.9f, -5.75f, 2.0f, 1.0f, 2.0f, CubeDeformation(0.04f)),
                PartPose.offset(0.75f, 0.0f, 0.0f)
            )
            val lefteyelid: PartDefinition =
                head.addOrReplaceChild("lefteyelid", CubeListBuilder.create(), PartPose.offset(-0.75f, 0.0f, 0.0f))
            return LayerDefinition.create(meshdefinition, 64, 64)
        }
    }
}