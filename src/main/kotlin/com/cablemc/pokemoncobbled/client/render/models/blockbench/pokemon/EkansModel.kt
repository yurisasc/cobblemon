package com.cablemc.pokemoncobbled.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.client.entity.PokemonClientDelegate
import com.cablemc.pokemoncobbled.client.render.models.blockbench.ModelPartChain
import com.cablemc.pokemoncobbled.client.render.pokemon.PokemonRenderer
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
import kotlin.math.PI
import kotlin.math.cos


class EkansModel(root: ModelPart) : EntityModel<PokemonEntity>() {
    private val ekans: ModelPart = root.getChild("ekans")
    private val head = ekans.getChild("body").getChild("head")
    private val tail = ekans.getChild("body").getChild("tail")
    private val tail2 = tail.getChild("tail2")
    private val tail3 = tail2.getChild("tail3")
    private val tail4 = tail3.getChild("tail4")
    private val tail5 = tail4.getChild("tail5")
    private val tail6 = tail5.getChild("tail6")
    private val tailChain = ModelPartChain(listOf(tail, tail2, tail3, tail4, tail5, tail6))

    override fun setupAnim(
        entity: PokemonEntity,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        val clientDelegate = entity.delegate as PokemonClientDelegate
        if (entity.isMoving.get()) {
            clientDelegate.animTick += 0.15f * PokemonRenderer.DELTA_TICKS
        }

        head.xRot = headPitch * (PI.toFloat() / 180f)
        head.yRot = netHeadYaw * (PI.toFloat() / 180f)

        // Tail sway
        tailChain.setupAnim(partHandler = { modelPart, placement ->
            // 0.1f base and then 0.1f increment to the multiplier per placement
            modelPart.yRot = cos(clientDelegate.animTick * 0.09f + (placement * 5)) * (0.3f + 0.15f * placement)
        })
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
        ekans.render(poseStack, buffer, packedLight, packedOverlay)
    }

    companion object {
        val LAYER_LOCATION = ModelLayerLocation(cobbledResource("ekans"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root
            val ekans =
                partdefinition.addOrReplaceChild("ekans", CubeListBuilder.create(), PartPose.offset(0.5f, 24.0f, 0.0f))
            val body = ekans.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-2.5f, -2.5f, -4.5f, 5.0f, 5.0f, 9.0f, CubeDeformation(0.0f)),
                PartPose.offset(-0.5f, -2.5f, -13.5f)
            )
            val head = body.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(36, 22)
                    .addBox(-3.0f, 1.9f, -7.0f, 6.0f, 1.0f, 4.0f, CubeDeformation(0.0f))
                    .texOffs(0, 3).addBox(-3.0f, -1.1f, -6.0f, 0.0f, 3.0f, 3.0f, CubeDeformation(0.02f))
                    .texOffs(36, 14).addBox(-3.0f, -4.1f, -8.0f, 6.0f, 3.0f, 5.0f, CubeDeformation(0.0f))
                    .texOffs(0, 46).addBox(-3.0f, -4.1f, -3.0f, 6.0f, 7.0f, 3.0f, CubeDeformation(0.0f))
                    .texOffs(0, 0).addBox(3.0f, -1.1f, -6.0f, 0.0f, 3.0f, 3.0f, CubeDeformation(0.02f)),
                PartPose.offset(0.0f, -0.4f, -4.5f)
            )
            val eyes = head.addOrReplaceChild("eyes", CubeListBuilder.create(), PartPose.offset(0.0f, -2.6f, -3.5f))
            val cube_r1 = eyes.addOrReplaceChild(
                "cube_r1",
                CubeListBuilder.create().texOffs(58, 32)
                    .addBox(0.0f, -1.0f, -1.5f, 0.0f, 2.0f, 3.0f, CubeDeformation(0.0f))
                    .texOffs(58, 32).mirror().addBox(6.1f, -1.0f, -1.5f, 0.0f, 2.0f, 3.0f, CubeDeformation(0.0f))
                    .mirror(false),
                PartPose.offsetAndRotation(-3.05f, 0.0f, 0.0f, 0.0873f, 0.0f, 0.0f)
            )
            val tail = body.addOrReplaceChild(
                "tail",
                CubeListBuilder.create().texOffs(28, 32)
                    .addBox(-2.5f, -2.5f, 0.0f, 5.0f, 5.0f, 9.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 4.5f)
            )
            val tail2 = tail.addOrReplaceChild(
                "tail2",
                CubeListBuilder.create().texOffs(0, 32)
                    .addBox(-2.5f, -2.5f, 0.0f, 5.0f, 5.0f, 9.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 9.0f)
            )
            val tail3 = tail2.addOrReplaceChild(
                "tail3",
                CubeListBuilder.create().texOffs(28, 0)
                    .addBox(-2.5f, -2.5f, 0.0f, 5.0f, 5.0f, 9.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 9.0f)
            )
            val tail4 = tail3.addOrReplaceChild(
                "tail4",
                CubeListBuilder.create().texOffs(0, 14)
                    .addBox(-2.5f, -2.5f, 0.0f, 5.0f, 5.0f, 9.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 9.0f)
            )
            val tail5 = tail4.addOrReplaceChild(
                "tail5",
                CubeListBuilder.create().texOffs(18, 18)
                    .addBox(-2.0f, -2.0f, 0.0f, 4.0f, 4.0f, 10.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.5f, 9.0f)
            )
            val tail6 = tail5.addOrReplaceChild(
                "tail6",
                CubeListBuilder.create().texOffs(19, 4)
                    .addBox(0.0f, -1.5f, 0.0f, 0.0f, 3.0f, 10.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 10.0f)
            )
            return LayerDefinition.create(meshdefinition, 64, 64)
        }
    }
}