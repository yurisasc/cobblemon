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


class BulbasaurModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame, QuadrupedFrame {

    override val rootPart = registerRelevantPart("bulbasaur", root.getChild("bulbasaur"))
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
        val LAYER_LOCATION = ModelLayerLocation(cobbledResource("bulbasaur"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root
            val bulbasaur = partdefinition.addOrReplaceChild(
                "bulbasaur",
                CubeListBuilder.create(),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )
            val body =
                bulbasaur.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0f, -4.4981f, 0.5436f))
            val body_r1 = body.addOrReplaceChild(
                "body_r1",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-4.0f, -3.0f, -5.5f, 8.0f, 5.0f, 11.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.4981f, -0.0436f, -0.0873f, 0.0f, 0.0f)
            )
            val bulb =
                body.addOrReplaceChild("bulb", CubeListBuilder.create(), PartPose.offset(0.0f, -1.0717f, 2.6764f))
            val bulb_r1 = bulb.addOrReplaceChild(
                "bulb_r1",
                CubeListBuilder.create().texOffs(25, 16)
                    .addBox(-2.5f, -4.5f, -3.0f, 5.0f, 2.0f, 5.0f, CubeDeformation(0.0f))
                    .texOffs(0, 16).addBox(-4.0f, -2.5f, -5.0f, 8.0f, 5.0f, 9.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(0.0f, -1.4302f, 0.78f, -0.2618f, 0.0f, 0.0f)
            )
            val head = body.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(28, 24)
                    .addBox(-4.5f, -6.5f, -3.5f, 9.0f, 7.0f, 6.0f, CubeDeformation(0.0f))
                    .texOffs(50, 14).addBox(-3.5f, -1.9f, -3.525f, 7.0f, 2.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -1.2519f, -4.0436f)
            )
            val eyes = head.addOrReplaceChild(
                "eyes",
                CubeListBuilder.create().texOffs(56, 0)
                    .addBox(-0.9875f, -1.5f, -0.9875f, 2.0f, 3.0f, 2.0f, CubeDeformation(0.02f))
                    .texOffs(56, 0).mirror().addBox(6.0125f, -1.5f, -0.9875f, 2.0f, 3.0f, 2.0f, CubeDeformation(0.02f))
                    .mirror(false),
                PartPose.offset(-3.5125f, -2.75f, -2.5125f)
            )
            val pupils = eyes.addOrReplaceChild(
                "pupils",
                CubeListBuilder.create().texOffs(56, 5).mirror()
                    .addBox(2.525f, -1.5f, -1.0f, 2.0f, 3.0f, 2.0f, CubeDeformation(0.02f)).mirror(false)
                    .texOffs(56, 5).addBox(-4.525f, -1.5f, -1.0f, 2.0f, 3.0f, 2.0f, CubeDeformation(0.02f)),
                PartPose.offset(3.5125f, 0.0f, -0.0125f)
            )
            val leftear = head.addOrReplaceChild(
                "leftear",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-3.0f, -3.0f, 0.0f, 4.0f, 3.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.75f, -5.25f, -0.5f, 0.0f, 0.0f, 0.3491f)
            )
            val rightear = head.addOrReplaceChild(
                "rightear",
                CubeListBuilder.create().texOffs(0, 3)
                    .addBox(-1.0f, -3.0f, 0.0f, 4.0f, 3.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-3.75f, -5.25f, -0.5f, 0.0f, 0.0f, -0.3491f)
            )
            val leftleg = body.addOrReplaceChild(
                "leftleg",
                CubeListBuilder.create().texOffs(27, 0)
                    .addBox(-1.5f, -1.0f, -1.5f, 3.0f, 5.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offset(2.75f, 0.4981f, -3.5436f)
            )
            val rightleg = body.addOrReplaceChild(
                "rightleg",
                CubeListBuilder.create().texOffs(0, 30)
                    .addBox(-1.5f, -1.0f, -1.5f, 3.0f, 5.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offset(-2.75f, 0.4981f, -3.5436f)
            )
            val leftbackleg = body.addOrReplaceChild(
                "leftbackleg",
                CubeListBuilder.create().texOffs(12, 30)
                    .addBox(-1.5f, -1.0f, -1.5f, 3.0f, 4.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offset(2.75f, 1.4981f, 3.4564f)
            )
            val rightbackleg = body.addOrReplaceChild(
                "rightbackleg",
                CubeListBuilder.create().texOffs(9, 37)
                    .addBox(-1.5f, -1.0f, -1.5f, 3.0f, 4.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offset(-2.75f, 1.4981f, 3.4564f)
            )
            return LayerDefinition.create(meshdefinition, 64, 64)
        }
    }
}