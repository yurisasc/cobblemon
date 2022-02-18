package com.cablemc.pokemoncobbled.forge.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.forge.client.render.models.blockbench.animation.BimanualSwingAnimation
import com.cablemc.pokemoncobbled.forge.client.render.models.blockbench.animation.BipedWalkAnimation
import com.cablemc.pokemoncobbled.forge.client.render.models.blockbench.animation.CascadeAnimation
import com.cablemc.pokemoncobbled.forge.client.render.models.blockbench.animation.SingleBoneLookAnimation
import com.cablemc.pokemoncobbled.forge.client.render.models.blockbench.animation.gradualFunction
import com.cablemc.pokemoncobbled.forge.client.render.models.blockbench.animation.sineFunction
import com.cablemc.pokemoncobbled.forge.client.render.models.blockbench.frame.BimanualFrame
import com.cablemc.pokemoncobbled.forge.client.render.models.blockbench.frame.BipedFrame
import com.cablemc.pokemoncobbled.forge.client.render.models.blockbench.frame.HeadedFrame
import com.cablemc.pokemoncobbled.forge.client.render.models.blockbench.pose.PoseType
import com.cablemc.pokemoncobbled.forge.client.render.models.blockbench.withRotation
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.cablemc.pokemoncobbled.common.util.math.geometry.toRadians
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition


class CharmeleonModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame, BipedFrame, BimanualFrame {

    override val rootPart = registerRelevantPart("charmeleon", root.getChild("charmeleon"))
    val body = registerRelevantPart("body", rootPart.getChild("body"))
    override val head = registerRelevantPart("head", body.getChild("neck").getChild("head"))
    override val rightLeg = registerRelevantPart("rightleg", body.getChild("rightleg"))
    override val leftLeg = registerRelevantPart("leftleg", body.getChild("leftleg"))
    override val rightArm = registerRelevantPart("rightarm", body.getChild("rightarm"))
    override val leftArm = registerRelevantPart("leftarm", body.getChild("leftarm"))

    private val tail = registerRelevantPart("tail", body.getChild("tail"))
    private val tailTip = registerRelevantPart("tail2", tail.getChild("tail2"))
    private val tailFlame = registerRelevantPart("fire", tailTip.getChild("fire"))
    private val leftHand = registerRelevantPart("lefthand", leftArm.getChild("lefthand"))
    private val rightHand = registerRelevantPart("righthand", rightArm.getChild("righthand"))

    override fun registerPoses() {
        registerPose(
            poseType = PoseType.WALK,
            condition = { true },
            idleAnimations = arrayOf(
                BipedWalkAnimation(this),
                BimanualSwingAnimation(this),
                SingleBoneLookAnimation(this),
                CascadeAnimation(
                    frame = this,
                    rootFunction = sineFunction(
                        period = 0.09f
                    ),
                    amplitudeFunction = gradualFunction(
                        base = 0.1f,
                        step = 0.1f
                    ),
                    segments = arrayOf(
                        tail,
                        tailTip
                    )
                )
            ),
            transformedParts = arrayOf(
                leftArm.withRotation(2, 70f.toRadians()),
                leftHand.withRotation(2, 10f.toRadians()),
                rightArm.withRotation(2, (-70f).toRadians()),
                rightHand.withRotation(2, (-10f).toRadians()),
                tailTip.withRotation(0, 35f.toRadians()),
                tailFlame.withRotation(0, (-35f).toRadians())
            )
        )
    }

    companion object {
        val LAYER_LOCATION = ModelLayerLocation(cobbledResource("charmeleon"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root
            val charmeleon = partdefinition.addOrReplaceChild(
                "charmeleon",
                CubeListBuilder.create(),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )
            val body = charmeleon.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(22, 17)
                    .addBox(-4.5f, -6.5f, -3.0f, 9.0f, 13.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -10.5f, 0.0f)
            )
            val neck = body.addOrReplaceChild(
                "neck",
                CubeListBuilder.create().texOffs(0, 44)
                    .addBox(-2.0f, -4.0f, -2.0f, 4.0f, 4.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -6.5f, 0.0f)
            )
            val head = neck.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(42, 36)
                    .addBox(-2.5f, -4.0f, -8.0f, 5.0f, 4.0f, 4.0f, CubeDeformation(0.0f))
                    .texOffs(0, 0).addBox(-4.0f, -7.0f, -4.0f, 8.0f, 7.0f, 7.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -3.0f, 0.0f)
            )
            val head_r1 = head.addOrReplaceChild(
                "head_r1",
                CubeListBuilder.create().texOffs(39, 0)
                    .addBox(-1.5f, -1.0f, -3.0f, 3.0f, 2.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -7.25f, 4.25f, 0.4363f, 0.0f, 0.0f)
            )
            val eyes = head.addOrReplaceChild("eyes", CubeListBuilder.create(), PartPose.offset(0.0f, -4.0f, -2.25f))
            val eyes_r1 = eyes.addOrReplaceChild(
                "eyes_r1",
                CubeListBuilder.create().texOffs(58, 0).mirror()
                    .addBox(0.0f, -1.0f, -1.5f, 0.0f, 2.0f, 3.0f, CubeDeformation(0.0f)).mirror(false)
                    .texOffs(58, 0).addBox(-8.05f, -1.0f, -1.5f, 0.0f, 2.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(4.025f, 0.0f, 0.0f, 0.2618f, 0.0f, 0.0f)
            )

            val tail = body.addOrReplaceChild("tail",
                CubeListBuilder.create().texOffs(42, 52).addBox(-2.0F, -2.5F, -1.0F, 4.0F, 5.0F, 7.0F, CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 3.75F, 3.0F)
            )

            val tail2 = tail.addOrReplaceChild("tail2",
                CubeListBuilder.create().texOffs(8, 52).addBox(-1.5F, -2.0F, 0.0F, 3.0F, 4.0F, 8.0F, CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.5F, 6.0F)
            )

            val fire = tail2.addOrReplaceChild("fire",
                CubeListBuilder.create().texOffs(0, 16).addBox(0.0F, -10.0F, -3.5F, 0.0F, 10.0F, 7.0F, CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -2.0F, 7.0F)
            )

            val leftleg = body.addOrReplaceChild(
                "leftleg",
                CubeListBuilder.create().texOffs(18, 21)
                    .addBox(1.75f, 5.5f, -4.0f, 0.0f, 1.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(14, 21).addBox(0.0f, 5.5f, -4.0f, 0.0f, 1.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(0, 4).addBox(-1.75f, 5.5f, -4.0f, 0.0f, 1.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(10, 32).addBox(-2.0f, -1.5f, -2.0f, 4.0f, 8.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(3.25f, 4.0f, -0.25f)
            )
            val rightleg = body.addOrReplaceChild(
                "rightleg",
                CubeListBuilder.create().texOffs(23, 0)
                    .addBox(-1.75f, 5.5f, -4.0f, 0.0f, 1.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(14, 22).addBox(0.0f, 5.5f, -4.0f, 0.0f, 1.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(18, 22).addBox(1.75f, 5.5f, -4.0f, 0.0f, 1.0f, 2.0f, CubeDeformation(0.0f))
                    .texOffs(26, 36).addBox(-2.0f, -1.5f, -2.0f, 4.0f, 8.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(-3.25f, 4.0f, -0.25f)
            )
            val leftarm = body.addOrReplaceChild(
                "leftarm",
                CubeListBuilder.create().texOffs(46, 8)
                    .addBox(0.0f, -1.0f, -1.5f, 5.0f, 2.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offset(4.5f, -5.25f, 0.0f)
            )
            val lefthand = leftarm.addOrReplaceChild(
                "lefthand",
                CubeListBuilder.create().texOffs(28, 10)
                    .addBox(0.0f, -1.0f, -2.0f, 7.0f, 2.0f, 4.0f, CubeDeformation(0.0f))
                    .texOffs(0, 5).addBox(7.0f, 0.0f, 1.0f, 2.0f, 0.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(0, 4).addBox(7.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(0, 3).addBox(7.0f, -0.25f, -0.5f, 2.0f, 0.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(5.0f, 0.0f, 0.0f)
            )
            val rightarm = body.addOrReplaceChild(
                "rightarm",
                CubeListBuilder.create().texOffs(42, 44)
                    .addBox(-5.0f, -1.0f, -1.5f, 5.0f, 2.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offset(-4.5f, -5.25f, 0.0f)
            )
            val righthand = rightarm.addOrReplaceChild(
                "righthand",
                CubeListBuilder.create().texOffs(23, 0)
                    .addBox(-7.0f, -1.0f, -2.0f, 7.0f, 2.0f, 4.0f, CubeDeformation(0.0f))
                    .texOffs(0, 2).addBox(-9.0f, 0.0f, 1.0f, 2.0f, 0.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(0, 1).addBox(-9.0f, 0.0f, -2.0f, 2.0f, 0.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(0, 0).addBox(-9.0f, -0.25f, -0.5f, 2.0f, 0.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(-5.0f, 0.0f, 0.0f)
            )
            return LayerDefinition.create(meshdefinition, 64, 64)
        }
    }
}