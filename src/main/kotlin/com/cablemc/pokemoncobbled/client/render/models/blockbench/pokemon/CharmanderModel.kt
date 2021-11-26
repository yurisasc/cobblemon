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


class CharmanderModel(root: ModelPart) : EntityModel<PokemonEntity>() {
    private val charmander: ModelPart
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
        charmander.render(poseStack, buffer, packedLight, packedOverlay)
    }

    companion object {
        // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
        val LAYER_LOCATION = ModelLayerLocation(ResourceLocation("modid", "charmander"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root
            val charmander = partdefinition.addOrReplaceChild(
                "charmander",
                CubeListBuilder.create(),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )
            val body = charmander.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(23, 20)
                    .addBox(-4.0f, -5.5f, -2.5f, 8.0f, 11.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -8.5f, 0.0f)
            )
            val head = body.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-4.0f, -7.0f, -3.0f, 8.0f, 7.0f, 6.0f, CubeDeformation(0.0f))
                    .texOffs(18, 36).addBox(-3.5f, -3.0f, -5.0f, 7.0f, 3.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -5.5f, 0.0f)
            )
            val tail = body.addOrReplaceChild(
                "tail",
                CubeListBuilder.create().texOffs(0, 2)
                    .addBox(0.0f, -7.0f, 0.0f, 0.0f, 9.0f, 14.0f, CubeDeformation(0.0f))
                    .texOffs(54, 0).addBox(0.0f, -15.0f, 10.5f, 0.0f, 8.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 3.5f, 2.5f)
            )
            val leftarm = body.addOrReplaceChild(
                "leftarm",
                CubeListBuilder.create().texOffs(22, 0)
                    .addBox(-1.0f, -1.5f, -1.5f, 7.0f, 3.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offset(4.0f, -3.5f, 0.0f)
            )
            val rightarm = body.addOrReplaceChild(
                "rightarm",
                CubeListBuilder.create().texOffs(22, 0).mirror()
                    .addBox(-6.0f, -1.5f, -1.5f, 7.0f, 3.0f, 3.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offset(-4.0f, -3.5f, 0.0f)
            )
            val leftleg = body.addOrReplaceChild(
                "leftleg",
                CubeListBuilder.create().texOffs(28, 6)
                    .addBox(-1.5f, -1.0f, -2.0f, 3.0f, 6.0f, 4.0f, CubeDeformation(0.0f))
                    .texOffs(55, 0).addBox(-1.25f, 4.0f, -3.0f, 0.0f, 1.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(55, 0).addBox(0.0f, 4.0f, -3.0f, 0.0f, 1.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(55, 0).addBox(1.25f, 4.0f, -3.0f, 0.0f, 1.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(3.0f, 3.5f, 0.0f)
            )
            val rightleg = body.addOrReplaceChild(
                "rightleg",
                CubeListBuilder.create().texOffs(0, 25)
                    .addBox(-1.5f, -1.0f, -2.0f, 3.0f, 6.0f, 4.0f, CubeDeformation(0.0f))
                    .texOffs(55, 0).addBox(0.0f, 4.0f, -3.0f, 0.0f, 1.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(55, 0).addBox(1.25f, 4.0f, -3.0f, 0.0f, 1.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(55, 0).addBox(-1.25f, 4.0f, -3.0f, 0.0f, 1.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(-3.0f, 3.5f, 0.0f)
            )
            return LayerDefinition.create(meshdefinition, 64, 64)
        }
    }

    init {
        charmander = root.getChild("charmander")
    }
}