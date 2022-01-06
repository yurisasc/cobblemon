package com.cablemc.pokemoncobbled.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemoncobbled.common.util.cobbledResource
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


class CharmeleonModel(root: ModelPart) : EntityModel<PokemonEntity>() {
    private val charmeleon: ModelPart
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
        charmeleon.render(poseStack, buffer, packedLight, packedOverlay)
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

    init {
        charmeleon = root.getChild("charmeleon")
    }
}