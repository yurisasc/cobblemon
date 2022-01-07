package com.cablemc.pokemoncobbled.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.client.render.models.blockbench.animation.QuadrupedWalkAnimation
import com.cablemc.pokemoncobbled.client.render.models.blockbench.animation.SingleBoneLookAnimation
import com.cablemc.pokemoncobbled.client.render.models.blockbench.frame.HeadedFrame
import com.cablemc.pokemoncobbled.client.render.models.blockbench.frame.QuadrupedFrame
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pose.PoseType
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition


class IvysaurModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame, QuadrupedFrame {

    override val rootPart = registerRelevantPart("ivysaur", root.getChild("ivysaur"))
    override val head = registerRelevantPart("head", rootPart.getChild("body").getChild("head"))
    override val hindRightLeg = registerRelevantPart("rightbackleg", rootPart.getChild("body").getChild("rightbackleg"))
    override val hindLeftLeg = registerRelevantPart("leftbackleg", rootPart.getChild("body").getChild("leftbackleg"))
    override val foreRightLeg = registerRelevantPart("rightleg", rootPart.getChild("body").getChild("rightleg"))
    override val foreLeftLeg = registerRelevantPart("leftleg", rootPart.getChild("body").getChild("leftleg"))

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
        val LAYER_LOCATION = ModelLayerLocation(cobbledResource("ivysaur"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root
            val ivysaur = partdefinition.addOrReplaceChild(
                "ivysaur",
                CubeListBuilder.create(),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )
            val body =
                ivysaur.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0f, -5.5528f, 0.2424f))
            val body_r1 = body.addOrReplaceChild(
                "body_r1",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-8.0f, -6.0f, -1.0f, 9.0f, 6.0f, 12.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.5f, 2.5528f, -5.2424f, -0.0873f, 0.0f, 0.0f)
            )
            val head = body.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(0, 18)
                    .addBox(-5.0f, -5.5f, -4.25f, 10.0f, 7.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -2.1972f, -4.2424f)
            )
            val eyes = head.addOrReplaceChild(
                "eyes",
                CubeListBuilder.create().texOffs(54, 0).mirror()
                    .addBox(-1.0f, -5.0f, -2.0f, 3.0f, 3.0f, 2.0f, CubeDeformation(0.02f)).mirror(false)
                    .texOffs(54, 0).addBox(-8.0f, -5.0f, -2.0f, 3.0f, 3.0f, 2.0f, CubeDeformation(0.02f)),
                PartPose.offset(3.0f, 2.0f, -2.25f)
            )
            val pupils = eyes.addOrReplaceChild(
                "pupils",
                CubeListBuilder.create().texOffs(54, 5).mirror()
                    .addBox(-0.975f, -5.0f, -2.025f, 3.0f, 3.0f, 2.0f, CubeDeformation(0.02f)).mirror(false)
                    .texOffs(54, 5).addBox(-8.025f, -5.0f, -2.025f, 3.0f, 3.0f, 2.0f, CubeDeformation(0.02f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )
            val rightear = head.addOrReplaceChild(
                "rightear",
                CubeListBuilder.create().texOffs(0, 5)
                    .addBox(0.0f, -3.0f, -2.0f, 0.0f, 3.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-4.5f, -5.5f, -1.25f, 0.0f, 0.0f, -0.0873f)
            )
            val leftear = head.addOrReplaceChild(
                "leftear",
                CubeListBuilder.create().texOffs(18, 27)
                    .addBox(0.0f, -3.0f, -2.0f, 0.0f, 3.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(4.5f, -5.5f, -1.25f, 0.0f, 0.0f, 0.0873f)
            )
            val bulb =
                body.addOrReplaceChild("bulb", CubeListBuilder.create(), PartPose.offset(0.0f, -2.8105f, 2.5804f))
            val bulb_r1 = bulb.addOrReplaceChild(
                "bulb_r1",
                CubeListBuilder.create().texOffs(26, 25)
                    .addBox(-3.5f, -3.5f, -3.75f, 7.0f, 8.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -3.9668f, 1.0211f, -0.0436f, 0.0f, 0.0f)
            )
            val frontleaf = bulb.addOrReplaceChild(
                "frontleaf",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.25f, -0.7538f, -2.7213f, -1.1345f, 0.0f, 0.0f)
            )
            val frontleaf_r1 = frontleaf.addOrReplaceChild(
                "frontleaf_r1",
                CubeListBuilder.create().texOffs(24, 0)
                    .addBox(-2.25f, 4.5f, -9.5f, 6.0f, 0.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-0.9456f, -4.463f, 3.5f, 0.0436f, 0.0f, 0.0f)
            )
            val frontleaftip = frontleaf.addOrReplaceChild(
                "frontleaftip",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(-0.1956f, 0.578f, -5.7918f, 0.48f, 0.0f, 0.0f)
            )
            val frontleaftip_r1 = frontleaftip.addOrReplaceChild(
                "frontleaftip_r1",
                CubeListBuilder.create().texOffs(20, 18)
                    .addBox(-2.25f, 4.5f, -15.5f, 6.0f, 0.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-0.75f, -5.0275f, 9.3526f, 0.0436f, 0.0f, 0.0f)
            )
            val backleaf =
                bulb.addOrReplaceChild("backleaf", CubeListBuilder.create(), PartPose.offset(0.25f, -0.2538f, 3.0755f))
            val backleaf_r1 = backleaf.addOrReplaceChild(
                "backleaf_r1",
                CubeListBuilder.create().texOffs(0, 31)
                    .addBox(-2.25f, 4.5f, 3.5f, 6.0f, 0.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-0.9456f, -4.463f, -3.5f, -0.0436f, 0.0f, 0.0f)
            )
            val backleaftip = backleaf.addOrReplaceChild(
                "backleaftip",
                CubeListBuilder.create(),
                PartPose.offset(-0.1956f, 0.578f, 5.7918f)
            )
            val backleaftip_r1 = backleaftip.addOrReplaceChild(
                "backleaftip_r1",
                CubeListBuilder.create().texOffs(24, 6)
                    .addBox(-2.25f, 4.5f, 9.5f, 6.0f, 0.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-0.75f, -5.041f, -9.2918f, -0.0436f, 0.0f, 0.0f)
            )
            val leftsideleaf = bulb.addOrReplaceChild(
                "leftsideleaf",
                CubeListBuilder.create(),
                PartPose.offset(3.5f, -0.2538f, 0.0755f)
            )
            val leftsideleaf_r1 = leftsideleaf.addOrReplaceChild(
                "leftsideleaf_r1",
                CubeListBuilder.create().texOffs(36, 0)
                    .addBox(3.5f, 4.5f, -3.75f, 6.0f, 0.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-3.5f, -4.463f, 0.9456f, -0.0436f, 0.0f, 0.0f)
            )
            val leftsideleaftip = leftsideleaf.addOrReplaceChild(
                "leftsideleaftip",
                CubeListBuilder.create(),
                PartPose.offset(6.0f, 0.0f, 0.0f)
            )
            val leftsideleaftip_r1 = leftsideleaftip.addOrReplaceChild(
                "leftsideleaftip_r1",
                CubeListBuilder.create().texOffs(32, 18)
                    .addBox(9.5f, 4.5f, -3.75f, 6.0f, 0.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-9.5f, -4.463f, 0.9456f, -0.0436f, 0.0f, 0.0f)
            )
            val rightsideleaf = bulb.addOrReplaceChild(
                "rightsideleaf",
                CubeListBuilder.create(),
                PartPose.offset(-3.5f, -0.2538f, 0.0755f)
            )
            val rightsideleaf_r1 = rightsideleaf.addOrReplaceChild(
                "rightsideleaf_r1",
                CubeListBuilder.create().texOffs(36, 6)
                    .addBox(-9.5f, 4.5f, -3.75f, 6.0f, 0.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.5f, -4.463f, 0.9456f, -0.0436f, 0.0f, 0.0f)
            )
            val rightsideleaftip = rightsideleaf.addOrReplaceChild(
                "rightsideleaftip",
                CubeListBuilder.create(),
                PartPose.offset(-6.0f, 0.0f, 0.0f)
            )
            val rightsideleaftip_r1 = rightsideleaftip.addOrReplaceChild(
                "rightsideleaftip_r1",
                CubeListBuilder.create().texOffs(36, 12)
                    .addBox(-15.5f, 4.5f, -3.75f, 6.0f, 0.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(9.5f, -4.463f, 0.9456f, -0.0436f, 0.0f, 0.0f)
            )
            val rightleg = body.addOrReplaceChild(
                "rightleg",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-1.5f, -1.0f, -1.5f, 3.0f, 6.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offset(-3.5f, 0.5528f, -4.2424f)
            )
            val leftleg = body.addOrReplaceChild(
                "leftleg",
                CubeListBuilder.create().texOffs(0, 37)
                    .addBox(-1.5f, -1.0f, -1.5f, 3.0f, 6.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offset(3.5f, 0.5528f, -4.2424f)
            )
            val rightbackleg = body.addOrReplaceChild(
                "rightbackleg",
                CubeListBuilder.create().texOffs(27, 39)
                    .addBox(-1.5f, -1.0f, -1.5f, 3.0f, 6.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offset(-3.5f, 0.5528f, 4.2576f)
            )
            val leftbackleg = body.addOrReplaceChild(
                "leftbackleg",
                CubeListBuilder.create().texOffs(15, 36)
                    .addBox(-1.5f, -1.0f, -1.5f, 3.0f, 6.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offset(3.5f, 0.5528f, 4.2576f)
            )
            return LayerDefinition.create(meshdefinition, 64, 64)
        }
    }
}