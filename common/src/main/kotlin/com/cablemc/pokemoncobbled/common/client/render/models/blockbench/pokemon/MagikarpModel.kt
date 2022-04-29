package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.StatelessAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.bedrock.animation.BedrockAnimationRepository
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.bedrock.animation.BedrockStatelessAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.ModelFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.getChildOf
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.PoseType
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.TransformedModelPart.Companion.Y_AXIS
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.withRotation
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.cablemc.pokemoncobbled.common.util.math.geometry.toRadians
import net.minecraft.client.model.*
import net.minecraft.client.render.entity.model.EntityModelLayer
import net.minecraft.util.math.Vec3d

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

    override val portraitScale = 1.65F
    override val portraitTranslation = Vec3d(0.12, -0.45, 0.0)
    override val profileScale = 1F
    override val profileTranslation = Vec3d(0.0, 0.0, 0.0)

    override fun registerPoses() {
        registerPose(
            poseType = PoseType.WALK,
            { !it.isSubmergedInWater },
            idleAnimations = arrayOf(BedrockStatelessAnimation(this, BedrockAnimationRepository.getAnimation("magikarp.animation.json", "animation.magikarp.flop"))),
            transformedParts = emptyArray()
        )

        registerPose(
            poseType = PoseType.SWIM,
            { it.isSubmergedInWater},
            idleAnimations = arrayOf(BedrockStatelessAnimation(this, BedrockAnimationRepository.getAnimation("magikarp.animation.json","animation.magikarp.fly"))),
            transformedParts = emptyArray()
        )

        registerPose(
            poseType = PoseType.PROFILE,
            { false },
            idleAnimations = emptyArray<StatelessAnimation<PokemonEntity, out ModelFrame>>(),
            transformedParts = arrayOf(
                leftMustache.withRotation(Y_AXIS, (-75F).toRadians()),
                rightMustache.withRotation(Y_AXIS, 75F.toRadians())
            )
        )
    }

    companion object {
        // This layer location should be baked with EntityRendererFactory.Context in the entity renderer and passed into this model's constructor
        val LAYER_LOCATION = EntityModelLayer(cobbledResource("magikarp"), "main")
        fun createBodyLayer(): TexturedModelData {
            val meshdefinition = ModelData()
            val partdefinition = meshdefinition.root
            val magikarp = partdefinition.addChild("magikarp", ModelPartBuilder.create(), ModelTransform.pivot(0.0f, 24.0f, 0.0f))
            val body = magikarp.addChild("body", ModelPartBuilder.create().uv(19, 21).cuboid(0.0f, -11.8333f, -4.0f, 0.0f, 8.0f, 7.0f, Dilation(0.02f))
                    .uv(0, 0).cuboid(-2.0f, -3.8333f, -6.25f, 4.0f, 9.0f, 11.0f, Dilation(0.0f))
                    .uv(0, 0).cuboid(0.0f, 5.1667f, -1.25f, 0.0f, 4.0f, 5.0f, Dilation(0.02f)), ModelTransform.pivot(0.0f, -9.1667f, -0.25f))
            val eyes = body.addChild("eyes", ModelPartBuilder.create().uv(49, 2).cuboid(2.075f, -1.5f, -1.5f, 0.0f, 3.0f, 3.0f, Dilation(0.02f))
                    .uv(49, 2).mirrored().cuboid(-2.075f, -1.5f, -1.5f, 0.0f, 3.0f, 3.0f, Dilation(0.02f)).mirrored(false), ModelTransform.pivot(0.0f, -1.0833f, -3.75f))
            val tail = body.addChild("tail", ModelPartBuilder.create().uv(0, 21).cuboid(0.0f, -7.5f, 0.0f, 0.0f, 15.0f, 9.0f, Dilation(0.02f)), ModelTransform.pivot(0.0f, 0.6667f, 4.75f))
            val rightmustache = body.addChild("rightmustache", ModelPartBuilder.create().uv(20, 2).cuboid(-5.0f, -0.5f, 0.0f, 6.0f, 1.0f, 0.0f, Dilation(0.02f)), ModelTransform.pivot(-2.0f, 1.4167f, -5.25f))
            val rightmustachetip = rightmustache.addChild("rightmustachetip", ModelPartBuilder.create().uv(20, 0).cuboid(-6.0f, -0.5f, 0.0f, 6.0f, 1.0f, 0.0f, Dilation(0.02f)), ModelTransform.pivot(-5.0f, 0.0f, 0.0f))
            val leftmustache = body.addChild("leftmustache", ModelPartBuilder.create().uv(20, 6).cuboid(-1.0f, -0.5f, 0.0f, 6.0f, 1.0f, 0.0f, Dilation(0.02f)), ModelTransform.pivot(2.0f, 1.4167f, -5.25f))
            val leftmustachetip = leftmustache.addChild("leftmustachetip", ModelPartBuilder.create().uv(20, 4).cuboid(0.0f, -0.5f, 0.0f, 6.0f, 1.0f, 0.0f, Dilation(0.02f)), ModelTransform.pivot(5.0f, 0.0f, 0.0f))
            val rightlfipper = body.addChild("rightlfipper", ModelPartBuilder.create().uv(27, 14).cuboid(0.0f, -2.5f, 0.0f, 0.0f, 5.0f, 7.0f, Dilation(0.02f)), ModelTransform.of(-2.0f, 1.9167f, -3.5f, 0.0f, -0.2618f, 0.0f))
            val leftlfipper = body.addChild("leftlfipper", ModelPartBuilder.create().uv(27, 14).cuboid(0.0f, -2.5f, 0.0f, 0.0f, 5.0f, 7.0f, Dilation(0.02f)), ModelTransform.of(2.0f, 1.9167f, -3.5f, 0.0f, 0.2618f, 0.0f))
            return TexturedModelData.of(meshdefinition, 64, 64)
        }
    }
}