package com.cablemc.pokemoncobbled.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.client.entity.PokemonClientDelegate
import com.cablemc.pokemoncobbled.client.render.pokemon.PokemonRenderer.Companion.DELTA_TICKS
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
import net.minecraft.util.Mth

class EeveeModel(root: ModelPart) : EntityModel<PokemonEntity>() {
    private val eevee: ModelPart = root.getChild("eevee")
    private val head = eevee.getChild("body").getChild("head")
    private val rightHindLeg = eevee.getChild("body").getChild("rightbackleg")
    private val leftHindLeg = eevee.getChild("body").getChild("leftbackleg")
    private val rightFrontLeg = eevee.getChild("body").getChild("rightleg")
    private val leftFrontLeg = eevee.getChild("body").getChild("leftleg")
    private val tail = eevee.getChild("body").getChild("tail")

    override fun setupAnim(entity: PokemonEntity, limbSwing: Float, limbSwingAmount: Float, ageInTicks: Float, netHeadYaw: Float, headPitch: Float) {
        val clientDelegate = entity.delegate as PokemonClientDelegate
        if (entity.isMoving.currentValue) {
            clientDelegate.animTick += DELTA_TICKS * 4
            if (clientDelegate.animTick > TAIL_ANIMATION_TOTAL) {
                clientDelegate.animTick = 0F
            }
        } else {
            clientDelegate.animTick = 0F
        }

        head.xRot = headPitch * (Math.PI.toFloat() / 180f)
        head.yRot = netHeadYaw * (Math.PI.toFloat() / 180f)
        rightHindLeg.xRot = Mth.cos(limbSwing * 0.6662f) * 1.4f * limbSwingAmount
        leftHindLeg.xRot = Mth.cos(limbSwing * 0.6662f + Math.PI.toFloat()) * 1.4f * limbSwingAmount
        rightFrontLeg.xRot = Mth.cos(limbSwing * 0.6662f + Math.PI.toFloat()) * 1.4f * limbSwingAmount
        leftFrontLeg.xRot = Mth.cos(limbSwing * 0.6662f) * 1.4f * limbSwingAmount

        tail.yRot = Mth.sin(clientDelegate.animTick * 6 * Math.PI.toFloat() / 180) * Math.PI.toFloat() / 7
    }

    override fun renderToBuffer(poseStack: PoseStack, buffer: VertexConsumer, packedLight: Int, packedOverlay: Int, r: Float, g: Float, b: Float, a: Float) {
        eevee.render(poseStack, buffer, packedLight, packedOverlay)
    }

    companion object {
        private const val TAIL_ANIMATION_TOTAL = 60F
        val LAYER_LOCATION = ModelLayerLocation(ResourceLocation("pokemoncobbled", "eevee"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshDefinition = MeshDefinition()
            val partDefinition = meshDefinition.root
            val eevee = partDefinition.addOrReplaceChild("eevee", CubeListBuilder.create(), PartPose.offset(0.0f, 24.0f, 0.0f))
            val body = eevee.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(0, 0).addBox(-3.5f, -2.625f, -3.875f, 7.0f, 6.0f, 12.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -8.375f, -2.125f)
            )
            val body_r1 = body.addOrReplaceChild(
                "body_r1",
                CubeListBuilder.create().texOffs(26, 49).addBox(-5.5f, -3.0f, -4.5f, 10.0f, 6.0f, 9.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.5f, -0.375f, -2.125f, 0.5236f, 0.0f, 0.0f)
            )
            val tail = body.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offset(0.0f, -0.225f, 7.125f))
            val tail_r1 = tail.addOrReplaceChild(
                "tail_r1",
                CubeListBuilder.create().texOffs(0, 18).addBox(-4.5f, -4.0f, -5.0f, 8.0f, 7.0f, 10.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.5f, -2.4f, 3.0f, 0.7418f, 0.0f, 0.0f)
            )
            val leftbackleg = body.addOrReplaceChild(
                "leftbackleg",
                CubeListBuilder.create().texOffs(14, 37).addBox(-1.5f, -1.5f, -2.25f, 3.0f, 4.0f, 4.0f, CubeDeformation(0.0f)).texOffs(28, 41).addBox(-1.5f, 2.5f, -1.25f, 3.0f, 4.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offset(2.25f, 1.875f, 6.05f)
            )
            val rightbackleg = body.addOrReplaceChild(
                "rightbackleg",
                CubeListBuilder.create().texOffs(14, 37).mirror()
                    .addBox(-1.5f, -1.5f, -2.25f, 3.0f, 4.0f, 4.0f, CubeDeformation(0.0f)).mirror(false)
                    .texOffs(28, 41).mirror().addBox(-1.5f, 2.5f, -1.25f, 3.0f, 4.0f, 3.0f, CubeDeformation(0.0f))
                    .mirror(false),
                PartPose.offset(-2.25f, 1.875f, 6.05f)
            )
            val rightleg = body.addOrReplaceChild(
                "rightleg",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1.5f, -1.5f, -1.5f, 3.0f, 8.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offset(-2.25f, 1.875f, -2.625f)
            )
            val leftleg = body.addOrReplaceChild(
                "leftleg",
                CubeListBuilder.create().texOffs(26, 0).addBox(-1.5f, -1.5f, -1.5f, 3.0f, 8.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offset(2.25f, 1.875f, -2.625f)
            )
            val head = body.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(31, 11)
                    .addBox(-4.0f, -6.5f, -4.25f, 8.0f, 8.0f, 7.0f, CubeDeformation(0.0f))
                    .texOffs(62, 5).mirror().addBox(-0.5f, -1.5f, -4.275f, 1.0f, 1.0f, 0.0f, CubeDeformation(0.0f))
                    .mirror(false),
                PartPose.offset(0.0f, -2.125f, -2.875f)
            )
            val pog = head.addOrReplaceChild(
                "pog",
                CubeListBuilder.create().texOffs(1, 60).mirror().addBox(-1.0f, -1.0f, 0.0f, 2.0f, 2.0f, 0.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offset(0.0f, 0.25f, -3.525f)
            )
            val mouth = head.addOrReplaceChild(
                "mouth",
                CubeListBuilder.create().texOffs(60, 3).mirror().addBox(-1.0f, -0.5f, 0.0f, 2.0f, 1.0f, 0.0f, CubeDeformation(0.0f)).mirror(false),
                PartPose.offset(0.0f, 0.25f, -4.275f)
            )
            val eyes = head.addOrReplaceChild(
                "eyes",
                CubeListBuilder.create().texOffs(55, 0).mirror()
                    .addBox(1.5f, -1.75f, 0.0f, 2.0f, 3.0f, 0.0f, CubeDeformation(0.0f)).mirror(false)
                    .texOffs(55, 0).addBox(-3.5f, -1.75f, 0.0f, 2.0f, 3.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -2.75f, -4.3f)
            )
            val eyesback = head.addOrReplaceChild(
                "eyesback",
                CubeListBuilder.create().texOffs(54, 5).mirror()
                    .addBox(1.5f, -0.5f, 0.0f, 2.0f, 1.0f, 0.0f, CubeDeformation(0.0f)).mirror(false)
                    .texOffs(54, 5).addBox(-3.5f, -0.5f, 0.0f, 2.0f, 1.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -2.75f, -4.275f)
            )
            val rightear = head.addOrReplaceChild("rightear", CubeListBuilder.create(), PartPose.offset(-3.5f, -4.75f, -0.25f))
            val rightear_r1 = rightear.addOrReplaceChild(
                "rightear_r1",
                CubeListBuilder.create().texOffs(40, 41).mirror().addBox(-1.5f, -4.0f, 0.0f, 4.0f, 8.0f, 0.0f, CubeDeformation(0.02f)).mirror(false),
                PartPose.offsetAndRotation(-1.75f, -3.75f, 0.0f, 0.0f, 0.0f, -0.6109f)
            )
            val leftear = head.addOrReplaceChild("leftear", CubeListBuilder.create(), PartPose.offset(3.5f, -4.75f, -0.25f))
            val leftear_r1 = leftear.addOrReplaceChild(
                "leftear_r1",
                CubeListBuilder.create().texOffs(40, 41).addBox(-2.5f, -4.0f, 0.0f, 4.0f, 8.0f, 0.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(1.75f, -3.75f, 0.0f, 0.0f, 0.0f, 0.6109f)
            )
            return LayerDefinition.create(meshDefinition, 64, 64)
        }
    }
}