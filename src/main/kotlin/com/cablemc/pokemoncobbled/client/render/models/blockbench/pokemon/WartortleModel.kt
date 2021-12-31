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


class WartortleModel(root: ModelPart) : EntityModel<PokemonEntity>() {
    private val wartortle: ModelPart
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
        wartortle.render(poseStack, buffer, packedLight, packedOverlay)
    }

    companion object {
        val LAYER_LOCATION = ModelLayerLocation(cobbledResource("wartortle"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root
            val wartortle = partdefinition.addOrReplaceChild(
                "wartortle",
                CubeListBuilder.create(),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )
            val body = wartortle.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-5.0f, -6.0f, -4.0f, 10.0f, 12.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -11.0f, 0.0f)
            )
            val tail = body.addOrReplaceChild(
                "tail",
                CubeListBuilder.create().texOffs(0, 7)
                    .addBox(0.0f, -13.0f, 0.0f, 0.0f, 16.0f, 13.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 3.0f, 4.0f)
            )
            val leftarm = body.addOrReplaceChild(
                "leftarm",
                CubeListBuilder.create().texOffs(28, 0)
                    .addBox(-1.0f, -2.0f, -2.0f, 8.0f, 4.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(5.0f, -3.5f, -1.5f, 0.0057f, 0.0001f, -0.0001f)
            )
            val rightarm = body.addOrReplaceChild(
                "rightarm",
                CubeListBuilder.create().texOffs(22, 35)
                    .addBox(-7.0f, -2.0f, -2.0f, 8.0f, 4.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(-5.0f, -3.5f, -1.5f)
            )
            val leftleg = body.addOrReplaceChild(
                "leftleg",
                CubeListBuilder.create().texOffs(36, 8)
                    .addBox(-2.0f, -1.0f, -2.0f, 4.0f, 7.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(3.5f, 5.0f, -1.0f)
            )
            val rightleg = body.addOrReplaceChild(
                "rightleg",
                CubeListBuilder.create().texOffs(0, 36)
                    .addBox(-2.0f, -1.0f, -2.0f, 4.0f, 7.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(-3.5f, 5.0f, -1.0f)
            )
            val head = body.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(26, 20)
                    .addBox(-4.5f, -7.0f, -4.0f, 9.0f, 7.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -6.0f, -1.5f, 0.0003f, 0.0003f, -0.0076f)
            )
            val head_r1 = head.addOrReplaceChild(
                "head_r1",
                CubeListBuilder.create().texOffs(26, 20).mirror()
                    .addBox(-1.5f, -2.0f, 0.0f, 3.0f, 4.0f, 0.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offsetAndRotation(2.75f, -4.0f, -4.05f, 0.0f, 0.0f, -0.0873f)
            )
            val head_r2 = head.addOrReplaceChild(
                "head_r2",
                CubeListBuilder.create().texOffs(26, 20)
                    .addBox(-1.5f, -2.0f, 0.0f, 3.0f, 4.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-2.75f, -4.0f, -4.05f, 0.0f, 0.0f, 0.0873f)
            )
            val leftear = head.addOrReplaceChild(
                "leftear",
                CubeListBuilder.create().texOffs(16, 38)
                    .addBox(0.0493f, -8.9483f, -0.1887f, 0.0f, 9.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(4.475f, -4.925f, -1.975f, -0.2224f, 0.0756f, 0.0693f)
            )
            val rightear = head.addOrReplaceChild(
                "rightear",
                CubeListBuilder.create().texOffs(26, 38)
                    .addBox(-0.0493f, -8.9483f, -0.1887f, 0.0f, 9.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-4.475f, -4.925f, -1.975f, -0.2224f, -0.0756f, -0.0693f)
            )
            return LayerDefinition.create(meshdefinition, 64, 64)
        }
    }

    init {
        wartortle = root.getChild("wartortle")
    }
}