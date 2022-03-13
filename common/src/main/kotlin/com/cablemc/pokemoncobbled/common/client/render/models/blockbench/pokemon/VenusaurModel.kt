package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.QuadrupedWalkAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.SingleBoneLookAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.HeadedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.QuadrupedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.PoseType
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.world.phys.Vec3


class VenusaurModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame, QuadrupedFrame {

    override val rootPart = registerRelevantPart("venusaur", root.getChild("venusaur"))
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

    override val portraitScale = 1.75F
    override val portraitTranslation = Vec3(-0.75, -0.2, 0.0)

    companion object {
        val LAYER_LOCATION = ModelLayerLocation(cobbledResource("venusaur"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root
            val venusaur = partdefinition.addOrReplaceChild(
                "venusaur",
                CubeListBuilder.create(),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )
            val body = venusaur.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(39, 35)
                    .addBox(-2.5f, -12.2502f, -2.5109f, 5.0f, 9.0f, 5.0f, CubeDeformation(0.0f))
                    .texOffs(106, 0).addBox(-2.5f, -10.2502f, -2.5109f, 5.0f, 0.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -6.7498f, 0.5109f)
            )
            val body_r1 = body.addOrReplaceChild(
                "body_r1",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-7.5f, -3.75f, -8.5f, 15.0f, 7.0f, 17.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.2498f, -0.0109f, -0.0436f, 0.0f, 0.0f)
            )
            val backleaf = body.addOrReplaceChild(
                "backleaf",
                CubeListBuilder.create().texOffs(54, 24)
                    .addBox(-3.5f, 0.0f, 0.0f, 7.0f, 0.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -4.0002f, 2.4891f)
            )
            val backleaftip = backleaf.addOrReplaceChild(
                "backleaftip",
                CubeListBuilder.create().texOffs(0, 54)
                    .addBox(-3.5f, 0.0f, 0.0f, 7.0f, 0.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 6.0f)
            )
            val leftleaf = body.addOrReplaceChild(
                "leftleaf",
                CubeListBuilder.create().texOffs(48, 49)
                    .addBox(-3.5f, 0.0f, 0.0f, 7.0f, 0.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(2.5f, -4.2502f, -0.0109f, 0.0f, 1.5708f, 0.0f)
            )
            val leftleaftip = leftleaf.addOrReplaceChild(
                "leftleaftip",
                CubeListBuilder.create().texOffs(34, 49)
                    .addBox(-3.5f, 0.0f, 0.0f, 7.0f, 0.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 6.0f)
            )
            val rightleaf = body.addOrReplaceChild(
                "rightleaf",
                CubeListBuilder.create().texOffs(41, 11)
                    .addBox(-3.5f, 0.0f, 0.0f, 7.0f, 0.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-2.5f, -4.2502f, -0.0109f, 0.0f, -1.5708f, 0.0f)
            )
            val rightleaftip = rightleaf.addOrReplaceChild(
                "rightleaftip",
                CubeListBuilder.create().texOffs(29, 24)
                    .addBox(-3.5f, 0.0f, 0.0f, 7.0f, 0.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 6.0f)
            )
            val frontleaf = body.addOrReplaceChild(
                "frontleaf",
                CubeListBuilder.create().texOffs(53, 41)
                    .addBox(-3.5f, 0.0f, 0.0f, 7.0f, 0.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -4.0002f, -2.5109f, -3.0107f, 0.0f, 3.1416f)
            )
            val frontleaftip = frontleaf.addOrReplaceChild(
                "frontleaftip",
                CubeListBuilder.create().texOffs(53, 35)
                    .addBox(-3.5f, 0.0f, 0.0f, 7.0f, 0.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 6.0f, -0.0873f, 0.0f, 0.0f)
            )
            val flowerpetal = body.addOrReplaceChild(
                "flowerpetal",
                CubeListBuilder.create().texOffs(59, 16)
                    .addBox(-8.0f, 0.0f, -2.5f, 8.0f, 0.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offset(-2.5f, -10.2502f, -0.0109f)
            )
            val flowerpetal6 = body.addOrReplaceChild(
                "flowerpetal6",
                CubeListBuilder.create().texOffs(35, 55)
                    .addBox(0.0f, 0.0f, -2.5f, 8.0f, 0.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offset(2.5f, -10.2502f, -0.0109f)
            )
            val flowerpetal2 = body.addOrReplaceChild(
                "flowerpetal2",
                CubeListBuilder.create().texOffs(15, 58)
                    .addBox(-8.0f, 0.0f, -2.5f, 8.0f, 0.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-2.25f, -10.0002f, 2.2391f, 0.0f, 0.6981f, 0.0f)
            )
            val flowerpetal5 = body.addOrReplaceChild(
                "flowerpetal5",
                CubeListBuilder.create().texOffs(51, 55)
                    .addBox(0.0f, 0.0f, -2.5f, 8.0f, 0.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(2.25f, -10.0002f, 2.2391f, 0.0f, -0.6981f, 0.0f)
            )
            val flowerpetal3 = body.addOrReplaceChild(
                "flowerpetal3",
                CubeListBuilder.create().texOffs(58, 0)
                    .addBox(-8.0f, 0.0f, -2.5f, 8.0f, 0.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-2.25f, -10.0002f, -2.2609f, 0.0f, -0.6981f, 0.0f)
            )
            val flowerpetal4 = body.addOrReplaceChild(
                "flowerpetal4",
                CubeListBuilder.create().texOffs(56, 11)
                    .addBox(0.0f, 0.0f, -2.5f, 8.0f, 0.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(2.25f, -10.0002f, -2.2609f, 0.0f, 0.6981f, 0.0f)
            )
            val head = body.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(0, 24)
                    .addBox(-6.25f, -4.5f, -8.5f, 13.0f, 7.0f, 9.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-0.25f, -0.0002f, -8.0109f, -0.0437f, 0.0436f, -0.0019f)
            )
            val head_r1 = head.addOrReplaceChild(
                "head_r1",
                CubeListBuilder.create().texOffs(2, 26).mirror()
                    .addBox(-1.5f, -1.5f, 0.0f, 3.0f, 3.0f, 0.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(-4.25f, -1.75f, -8.6f, 0.0f, 0.0f, 0.2182f)
            )
            val head_r2 = head.addOrReplaceChild(
                "head_r2",
                CubeListBuilder.create().texOffs(2, 26)
                    .addBox(-1.5f, -1.5f, 0.0f, 3.0f, 3.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(4.75f, -1.75f, -8.6f, 0.0f, 0.0f, -0.2182f)
            )
            val eyes = head.addOrReplaceChild("eyes", CubeListBuilder.create(), PartPose.offset(0.25f, -2.0f, -7.5f))
            val rightear = head.addOrReplaceChild(
                "rightear",
                CubeListBuilder.create().texOffs(0, 4)
                    .addBox(0.0f, -4.0f, -2.5f, 0.0f, 4.0f, 5.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(-5.5f, -4.5f, -3.0f, 0.0341f, -0.1264f, -0.264f)
            )
            val leftear = head.addOrReplaceChild(
                "leftear",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(0.0f, -4.0f, -2.5f, 0.0f, 4.0f, 5.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(6.0f, -4.5f, -3.0f, 0.0341f, 0.1264f, 0.264f)
            )
            val rightleg = body.addOrReplaceChild(
                "rightleg",
                CubeListBuilder.create().texOffs(0, 3)
                    .addBox(0.0f, 6.5f, -3.5f, 0.0f, 1.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(2, 3).addBox(-2.0f, 6.5f, -3.5f, 0.0f, 1.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(4, 0).addBox(2.0f, 6.5f, -3.5f, 0.0f, 1.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(20, 44).mirror().addBox(-2.5f, -1.5f, -2.5f, 5.0f, 9.0f, 5.0f, CubeDeformation(0.0f))
                    .mirror(false),
                PartPose.offsetAndRotation(-6.0f, -0.7502f, -5.0109f, 0.0f, 0.0436f, 0.0f)
            )
            val leftleg = body.addOrReplaceChild(
                "leftleg",
                CubeListBuilder.create().texOffs(20, 44)
                    .addBox(-2.5f, -1.5f, -2.5f, 5.0f, 9.0f, 5.0f, CubeDeformation(0.0f))
                    .texOffs(4, 3).addBox(2.0f, 6.5f, -3.5f, 0.0f, 1.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(4, 2).addBox(-2.0f, 6.5f, -3.5f, 0.0f, 1.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(4, 1).addBox(0.0f, 6.5f, -3.5f, 0.0f, 1.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(6.0f, -0.7502f, -5.0109f, 0.0f, -0.0436f, 0.0f)
            )
            val rightbackleg = body.addOrReplaceChild(
                "rightbackleg",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(2.0f, 5.5f, -3.0f, 0.0f, 1.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(31, 60).mirror().addBox(-2.5f, 3.5f, -2.0f, 5.0f, 3.0f, 5.0f, CubeDeformation(0.0f))
                    .mirror(false)
                    .texOffs(47, 0).mirror().addBox(-2.5f, -1.5f, -3.0f, 5.0f, 5.0f, 6.0f, CubeDeformation(0.0f))
                    .mirror(false)
                    .texOffs(0, 1).addBox(-2.0f, 5.5f, -3.0f, 0.0f, 1.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(0, 2).addBox(0.0f, 5.5f, -3.0f, 0.0f, 1.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-6.0f, 0.2498f, 5.2391f, 0.0f, 0.0436f, 0.0f)
            )
            val leftbackleg = body.addOrReplaceChild(
                "leftbackleg",
                CubeListBuilder.create().texOffs(31, 60)
                    .addBox(-2.5f, 3.5f, -2.0f, 5.0f, 3.0f, 5.0f, CubeDeformation(0.0f))
                    .texOffs(47, 0).addBox(-2.5f, -1.5f, -3.0f, 5.0f, 5.0f, 6.0f, CubeDeformation(0.0f))
                    .texOffs(2, 2).addBox(-2.0f, 5.5f, -3.0f, 0.0f, 1.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(2, 1).addBox(2.0f, 5.5f, -3.0f, 0.0f, 1.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(2, 0).addBox(0.0f, 5.5f, -3.0f, 0.0f, 1.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(6.0f, 0.2498f, 5.2391f, 0.0f, -0.0436f, 0.0f)
            )
            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}