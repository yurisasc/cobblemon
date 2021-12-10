package com.cablemc.pokemoncobbled.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.client.render.models.blockbench.animation.StatelessAnimation
import com.cablemc.pokemoncobbled.client.render.models.blockbench.frame.ModelFrame
import com.cablemc.pokemoncobbled.client.render.models.blockbench.getChildOf
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pose.PoseType
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pose.TransformedModelPart.Companion.X_AXIS
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pose.TransformedModelPart.Companion.Y_AXIS
import com.cablemc.pokemoncobbled.client.render.models.blockbench.withRotation
import com.cablemc.pokemoncobbled.common.PokemonCobbled
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemoncobbled.common.util.math.geometry.toRadians
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.client.model.geom.builders.PartDefinition
import net.minecraft.resources.ResourceLocation

class GyaradosModel(root: ModelPart) : PokemonPoseableModel() {
    override val rootPart = registerRelevantPart(root.getChild("gyarados"))
    val spine = registerRelevantPart(rootPart.getChild("body").getChild("spine"))
    val neck = registerRelevantPart(rootPart.getChildOf("body", "spine", "spine2", "spine3", "spinefinal", "neck"))

    override fun registerPoses() {
        registerPose(
            poseType = PoseType.WALK,
            { !it.isUnderWater },
            idleAnimations = emptyArray<StatelessAnimation<PokemonEntity, out ModelFrame>>(),
            transformedParts = emptyArray()
        )

        registerPose(
            poseType = PoseType.SWIM,
            { it.isUnderWater },
            idleAnimations = emptyArray<StatelessAnimation<PokemonEntity, out ModelFrame>>(),
            transformedParts = arrayOf(
                spine.withRotation(X_AXIS, 0F),
                neck.withRotation(X_AXIS, (-77.5F).toRadians()).withPosition(Y_AXIS, 3F)
            )
        )
    }


    companion object {
        // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
        val LAYER_LOCATION: ModelLayerLocation = ModelLayerLocation(ResourceLocation(PokemonCobbled.MODID, "gyarados"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition: PartDefinition = meshdefinition.getRoot()
            val gyarados: PartDefinition = partdefinition.addOrReplaceChild("gyarados", CubeListBuilder.create(), PartPose.offset(0.0f, 24.0f, 0.0f))
            val body: PartDefinition = gyarados.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 15).addBox(-3.5f, -3.5f, -4.5f, 7.0f, 7.0f, 9.0f, CubeDeformation(0.0f)), PartPose.offset(0.0f, -3.5f, -2.5f))
            val spine: PartDefinition = body.addOrReplaceChild("spine", CubeListBuilder.create().texOffs(24, 23).addBox(-3.5f, -3.5f, -8.0f, 7.0f, 7.0f, 8.0f, CubeDeformation(0.02f)), PartPose.offsetAndRotation(0.0f, 0.0f, -4.5f, -1.309f, 0.0f, 0.0f))
            val spine2: PartDefinition = spine.addOrReplaceChild("spine2", CubeListBuilder.create().texOffs(28, 38).addBox(-3.5f, -3.5f, -7.0f, 7.0f, 7.0f, 7.0f, CubeDeformation(0.0f)), PartPose.offsetAndRotation(0.0f, 0.0f, -8.0f, -0.1745f, 0.0f, 0.0f))
            val spine3: PartDefinition = spine2.addOrReplaceChild("spine3", CubeListBuilder.create().texOffs(0, 47).addBox(-3.5f, -3.5f, -6.0f, 7.0f, 7.0f, 6.0f, CubeDeformation(0.02f)), PartPose.offsetAndRotation(0.0f, 0.0f, -7.0f, 0.1309f, 0.0f, 0.0f))
            val spinefinal: PartDefinition = spine3.addOrReplaceChild("spinefinal", CubeListBuilder.create().texOffs(26, 52).addBox(-3.0f, -3.5f, -6.0f, 6.0f, 7.0f, 6.0f, CubeDeformation(0.0f))
                .texOffs(0, 0).addBox(0.0f, -10.5f, -6.0f, 0.0f, 7.0f, 6.0f, CubeDeformation(0.0f)), PartPose.offsetAndRotation(0.0f, 0.0f, -6.0f, 0.1745f, 0.0f, 0.0f))
            val neck: PartDefinition = spinefinal.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(65, 35).addBox(-3.0f, -3.5f, -4.0f, 6.0f, 7.0f, 4.0f, CubeDeformation(0.02f)), PartPose.offsetAndRotation(0.0f, 0.0f, -6.0f, 0.0873f, 0.0f, 0.0f))
            val head: PartDefinition = neck.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0f, 0.25f, -2.0f))
            val cube_r1: PartDefinition = head.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5f, 0.25f, -3.0f, 3.0f, 0.0f, 5.0f, CubeDeformation(0.0f))
                .texOffs(32, 0).addBox(7.5f, 0.25f, -3.0f, 3.0f, 0.0f, 5.0f, CubeDeformation(0.0f)), PartPose.offsetAndRotation(-4.5f, 8.6356f, -3.4774f, -0.4363f, 0.0f, 0.0f))
            val cube_r2: PartDefinition = head.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(44, 28).addBox(-2.25f, 0.25f, -1.25f, 4.0f, 0.0f, 2.0f, CubeDeformation(0.0f))
                .texOffs(18, 47).addBox(10.25f, 0.25f, -1.25f, 4.0f, 0.0f, 2.0f, CubeDeformation(0.0f)), PartPose.offsetAndRotation(-6.0f, 6.1691f, -6.6184f, -1.1345f, 0.0f, 0.0f))
            val cube_r3: PartDefinition = head.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(50, 58).addBox(-2.5f, 4.5f, 1.5f, 6.0f, 6.0f, 6.0f, CubeDeformation(0.0f))
                .texOffs(51, 47).addBox(-3.5f, 4.5f, -3.5f, 8.0f, 6.0f, 5.0f, CubeDeformation(0.0f))
                .texOffs(0, 15).addBox(-6.0f, -4.5f, 1.5f, 3.0f, 9.0f, 0.0f, CubeDeformation(0.0f))
                .texOffs(60, 70).addBox(4.0f, -4.5f, 1.5f, 3.0f, 9.0f, 0.0f, CubeDeformation(0.0f))
                .texOffs(0, 31).addBox(-3.0f, -3.5f, -2.5f, 7.0f, 8.0f, 7.0f, CubeDeformation(0.0f))
                .texOffs(0, 7).addBox(-2.0f, 10.5f, 0.5f, 0.0f, 2.0f, 6.0f, CubeDeformation(0.02f))
                .texOffs(54, 29).addBox(3.0f, 10.5f, 0.5f, 0.0f, 2.0f, 6.0f, CubeDeformation(0.02f)), PartPose.offsetAndRotation(-0.5f, -0.5f, -4.5f, -0.3491f, 0.0f, 0.0f))
            val cube_r4: PartDefinition = head.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(15, 15).addBox(-5.5f, -1.0f, -4.0f, 11.0f, 0.0f, 8.0f, CubeDeformation(0.0f)), PartPose.offsetAndRotation(0.0f, 8.7674f, -12.6618f, -0.3927f, 0.0f, 0.0f))
            val cube_r5: PartDefinition = head.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(57, 1).addBox(-2.5f, 1.25f, 0.75f, 5.0f, 0.0f, 1.0f, CubeDeformation(0.0f))
                .texOffs(68, 58).addBox(-2.5f, -1.75f, -1.25f, 5.0f, 3.0f, 2.0f, CubeDeformation(0.0f)), PartPose.offsetAndRotation(0.0f, 10.9473f, -8.1344f, -0.5236f, 0.0f, 0.0f))
            val cube_r6: PartDefinition = head.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(57, 0).addBox(-2.5f, 0.75f, -1.0f, 5.0f, 0.0f, 1.0f, CubeDeformation(0.0f))
                .texOffs(0, 68).addBox(-2.5f, -5.25f, 0.0f, 5.0f, 6.0f, 2.0f, CubeDeformation(0.0f)), PartPose.offsetAndRotation(0.0f, 13.1508f, -2.8175f, -0.0436f, 0.0f, 0.0f))
            val cube_r7: PartDefinition = head.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(18, 57).addBox(0.0f, -3.75f, -3.75f, 0.0f, 7.0f, 8.0f, CubeDeformation(0.0f)), PartPose.offsetAndRotation(0.0f, -3.4918f, -9.7961f, -0.5236f, 0.0f, 0.0f))
            val rightwhisker: PartDefinition = head.addOrReplaceChild("rightwhisker", CubeListBuilder.create(), PartPose.offsetAndRotation(-3.0f, 10.6372f, -2.0507f, 0.0873f, 0.0f, 0.0f))
            val cube_r8: PartDefinition = rightwhisker.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(55, 46).addBox(-8.5f, 9.5f, 4.5f, 5.0f, 0.0f, 1.0f, CubeDeformation(0.0f)), PartPose.offsetAndRotation(3.5f, -10.6372f, -1.4493f, -0.3491f, 0.0f, 0.0f))
            val rightwhiskermid: PartDefinition = rightwhisker.addOrReplaceChild("rightwhiskermid", CubeListBuilder.create(), PartPose.offset(-5.0f, 0.0f, 0.0f))
            val cube_r9: PartDefinition = rightwhiskermid.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(53, 37).addBox(-13.5f, 9.5f, 4.5f, 5.0f, 0.0f, 1.0f, CubeDeformation(0.0f)), PartPose.offsetAndRotation(8.5f, -10.6372f, -1.4493f, -0.3491f, 0.0f, 0.0f))
            val rightwhiskertip: PartDefinition = rightwhiskermid.addOrReplaceChild("rightwhiskertip", CubeListBuilder.create(), PartPose.offset(-5.0f, 0.033f, 0.0603f))
            val cube_r10: PartDefinition = rightwhiskertip.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(42, 17).addBox(-17.5f, 9.5f, 2.5f, 4.0f, 0.0f, 3.0f, CubeDeformation(0.0f)), PartPose.offsetAndRotation(13.5f, -10.6702f, -1.5096f, -0.3491f, 0.0f, 0.0f))
            val leftwhisker: PartDefinition = head.addOrReplaceChild("leftwhisker", CubeListBuilder.create(), PartPose.offsetAndRotation(3.0f, 10.6372f, -2.0507f, 0.0873f, 0.0f, 0.0f))
            val cube_r11: PartDefinition = leftwhisker.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(19, 52).addBox(3.5f, 9.5f, 4.5f, 5.0f, 0.0f, 1.0f, CubeDeformation(0.0f)), PartPose.offsetAndRotation(-3.5f, -10.6372f, -1.4493f, -0.3491f, 0.0f, 0.0f))
            val leftwhiskermid: PartDefinition = leftwhisker.addOrReplaceChild("leftwhiskermid", CubeListBuilder.create(), PartPose.offset(5.0f, 0.0f, 0.0f))
            val cube_r12: PartDefinition = leftwhiskermid.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(0, 5).addBox(8.5f, 9.5f, 4.5f, 5.0f, 0.0f, 1.0f, CubeDeformation(0.0f)), PartPose.offsetAndRotation(-8.5f, -10.6372f, -1.4493f, -0.3491f, 0.0f, 0.0f))
            val leftwhiskertip: PartDefinition = leftwhiskermid.addOrReplaceChild("leftwhiskertip", CubeListBuilder.create(), PartPose.offset(5.0f, 0.033f, 0.0603f))
            val cube_r13: PartDefinition = leftwhiskertip.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(42, 14).addBox(13.5f, 9.5f, 2.5f, 4.0f, 0.0f, 3.0f, CubeDeformation(0.0f)), PartPose.offsetAndRotation(-13.5f, -10.6702f, -1.5096f, -0.3491f, 0.0f, 0.0f))
            val rightheadfinfront: PartDefinition = head.addOrReplaceChild("rightheadfinfront", CubeListBuilder.create(), PartPose.offsetAndRotation(-3.0f, 7.461f, -3.2999f, -0.0475f, 0.346f, 0.6898f))
            val cube_r14: PartDefinition = rightheadfinfront.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(42, 38).addBox(-11.5f, 6.75f, 0.5f, 8.0f, 0.0f, 7.0f, CubeDeformation(0.0f)), PartPose.offsetAndRotation(3.5f, -7.711f, -1.4501f, -0.3491f, 0.0f, 0.0f))
            val leftheadfinfront: PartDefinition = head.addOrReplaceChild("leftheadfinfront", CubeListBuilder.create(), PartPose.offsetAndRotation(3.0f, 7.461f, -3.2999f, -0.0475f, -0.346f, -0.6898f))
            val cube_r15: PartDefinition = leftheadfinfront.addOrReplaceChild("cube_r15", CubeListBuilder.create().texOffs(47, 28).addBox(3.5f, 6.75f, 0.5f, 8.0f, 0.0f, 7.0f, CubeDeformation(0.0f)), PartPose.offsetAndRotation(-3.5f, -7.711f, -1.4501f, -0.3491f, 0.0f, 0.0f))
            val tail: PartDefinition = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(37, 0).addBox(-3.5f, -3.5f, 0.0f, 7.0f, 7.0f, 7.0f, CubeDeformation(0.02f)), PartPose.offset(0.0f, 0.0f, 4.5f))
            val tail2: PartDefinition = tail.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(46, 14).addBox(-3.0f, -3.5f, 0.0f, 6.0f, 7.0f, 7.0f, CubeDeformation(0.0f))
                .texOffs(0, 51).addBox(0.0f, -11.5f, -1.0f, 0.0f, 8.0f, 9.0f, CubeDeformation(0.0f)), PartPose.offset(0.0f, 0.0f, 7.0f))
            val tail3: PartDefinition = tail2.addOrReplaceChild("tail3", CubeListBuilder.create().texOffs(65, 0).addBox(-2.5f, -3.0f, 0.0f, 5.0f, 6.0f, 6.0f, CubeDeformation(0.0f)), PartPose.offset(0.0f, 0.5f, 7.0f))
            val tail4: PartDefinition = tail3.addOrReplaceChild("tail4", CubeListBuilder.create().texOffs(65, 12).addBox(-2.0f, -2.5f, 0.0f, 4.0f, 5.0f, 4.0f, CubeDeformation(0.0f))
                .texOffs(50, 65).addBox(0.0f, -8.5f, -0.5f, 0.0f, 6.0f, 5.0f, CubeDeformation(0.0f)), PartPose.offset(0.0f, 0.5f, 6.0f))
            val tail5: PartDefinition = tail4.addOrReplaceChild("tail5", CubeListBuilder.create().texOffs(34, 65).addBox(-2.0f, -2.0f, 0.0f, 4.0f, 4.0f, 4.0f, CubeDeformation(0.0f)), PartPose.offset(0.0f, 0.5f, 4.0f))
            val tail6: PartDefinition = tail5.addOrReplaceChild("tail6", CubeListBuilder.create().texOffs(70, 24).addBox(-1.5f, -1.5f, 0.0f, 3.0f, 3.0f, 4.0f, CubeDeformation(0.0f)), PartPose.offset(0.0f, 0.5f, 4.0f))
            val tail7: PartDefinition = tail6.addOrReplaceChild("tail7", CubeListBuilder.create().texOffs(0, 0).addBox(-5.5f, 0.0f, 0.0f, 11.0f, 0.0f, 15.0f, CubeDeformation(0.0f)), PartPose.offset(0.0f, 0.0f, 4.0f))
            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }
}