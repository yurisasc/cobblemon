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


class BlastoiseModel(root: ModelPart) : EntityModel<PokemonEntity>() {
    private val blastoise: ModelPart
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
        blastoise.render(poseStack, buffer, packedLight, packedOverlay)
    }

    companion object {
        val LAYER_LOCATION = ModelLayerLocation(cobbledResource("blastoise"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root
            val blastoise = partdefinition.addOrReplaceChild(
                "blastoise",
                CubeListBuilder.create(),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )
            val body = blastoise.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-9.0f, -9.9128f, -6.7786f, 18.0f, 22.0f, 13.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -17.0872f, 0.2786f)
            )
            val body_r1 = body.addOrReplaceChild(
                "body_r1",
                CubeListBuilder.create().texOffs(22, 53)
                    .addBox(-1.4167f, -1.5f, -6.0f, 3.0f, 3.0f, 4.0f, CubeDeformation(0.0f))
                    .texOffs(0, 0).addBox(-0.9167f, -1.0f, -2.0f, 2.0f, 2.0f, 4.0f, CubeDeformation(0.0f))
                    .texOffs(49, 0).addBox(-3.6667f, -2.5f, 1.0f, 7.0f, 5.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(9.742f, -11.8479f, 3.5464f, -0.9599f, 0.0436f, 0.7418f)
            )
            val body_r2 = body.addOrReplaceChild(
                "body_r2",
                CubeListBuilder.create().texOffs(60, 57)
                    .addBox(-3.3333f, -2.5f, 1.0f, 7.0f, 5.0f, 6.0f, CubeDeformation(0.0f))
                    .texOffs(62, 19).addBox(-1.5833f, -1.5f, -6.0f, 3.0f, 3.0f, 4.0f, CubeDeformation(0.0f))
                    .texOffs(52, 57).addBox(-1.0833f, -1.0f, -2.0f, 2.0f, 2.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-9.742f, -11.8479f, 3.5464f, -0.9599f, -0.0436f, -0.7418f)
            )
            val tail = body.addOrReplaceChild(
                "tail",
                CubeListBuilder.create().texOffs(114, 11)
                    .addBox(0.0f, -4.0f, 0.0f, 0.0f, 6.0f, 7.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 10.0872f, 6.2214f)
            )
            val head = body.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(62, 11)
                    .addBox(-4.5f, -4.5f, -7.5f, 9.0f, 5.0f, 3.0f, CubeDeformation(0.0f))
                    .texOffs(0, 35).addBox(-5.0f, -8.5f, -4.5f, 10.0f, 9.0f, 9.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -8.4128f, -3.2786f)
            )
            val rightear = head.addOrReplaceChild(
                "rightear",
                CubeListBuilder.create().texOffs(30, 60)
                    .addBox(-2.0f, -3.0f, 0.0f, 4.0f, 3.0f, 0.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(-3.5f, -7.75f, 1.5f, 0.0f, 0.0f, -0.5236f)
            )
            val leftear = head.addOrReplaceChild(
                "leftear",
                CubeListBuilder.create().texOffs(0, 56)
                    .addBox(-2.0f, -3.0f, 0.0f, 4.0f, 3.0f, 0.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(3.5f, -7.75f, 1.5f, 0.0f, 0.0f, 0.5236f)
            )
            val leftarm = body.addOrReplaceChild(
                "leftarm",
                CubeListBuilder.create().texOffs(0, 10)
                    .addBox(12.0f, 0.0f, -1.5f, 3.0f, 0.0f, 3.0f, CubeDeformation(0.0f))
                    .texOffs(38, 35).addBox(-1.0f, -2.5f, -3.0f, 13.0f, 5.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offset(9.0f, -5.4128f, -1.7786f)
            )
            val leftarm_r1 = leftarm.addOrReplaceChild(
                "leftarm_r1",
                CubeListBuilder.create().texOffs(35, 39)
                    .addBox(-1.5f, 0.0f, -1.5f, 3.0f, 0.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(12.75f, 0.0f, -2.75f, 0.0f, 0.6109f, 0.0f)
            )
            val leftarm_r2 = leftarm.addOrReplaceChild(
                "leftarm_r2",
                CubeListBuilder.create().texOffs(27, 42)
                    .addBox(-1.5f, 0.0f, -0.5f, 3.0f, 0.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(12.75f, 0.0f, 2.75f, 0.0f, -0.6109f, 0.0f)
            )
            val rightarm = body.addOrReplaceChild(
                "rightarm",
                CubeListBuilder.create().texOffs(0, 35)
                    .addBox(-15.0f, 0.0f, -1.5f, 3.0f, 0.0f, 3.0f, CubeDeformation(0.0f))
                    .texOffs(38, 46).addBox(-12.0f, -2.5f, -3.0f, 13.0f, 5.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offset(-9.0f, -5.4128f, -1.7786f)
            )
            val rightarm_r1 = rightarm.addOrReplaceChild(
                "rightarm_r1",
                CubeListBuilder.create().texOffs(47, 0)
                    .addBox(-1.5f, 0.0f, -1.5f, 3.0f, 0.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-12.75f, 0.0f, -2.75f, 0.0f, -0.6109f, 0.0f)
            )
            val rightarm_r2 = rightarm.addOrReplaceChild(
                "rightarm_r2",
                CubeListBuilder.create().texOffs(36, 46)
                    .addBox(-1.5f, 0.0f, -0.5f, 3.0f, 0.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-12.75f, 0.0f, 2.75f, 0.0f, 0.6109f, 0.0f)
            )
            val leftleg = body.addOrReplaceChild(
                "leftleg",
                CubeListBuilder.create().texOffs(29, 29)
                    .addBox(0.0f, 4.5f, -10.0f, 0.0f, 4.0f, 6.0f, CubeDeformation(0.0f))
                    .texOffs(30, 57).addBox(-3.5f, -1.5f, -4.0f, 7.0f, 10.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(7.0f, 8.5872f, -2.2786f, 0.0f, -0.0873f, 0.0f)
            )
            val leftleg_r1 = leftleg.addOrReplaceChild(
                "leftleg_r1",
                CubeListBuilder.create().texOffs(0, 37)
                    .addBox(0.0f, -1.5f, -2.0f, 0.0f, 3.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.0f, 7.0f, -5.75f, 0.0f, -0.1745f, 0.0f)
            )
            val leftleg_r2 = leftleg.addOrReplaceChild(
                "leftleg_r2",
                CubeListBuilder.create().texOffs(0, 49)
                    .addBox(0.0f, -1.5f, -2.0f, 0.0f, 3.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-3.0f, 7.0f, -5.75f, 0.0f, 0.1745f, 0.0f)
            )
            val rightleg = body.addOrReplaceChild(
                "rightleg",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(0.0f, 4.5f, -10.0f, 0.0f, 4.0f, 6.0f, CubeDeformation(0.0f))
                    .texOffs(0, 53).addBox(-3.5f, -1.5f, -4.0f, 7.0f, 10.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-7.0f, 8.5872f, -2.2786f, 0.0f, 0.0873f, 0.0f)
            )
            val rightleg_r1 = rightleg.addOrReplaceChild(
                "rightleg_r1",
                CubeListBuilder.create().texOffs(0, 34)
                    .addBox(0.0f, -1.5f, -2.0f, 0.0f, 3.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-3.0f, 7.0f, -5.75f, 0.0f, 0.1745f, 0.0f)
            )
            val rightleg_r2 = rightleg.addOrReplaceChild(
                "rightleg_r2",
                CubeListBuilder.create().texOffs(29, 35)
                    .addBox(0.0f, -1.5f, -2.0f, 0.0f, 3.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(3.0f, 7.0f, -5.75f, 0.0f, -0.1745f, 0.0f)
            )
            return LayerDefinition.create(meshdefinition, 128, 128)
        }
    }

    init {
        blastoise = root.getChild("blastoise")
    }
}