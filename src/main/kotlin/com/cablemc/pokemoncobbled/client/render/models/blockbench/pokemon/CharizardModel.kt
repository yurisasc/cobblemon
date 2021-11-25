package com.cablemc.pokemoncobbled.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.resources.ResourceLocation


class CharizardModel(root: ModelPart) : EntityModel<PokemonEntity>() {
    private val charizard: ModelPart
    override fun setupAnim(
        entity: PokemonEntity,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
    }

    override fun renderToBuffer(
        poseStack: PoseStack,
        buffer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        red: Float,
        green: Float,
        blue: Float,
        alpha: Float
    ) {
        charizard.render(poseStack, buffer, packedLight, packedOverlay)
    }

    companion object {
        // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
        val LAYER_LOCATION = ModelLayerLocation(ResourceLocation("modid", "charizard"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root
            val charizard = partdefinition.addOrReplaceChild(
                "charizard",
                CubeListBuilder.create(),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )
            val body = charizard.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(46, 48)
                    .addBox(-5.5f, -7.5f, -4.5f, 11.0f, 7.0f, 9.0f, CubeDeformation(0.0f))
                    .texOffs(0, 48).addBox(-6.5f, -0.5f, -5.0f, 13.0f, 9.0f, 10.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -13.5f, -0.5f)
            )
            val neck = body.addOrReplaceChild(
                "neck",
                CubeListBuilder.create().texOffs(41, 81)
                    .addBox(-2.0f, -9.0f, -2.0f, 4.0f, 10.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -7.5f, 0.0f)
            )
            val head = neck.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(17, 76)
                    .addBox(-2.5f, -5.0f, -10.45f, 5.0f, 3.0f, 7.0f, CubeDeformation(0.0f))
                    .texOffs(66, 33).addBox(-3.0f, -6.0f, -3.45f, 6.0f, 6.0f, 7.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -8.0f, -0.05f)
            )
            val head_r1 = head.addOrReplaceChild(
                "head_r1",
                CubeListBuilder.create().texOffs(66, 33).mirror()
                    .addBox(0.0f, -1.0f, -1.5f, 0.0f, 2.0f, 3.0f, CubeDeformation(0.0f)).mirror(false)
                    .texOffs(66, 33).addBox(-6.1f, -1.0f, -1.5f, 0.0f, 2.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.05f, -4.25f, -1.45f, 0.0436f, 0.0f, 0.0f)
            )
            val head_r2 = head.addOrReplaceChild(
                "head_r2",
                CubeListBuilder.create().texOffs(31, 67)
                    .addBox(-0.5f, -1.0f, -3.0f, 1.0f, 2.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-2.25f, -5.0f, 6.3f, 0.0873f, -0.0873f, 0.0f)
            )
            val head_r3 = head.addOrReplaceChild(
                "head_r3",
                CubeListBuilder.create().texOffs(83, 58)
                    .addBox(-0.5f, -1.0f, -3.0f, 1.0f, 2.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(2.25f, -5.0f, 6.3f, 0.0873f, 0.0873f, 0.0f)
            )
            val jaw = head.addOrReplaceChild(
                "jaw",
                CubeListBuilder.create().texOffs(77, 46)
                    .addBox(-2.5f, -1.0f, -6.75f, 5.0f, 2.0f, 7.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -1.0f, -3.7f)
            )
            val tail = body.addOrReplaceChild(
                "tail",
                CubeListBuilder.create().texOffs(35, 64)
                    .addBox(-3.0f, -3.125f, 0.0f, 6.0f, 6.0f, 11.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 5.625f, 5.0f)
            )
            val tail2 = tail.addOrReplaceChild(
                "tail2",
                CubeListBuilder.create().texOffs(66, 0)
                    .addBox(-2.0f, -2.5f, 0.0f, 4.0f, 5.0f, 12.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.375f, 11.0f)
            )
            val tail3 = tail2.addOrReplaceChild(
                "tail3",
                CubeListBuilder.create().texOffs(69, 64)
                    .addBox(-1.0f, -2.0f, 0.0f, 2.0f, 4.0f, 10.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.5f, 12.0f)
            )
            val fire = tail3.addOrReplaceChild(
                "fire",
                CubeListBuilder.create().texOffs(69, 71)
                    .addBox(0.0f, -12.0f, -3.5f, 0.0f, 12.0f, 7.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -2.0f, 9.5f)
            )
            val leftarm = body.addOrReplaceChild(
                "leftarm",
                CubeListBuilder.create().texOffs(58, 64)
                    .addBox(0.0f, -1.0f, -1.5f, 7.0f, 2.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offset(5.5f, -6.5f, 0.0f)
            )
            val leftforearm = leftarm.addOrReplaceChild(
                "leftforearm",
                CubeListBuilder.create().texOffs(58, 69)
                    .addBox(0.0f, -1.0f, -1.5f, 6.0f, 2.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offset(7.0f, 0.0f, 0.0f)
            )
            val lefthand =
                leftforearm.addOrReplaceChild("lefthand", CubeListBuilder.create(), PartPose.offset(6.0f, 0.0f, 0.0f))
            val leftfinger4 = lefthand.addOrReplaceChild(
                "leftfinger4",
                CubeListBuilder.create().texOffs(35, 48)
                    .addBox(3.0f, 0.0f, -0.5f, 1.0f, 0.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(0, 56).addBox(0.0f, -0.5f, -0.5f, 3.0f, 1.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -0.5f, -1.0f)
            )
            val leftfinger5 = lefthand.addOrReplaceChild(
                "leftfinger5",
                CubeListBuilder.create().texOffs(7, 48)
                    .addBox(3.0f, 0.0f, -0.5f, 1.0f, 0.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(44, 55).addBox(0.0f, -0.5f, -0.5f, 3.0f, 1.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -0.5f, 1.0f)
            )
            val leftfinger6 = lefthand.addOrReplaceChild(
                "leftfinger6",
                CubeListBuilder.create().texOffs(5, 48)
                    .addBox(3.0f, 0.0f, -0.5f, 1.0f, 0.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(36, 55).addBox(0.0f, -0.5f, -0.5f, 3.0f, 1.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -0.5f, 0.0f)
            )
            val rightarm = body.addOrReplaceChild(
                "rightarm",
                CubeListBuilder.create().texOffs(17, 67)
                    .addBox(-7.0f, -1.0f, -1.5f, 7.0f, 2.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offset(-5.5f, -6.5f, 0.0f)
            )
            val rightforearm = rightarm.addOrReplaceChild(
                "rightforearm",
                CubeListBuilder.create().texOffs(83, 17)
                    .addBox(-6.0f, -1.0f, -1.5f, 6.0f, 2.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offset(-7.0f, 0.0f, 0.0f)
            )
            val righthand = rightforearm.addOrReplaceChild(
                "righthand",
                CubeListBuilder.create(),
                PartPose.offset(-6.0f, 0.0f, 0.0f)
            )
            val rightfinger = righthand.addOrReplaceChild(
                "rightfinger",
                CubeListBuilder.create().texOffs(7, 49)
                    .addBox(-4.0f, 0.0f, -0.5f, 1.0f, 0.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(66, 4).addBox(-3.0f, -0.5f, -0.5f, 3.0f, 1.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -0.5f, -1.0f)
            )
            val rightfinger2 = righthand.addOrReplaceChild(
                "rightfinger2",
                CubeListBuilder.create().texOffs(5, 49)
                    .addBox(-4.0f, 0.0f, -0.5f, 1.0f, 0.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(66, 2).addBox(-3.0f, -0.5f, -0.5f, 3.0f, 1.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -0.5f, 1.0f)
            )
            val rightfinger3 = righthand.addOrReplaceChild(
                "rightfinger3",
                CubeListBuilder.create().texOffs(37, 48)
                    .addBox(-4.0f, 0.0f, -0.5f, 1.0f, 0.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(66, 0).addBox(-3.0f, -0.5f, -0.5f, 3.0f, 1.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -0.5f, 0.0f)
            )
            val leftleg = body.addOrReplaceChild(
                "leftleg",
                CubeListBuilder.create().texOffs(66, 17)
                    .addBox(-2.5f, -1.9f, -3.6f, 5.0f, 9.0f, 7.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(5.25f, 4.4f, 0.1f, 0.0f, -0.0873f, 0.0f)
            )
            val leftfoot = leftleg.addOrReplaceChild(
                "leftfoot",
                CubeListBuilder.create().texOffs(0, 49)
                    .addBox(-1.75f, 0.0f, -5.5f, 0.0f, 2.0f, 3.0f, CubeDeformation(0.0f))
                    .texOffs(0, 47).addBox(0.0f, 0.0f, -5.5f, 0.0f, 2.0f, 3.0f, CubeDeformation(0.0f))
                    .texOffs(0, 45).addBox(1.75f, 0.0f, -5.5f, 0.0f, 2.0f, 3.0f, CubeDeformation(0.0f))
                    .texOffs(36, 48).addBox(-2.0f, 0.0f, -2.5f, 4.0f, 2.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 7.1f, -0.1f)
            )
            val rightleg = body.addOrReplaceChild(
                "rightleg",
                CubeListBuilder.create().texOffs(0, 67)
                    .addBox(-2.5f, -1.9f, -3.6f, 5.0f, 9.0f, 7.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-5.25f, 4.4f, 0.1f, 0.0f, 0.0873f, 0.0f)
            )
            val rightfoot = rightleg.addOrReplaceChild(
                "rightfoot",
                CubeListBuilder.create().texOffs(49, 47)
                    .addBox(1.75f, 0.0f, -5.5f, 0.0f, 2.0f, 3.0f, CubeDeformation(0.0f))
                    .texOffs(49, 45).addBox(0.0f, 0.0f, -5.5f, 0.0f, 2.0f, 3.0f, CubeDeformation(0.0f))
                    .texOffs(0, 51).addBox(-1.75f, 0.0f, -5.5f, 0.0f, 2.0f, 3.0f, CubeDeformation(0.0f))
                    .texOffs(0, 83).addBox(-2.0f, 0.0f, -2.5f, 4.0f, 2.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 7.1f, -0.1f)
            )
            val leftwing = body.addOrReplaceChild(
                "leftwing",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(0.0f, -18.0f, 0.0f, 33.0f, 24.0f, 0.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(0.5f, -4.5f, 4.5f, 0.0f, -0.0873f, 0.0f)
            )
            val rightwing = body.addOrReplaceChild(
                "rightwing",
                CubeListBuilder.create().texOffs(0, 24)
                    .addBox(-33.0f, -18.0f, 0.0f, 33.0f, 24.0f, 0.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(-0.5f, -4.5f, 4.5f, 0.0f, 0.0873f, 0.0f)
            )
            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }

    init {
        charizard = root.getChild("charizard")
    }
}