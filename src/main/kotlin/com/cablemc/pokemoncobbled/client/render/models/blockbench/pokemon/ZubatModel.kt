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


class ZubatModel(root: ModelPart) : EntityModel<PokemonEntity>() {
    private val zubat: ModelPart
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
        zubat.render(poseStack, buffer, packedLight, packedOverlay)
    }

    companion object {
        // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
        val LAYER_LOCATION = ModelLayerLocation(ResourceLocation("modid", "zubat"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root
            val zubat =
                partdefinition.addOrReplaceChild("zubat", CubeListBuilder.create(), PartPose.offset(0.0f, 24.0f, 0.0f))
            val body = zubat.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(0, 16)
                    .addBox(-2.5f, -8.0f, -1.5f, 5.0f, 7.0f, 3.0f, CubeDeformation(0.0f))
                    .texOffs(24, 29).addBox(-2.0f, -7.5f, -1.55f, 4.0f, 3.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -7.0f, 0.0f)
            )
            val cube_r1 = body.addOrReplaceChild(
                "cube_r1",
                CubeListBuilder.create().texOffs(22, 7)
                    .addBox(-0.5f, -3.5f, 0.0f, 1.0f, 7.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(1.75f, 2.25f, 0.0f, 0.0f, 0.0f, -0.2618f)
            )
            val cube_r2 = body.addOrReplaceChild(
                "cube_r2",
                CubeListBuilder.create().texOffs(22, 0)
                    .addBox(-0.5f, -3.5f, 0.0f, 1.0f, 7.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-1.75f, 2.25f, 0.0f, 0.0f, 0.0f, 0.2618f)
            )
            val rightear = body.addOrReplaceChild(
                "rightear",
                CubeListBuilder.create().texOffs(16, 20)
                    .addBox(-1.5f, -4.0f, 0.0f, 3.0f, 4.0f, 0.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(-1.5f, -7.25f, 0.0f, 0.0f, 0.0f, -0.6981f)
            )
            val leftear = body.addOrReplaceChild(
                "leftear",
                CubeListBuilder.create().texOffs(16, 16)
                    .addBox(-1.5f, -4.0f, 0.0f, 3.0f, 4.0f, 0.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(1.5f, -7.25f, 0.0f, 0.0f, 0.0f, 0.6981f)
            )
            val rightwing = body.addOrReplaceChild(
                "rightwing",
                CubeListBuilder.create().texOffs(0, 8)
                    .addBox(-11.0f, -6.0f, 0.0f, 11.0f, 8.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offset(-2.5f, -3.0f, 0.0f)
            )
            val leftwing = body.addOrReplaceChild(
                "leftwing",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(0.0f, -6.0f, 0.0f, 11.0f, 8.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offset(2.5f, -3.0f, 0.0f)
            )
            return LayerDefinition.create(meshdefinition, 32, 32)
        }
    }

    init {
        zubat = root.getChild("zubat")
    }
}