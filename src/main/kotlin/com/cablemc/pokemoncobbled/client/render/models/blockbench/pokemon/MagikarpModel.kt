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


class MagikarpModel(root: ModelPart) : EntityModel<PokemonEntity>() {
    private val magikarp: ModelPart
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
        magikarp.render(poseStack, buffer, packedLight, packedOverlay)
    }

    companion object {
        // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
        val LAYER_LOCATION = ModelLayerLocation(ResourceLocation("modid", "magikarp"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root
            val magikarp = partdefinition.addOrReplaceChild(
                "magikarp",
                CubeListBuilder.create(),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )
            val body = magikarp.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(18, 13)
                    .addBox(0.0f, -11.8333f, -4.0f, 0.0f, 8.0f, 7.0f, CubeDeformation(0.0f))
                    .texOffs(0, 0).addBox(-2.0f, -3.8333f, -6.25f, 4.0f, 9.0f, 11.0f, CubeDeformation(0.0f))
                    .texOffs(0, 0).addBox(0.0f, 5.1667f, -1.25f, 0.0f, 4.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -9.1667f, -0.25f)
            )
            val eyes = body.addOrReplaceChild(
                "eyes",
                CubeListBuilder.create().texOffs(46, 2)
                    .addBox(2.075f, -1.5f, -1.5f, 0.0f, 3.0f, 3.0f, CubeDeformation(0.0f))
                    .texOffs(46, 2).mirror().addBox(-2.075f, -1.5f, -1.5f, 0.0f, 3.0f, 3.0f, CubeDeformation(0.0f))
                    .mirror(false),
                PartPose.offset(0.0f, -1.0833f, -3.75f)
            )
            val tail = body.addOrReplaceChild(
                "tail",
                CubeListBuilder.create().texOffs(0, 11)
                    .addBox(0.0f, -7.5f, 0.0f, 0.0f, 15.0f, 9.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.6667f, 4.75f)
            )
            val rightmustache = body.addOrReplaceChild(
                "rightmustache",
                CubeListBuilder.create().texOffs(19, 1)
                    .addBox(-5.0f, -0.5f, 0.0f, 6.0f, 1.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offset(-2.0f, 1.4167f, -5.25f)
            )
            val rightmustachetip = rightmustache.addOrReplaceChild(
                "rightmustachetip",
                CubeListBuilder.create().texOffs(19, 0)
                    .addBox(-6.0f, -0.5f, 0.0f, 6.0f, 1.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offset(-5.0f, 0.0f, 0.0f)
            )
            val leftmustache = body.addOrReplaceChild(
                "leftmustache",
                CubeListBuilder.create().texOffs(19, 3)
                    .addBox(-1.0f, -0.5f, 0.0f, 6.0f, 1.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offset(2.0f, 1.4167f, -5.25f)
            )
            val leftmustachetip = leftmustache.addOrReplaceChild(
                "leftmustachetip",
                CubeListBuilder.create().texOffs(19, 2)
                    .addBox(0.0f, -0.5f, 0.0f, 6.0f, 1.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offset(5.0f, 0.0f, 0.0f)
            )
            val rightlfipper = body.addOrReplaceChild(
                "rightlfipper",
                CubeListBuilder.create().texOffs(18, 21)
                    .addBox(0.0f, -2.5f, 0.0f, 0.0f, 5.0f, 7.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-2.0f, 1.9167f, -3.5f, 0.0f, -0.2618f, 0.0f)
            )
            val leftlfipper = body.addOrReplaceChild(
                "leftlfipper",
                CubeListBuilder.create().texOffs(18, 26)
                    .addBox(0.0f, -2.5f, 0.0f, 0.0f, 5.0f, 7.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(2.0f, 1.9167f, -3.5f, 0.0f, 0.2618f, 0.0f)
            )
            return LayerDefinition.create(meshdefinition, 64, 64)
        }
    }

    init {
        magikarp = root.getChild("magikarp")
    }
}