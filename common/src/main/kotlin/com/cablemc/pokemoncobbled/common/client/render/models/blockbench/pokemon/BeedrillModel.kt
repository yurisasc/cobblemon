package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.SingleBoneLookAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.HeadedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.getChildOf
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.PoseType
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.*
import net.minecraft.world.phys.Vec3

class BeedrillModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame {
    override val rootPart = registerRelevantPart("beedrill", root.getChild("beedrill"))
    override val head = registerRelevantPart("head", rootPart.getChildOf("body", "head"))
    override val portraitScale = 1.95F
    override val portraitTranslation = Vec3(-0.05, -0.7, 0.0)

    override val profileScale = 1.0F
    override val profileTranslation = Vec3(0.0, 0.0, 0.0)

    init {
        registerPoses()
    }

    override fun registerPoses() {
        registerPose(
            poseType = PoseType.FLY,
            condition = { true },
            idleAnimations = arrayOf(
                SingleBoneLookAnimation(this)
            ),
            transformedParts = arrayOf()
        )
    }

    companion object {
        val LAYER_LOCATION = ModelLayerLocation(cobbledResource("beedrill"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root
            val beedrill =
                partdefinition.addOrReplaceChild("beedrill", CubeListBuilder.create(), PartPose.offset(0.0f, 21.0f, 0.0f))
            val body = beedrill.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(23, 5).addBox(-1.5f, -2.5f, -1.0f, 3.0f, 5.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(0, 22).addBox(-2.0f, -1.0f, -1.5f, 4.0f, 3.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -12.5f, -0.5f)
            )
            val head = body.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0f, -1.75f, -0.5f))
            val head_r1 = head.addOrReplaceChild(
                "head_r1",
                CubeListBuilder.create().texOffs(0, 36)
                    .addBox(-0.75f, -1.0f, -1.75f, 1.0f, 2.0f, 3.0f, CubeDeformation(0.0f))
                    .texOffs(19, 36).addBox(-5.75f, -1.0f, -1.75f, 1.0f, 2.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(2.75f, -2.0f, -0.5f, 0.48f, 0.0f, 0.0f)
            )
            val head_r2 = head.addOrReplaceChild(
                "head_r2",
                CubeListBuilder.create().texOffs(1, 1)
                    .addBox(-2.0f, -2.0f, -2.25f, 4.0f, 4.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -1.75f, -0.75f, 0.2618f, 0.0f, 0.0f)
            )
            val leftattena = head.addOrReplaceChild(
                "leftattena",
                CubeListBuilder.create().texOffs(27, 36)
                    .addBox(-0.75f, -4.0f, 0.0f, 1.0f, 4.0f, 0.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(1.5f, -3.75f, -0.5f, 0.133f, -0.2173f, -0.0193f)
            )
            val leftattenatip = leftattena.addOrReplaceChild(
                "leftattenatip",
                CubeListBuilder.create().texOffs(4, 0)
                    .addBox(-0.75f, -5.0f, 0.0f, 1.0f, 5.0f, 0.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(0.0f, -4.0f, 0.0f, 1.8326f, 0.0f, 0.0f)
            )
            val rightattena = head.addOrReplaceChild(
                "rightattena",
                CubeListBuilder.create().texOffs(29, 36)
                    .addBox(-0.25f, -4.0f, 0.0f, 1.0f, 4.0f, 0.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(-1.5f, -3.75f, -0.5f, 0.133f, 0.2173f, 0.0193f)
            )
            val rightattenatip = rightattena.addOrReplaceChild(
                "rightattenatip",
                CubeListBuilder.create().texOffs(0, 34)
                    .addBox(-0.25f, -5.0f, 0.0f, 1.0f, 5.0f, 0.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(0.0f, -4.0f, 0.0f, 1.8326f, 0.0f, 0.0f)
            )
            val bottombody = body.addOrReplaceChild(
                "bottombody",
                CubeListBuilder.create().texOffs(24, 12).addBox(-2.5f, 1.0f, -1.5f, 5.0f, 9.0f, 3.0f, CubeDeformation(0.0f))
                    .texOffs(0, 10).addBox(-3.5f, 2.0f, -2.5f, 7.0f, 7.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 1.5f, 0.0f)
            )
            val bottombody_r1 = bottombody.addOrReplaceChild(
                "bottombody_r1",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1.0f, 1.0f, 0.0f, 2.0f, 4.0f, 0.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(0.0f, 8.975f, -0.175f, -0.0873f, 0.0f, 0.0f)
            )
            val leftwingbottom = body.addOrReplaceChild(
                "leftwingbottom",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(1.0f, 0.0f, 1.5f, 0.0f, -0.2618f, 0.0f)
            )
            val rightwingbottom_r1 = leftwingbottom.addOrReplaceChild(
                "rightwingbottom_r1",
                CubeListBuilder.create().texOffs(41, 12).mirror()
                    .addBox(1.0f, -13.0f, 1.5f, 11.0f, 8.0f, 0.0f, CubeDeformation(0.02f)).mirror(false),
                PartPose.offsetAndRotation(-1.0f, 12.0f, -1.5f, 0.0f, -0.1309f, 0.0f)
            )
            val rightwingbottom = body.addOrReplaceChild(
                "rightwingbottom",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(-1.0f, 0.0f, 1.5f, 0.0f, 0.2618f, 0.0f)
            )
            val rightwingbottom_r2 = rightwingbottom.addOrReplaceChild(
                "rightwingbottom_r2",
                CubeListBuilder.create().texOffs(41, 12)
                    .addBox(-12.0f, -13.0f, 1.5f, 11.0f, 8.0f, 0.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(1.0f, 12.0f, -1.5f, 0.0f, 0.1309f, 0.0f)
            )
            val leftwing = body.addOrReplaceChild(
                "leftwing",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(1.0f, -1.0f, 1.5f, 0.0f, -0.1745f, 0.0f)
            )
            val rightwing_r1 = leftwing.addOrReplaceChild(
                "rightwing_r1",
                CubeListBuilder.create().texOffs(40, 0).mirror()
                    .addBox(1.0f, -24.0f, 1.5f, 12.0f, 12.0f, 0.0f, CubeDeformation(0.02f)).mirror(false),
                PartPose.offsetAndRotation(-1.0f, 15.0f, -1.5f, 0.0f, -0.1309f, 0.0f)
            )
            val rightwing = body.addOrReplaceChild(
                "rightwing",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(-1.0f, -1.0f, 1.5f, 0.0f, 0.1745f, 0.0f)
            )
            val rightwing_r2 = rightwing.addOrReplaceChild(
                "rightwing_r2",
                CubeListBuilder.create().texOffs(40, 0)
                    .addBox(-13.0f, -24.0f, 1.5f, 12.0f, 12.0f, 0.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(1.0f, 15.0f, -1.5f, 0.0f, 0.1309f, 0.0f)
            )
            val leftleg = body.addOrReplaceChild(
                "leftleg",
                CubeListBuilder.create().texOffs(10, 24)
                    .addBox(0.0f, 0.0f, -0.5f, 6.0f, 0.0f, 1.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(2.0f, 1.0f, -0.5f, 0.0f, 0.0f, 0.7418f)
            )
            val leftknee = leftleg.addOrReplaceChild(
                "leftknee",
                CubeListBuilder.create().texOffs(0, 34).mirror()
                    .addBox(0.0f, 0.0f, -1.0f, 6.0f, 0.0f, 2.0f, CubeDeformation(0.02f)).mirror(false),
                PartPose.offset(6.0f, 0.0f, 0.0f)
            )
            val leftfoot =
                leftknee.addOrReplaceChild("leftfoot", CubeListBuilder.create(), PartPose.offset(6.0f, 0.0f, 0.0f))
            val leftfoot_r1 = leftfoot.addOrReplaceChild(
                "leftfoot_r1",
                CubeListBuilder.create().texOffs(0, 8)
                    .addBox(14.0f, -14.5f, 4.0f, 0.0f, 5.0f, 2.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(-14.0f, 11.5f, 1.0f, 0.5236f, 0.0f, 0.0f)
            )
            val rightleg = body.addOrReplaceChild(
                "rightleg",
                CubeListBuilder.create().texOffs(13, 25)
                    .addBox(-6.0f, 0.0f, -0.5f, 6.0f, 0.0f, 1.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(-2.0f, 1.0f, -0.5f, 0.0f, 0.0f, -0.7418f)
            )
            val rightknee = rightleg.addOrReplaceChild(
                "rightknee",
                CubeListBuilder.create().texOffs(0, 34)
                    .addBox(-6.0f, 0.0f, -1.0f, 6.0f, 0.0f, 2.0f, CubeDeformation(0.02f)),
                PartPose.offset(-6.0f, 0.0f, 0.0f)
            )
            val rightfoot =
                rightknee.addOrReplaceChild("rightfoot", CubeListBuilder.create(), PartPose.offset(-6.0f, 0.0f, 0.0f))
            val rightfoot_r1 = rightfoot.addOrReplaceChild(
                "rightfoot_r1",
                CubeListBuilder.create().texOffs(8, 34)
                    .addBox(-14.0f, -14.5f, 4.0f, 0.0f, 5.0f, 2.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(14.0f, 11.5f, 1.0f, 0.5236f, 0.0f, 0.0f)
            )
            val leftarm = body.addOrReplaceChild(
                "leftarm",
                CubeListBuilder.create().texOffs(25, 30)
                    .addBox(0.0f, 0.0f, -0.5f, 4.0f, 0.0f, 1.0f, CubeDeformation(0.02f)),
                PartPose.offset(2.0f, -0.5f, -0.5f)
            )
            val leftelbow = leftarm.addOrReplaceChild(
                "leftelbow",
                CubeListBuilder.create().texOffs(13, 26).addBox(0.0f, 0.0f, -0.5f, 4.0f, 0.0f, 1.0f, CubeDeformation(0.02f))
                    .texOffs(0, 60).addBox(9.0f, -1.0f, -1.0f, 5.0f, 2.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(24, 62).addBox(14.0f, -0.5f, 0.0f, 3.0f, 1.0f, 0.0f, CubeDeformation(0.02f))
                    .texOffs(46, 58).addBox(3.0f, -1.5f, -1.5f, 6.0f, 3.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offset(4.0f, 0.0f, 0.0f)
            )
            val rightarm = body.addOrReplaceChild(
                "rightarm",
                CubeListBuilder.create().texOffs(13, 5)
                    .addBox(-4.0f, 0.0f, -0.5f, 4.0f, 0.0f, 1.0f, CubeDeformation(0.02f)),
                PartPose.offset(-2.0f, -0.5f, -0.5f)
            )
            val rightelbow = rightarm.addOrReplaceChild(
                "rightelbow",
                CubeListBuilder.create().texOffs(13, 4).addBox(-4.0f, 0.0f, -0.5f, 4.0f, 0.0f, 1.0f, CubeDeformation(0.02f))
                    .texOffs(24, 63).addBox(-17.0f, -0.5f, 0.0f, 3.0f, 1.0f, 0.0f, CubeDeformation(0.02f))
                    .texOffs(46, 58).mirror().addBox(-9.0f, -1.5f, -1.5f, 6.0f, 3.0f, 3.0f, CubeDeformation(0.0f))
                    .mirror(false)
                    .texOffs(0, 60).mirror().addBox(-14.0f, -1.0f, -1.0f, 5.0f, 2.0f, 2.0f, CubeDeformation(0.0f))
                    .mirror(false),
                PartPose.offset(-4.0f, 0.0f, 0.0f)
            )
            return LayerDefinition.create(meshdefinition, 64, 64)
        }
    }
}