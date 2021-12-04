package com.cablemc.pokemoncobbled.client.render.models.blockbench.pokeball

import com.cablemc.pokemoncobbled.common.entity.pokeball.PokeBallEntity
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

class PokeBallModel(root: ModelPart) : EntityModel<PokeBallEntity>() {

    val pokeball: ModelPart = root.getChild("pokeball")

    override fun setupAnim(
        entity: PokeBallEntity,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        // Throwing animation

        // Catching animation
        val frame = entity.shakeAnimation.currentFrame ?: -1
        entity.shakeAnimation.animate(this, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, frame + 1)
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
        pokeball.render(poseStack, buffer, packedLight, packedOverlay)
    }

    companion object {
        // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
        val LAYER_LOCATION = ModelLayerLocation(ResourceLocation("modid", "pokeball"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root
            val pokeball = partdefinition.addOrReplaceChild(
                "pokeball",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-4.0f, -4.0f, -4.0f, 8.0f, 4.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )
            val pokeball_lid = pokeball.addOrReplaceChild(
                "pokeball_lid",
                CubeListBuilder.create().texOffs(0, 12)
                    .addBox(-4.0f, -4.0f, -8.0f, 8.0f, 4.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -4.0f, 4.0f)
            )
            return LayerDefinition.create(meshdefinition, 32, 32)
        }
    }
}