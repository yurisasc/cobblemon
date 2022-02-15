package com.cablemc.pokemoncobbled.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.client.render.models.blockbench.bedrock.animation.BedrockAnimationRepository
import com.cablemc.pokemoncobbled.client.render.models.blockbench.bedrock.animation.BedrockStatelessAnimation
import com.cablemc.pokemoncobbled.client.render.models.blockbench.getChildOf
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pose.PoseType
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.world.phys.Vec3


class MagikarpModel(root: ModelPart) : PokemonPoseableModel() {
    override val rootPart: ModelPart = registerRelevantPart("magikarp", root.getChild("magikarp"))
    val body: ModelPart = registerRelevantPart("body", rootPart.getChildOf("body"))
    val leftMustache: ModelPart = registerRelevantPart("leftmustache", rootPart.getChildOf("body", "leftmustache"))
    val leftMustacheTip: ModelPart = registerRelevantPart("leftmustachetip", rootPart.getChildOf("body", "leftmustache", "leftmustachetip"))
    val leftFlipper: ModelPart = registerRelevantPart("leftlfipper", rootPart.getChildOf("body", "leftlfipper"))
    val rightMustache: ModelPart = registerRelevantPart("rightmustache", rootPart.getChildOf("body", "rightmustache"))
    val rightMustacheTip: ModelPart = registerRelevantPart("rightmustachetip", rootPart.getChildOf("body", "rightmustache", "rightmustachetip"))
    val rightFlipper: ModelPart = registerRelevantPart("rightlfipper", rootPart.getChildOf("body", "rightlfipper"))
    val tail: ModelPart = registerRelevantPart("tail", rootPart.getChildOf("body", "tail"))

    override fun registerPoses() {
        registerPose(
            poseType = PoseType.WALK,
            { !it.isUnderWater },
            idleAnimations = arrayOf(BedrockStatelessAnimation(this, BedrockAnimationRepository.getAnimation("magikarp.animation.json", "animation.magikarp.flop"))),
            transformedParts = emptyArray()
        )

        registerPose(
            poseType = PoseType.SWIM,
            { it.isUnderWater },
            idleAnimations = arrayOf(BedrockStatelessAnimation(this, BedrockAnimationRepository.getAnimation("magikarp.animation.json","animation.magikarp.fly"))),
            transformedParts = emptyArray()
        )
    }


    override val portraitScale = 1.65F
    override val portraitTranslation = Vec3(-0.05, -0.1, 0.0)

    companion object {
        // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
        val LAYER_LOCATION = ModelLayerLocation(cobbledResource("magikarp"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root

            val magikarp = partdefinition.addOrReplaceChild("magikarp", CubeListBuilder.create(), PartPose.offset(0.0f, 24.0f, 0.0f))

            val body = magikarp.addOrReplaceChild("body", CubeListBuilder.create().texOffs(19, 21).addBox(0.0f, -11.8333f, -4.0f, 0.0f, 8.0f, 7.0f, CubeDeformation(0.02f))
                    .texOffs(0, 0).addBox(-2.0f, -3.8333f, -6.25f, 4.0f, 9.0f, 11.0f, CubeDeformation(0.0f))
                    .texOffs(0, 0).addBox(0.0f, 5.1667f, -1.25f, 0.0f, 4.0f, 5.0f, CubeDeformation(0.02f)), PartPose.offset(0.0f, -9.1667f, -0.25f))

            val eyes = body.addOrReplaceChild("eyes", CubeListBuilder.create().texOffs(49, 2).addBox(2.075f, -1.5f, -1.5f, 0.0f, 3.0f, 3.0f, CubeDeformation(0.02f))
                    .texOffs(49, 2).mirror().addBox(-2.075f, -1.5f, -1.5f, 0.0f, 3.0f, 3.0f, CubeDeformation(0.02f)).mirror(false), PartPose.offset(0.0f, -1.0833f, -3.75f))

            val tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 21).addBox(0.0f, -7.5f, 0.0f, 0.0f, 15.0f, 9.0f, CubeDeformation(0.02f)), PartPose.offset(0.0f, 0.6667f, 4.75f))

            val rightmustache = body.addOrReplaceChild("rightmustache", CubeListBuilder.create().texOffs(20, 2).addBox(-5.0f, -0.5f, 0.0f, 6.0f, 1.0f, 0.0f, CubeDeformation(0.02f)), PartPose.offset(-2.0f, 1.4167f, -5.25f))

            val rightmustachetip = rightmustache.addOrReplaceChild("rightmustachetip", CubeListBuilder.create().texOffs(20, 0).addBox(-6.0f, -0.5f, 0.0f, 6.0f, 1.0f, 0.0f, CubeDeformation(0.02f)), PartPose.offset(-5.0f, 0.0f, 0.0f))

            val leftmustache = body.addOrReplaceChild("leftmustache", CubeListBuilder.create().texOffs(20, 6).addBox(-1.0f, -0.5f, 0.0f, 6.0f, 1.0f, 0.0f, CubeDeformation(0.02f)), PartPose.offset(2.0f, 1.4167f, -5.25f))

            val leftmustachetip = leftmustache.addOrReplaceChild("leftmustachetip", CubeListBuilder.create().texOffs(20, 4).addBox(0.0f, -0.5f, 0.0f, 6.0f, 1.0f, 0.0f, CubeDeformation(0.02f)), PartPose.offset(5.0f, 0.0f, 0.0f))

            val rightlfipper = body.addOrReplaceChild("rightlfipper", CubeListBuilder.create().texOffs(27, 14).addBox(0.0f, -2.5f, 0.0f, 0.0f, 5.0f, 7.0f, CubeDeformation(0.02f)), PartPose.offsetAndRotation(-2.0f, 1.9167f, -3.5f, 0.0f, -0.2618f, 0.0f))

            val leftlfipper = body.addOrReplaceChild("leftlfipper", CubeListBuilder.create().texOffs(27, 14).addBox(0.0f, -2.5f, 0.0f, 0.0f, 5.0f, 7.0f, CubeDeformation(0.02f)), PartPose.offsetAndRotation(2.0f, 1.9167f, -3.5f, 0.0f, 0.2618f, 0.0f))

            return LayerDefinition.create(meshdefinition, 64, 64)
        }
    }
}