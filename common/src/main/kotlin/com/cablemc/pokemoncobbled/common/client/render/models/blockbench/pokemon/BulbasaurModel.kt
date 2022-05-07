package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.QuadrupedWalkAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.SingleBoneLookAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.HeadedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.QuadrupedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.PoseType
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.client.render.entity.model.EntityModelLayer
import net.minecraft.client.model.ModelPart
import net.minecraft.client.model.ModelTransform
import net.minecraft.client.model.Dilation
import net.minecraft.client.model.ModelPartBuilder
import net.minecraft.client.model.TexturedModelData
import net.minecraft.client.model.ModelData
import net.minecraft.util.math.Vec3d

class BulbasaurModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame, QuadrupedFrame {

    override val rootPart = registerRelevantPart("bulbasaur", root.getChild("bulbasaur"))
    override val head = registerRelevantPart("head", rootPart.getChild("body").getChild("head"))
    override val hindRightLeg = registerRelevantPart("rightbackleg", rootPart.getChild("body").getChild("rightbackleg"))
    override val hindLeftLeg = registerRelevantPart("leftbackleg", rootPart.getChild("body").getChild("leftbackleg"))
    override val foreRightLeg = registerRelevantPart("rightleg", rootPart.getChild("body").getChild("rightleg"))
    override val foreLeftLeg = registerRelevantPart("leftleg", rootPart.getChild("body").getChild("leftleg"))

    override val portraitScale = 1.65F
    override val portraitTranslation = Vec3d(0.0, -0.6, 0.0)
    override val profileScale = 1.0F
    override val profileTranslation = Vec3d(0.0, 0.0, 0.0)

    init {
        registerPoses()
    }

    override fun registerPoses() {
        registerPose(
            poseType = PoseType.WALK,
            condition = { true },
            idleAnimations = arrayOf(
                QuadrupedWalkAnimation(this),
                SingleBoneLookAnimation(this)
            ),
            transformedParts = arrayOf()
        )
    }

    companion object {
        val LAYER_LOCATION = EntityModelLayer(cobbledResource("bulbasaur"), "main")
        fun createBodyLayer(): TexturedModelData {
            val meshdefinition = ModelData()
            val partdefinition = meshdefinition.root
            val bulbasaur = partdefinition.addChild(
                "bulbasaur",
                ModelPartBuilder.create(),
                ModelTransform.pivot(0.0f, 24.0f, 0.0f)
            )
            val body =
                bulbasaur.addChild("body", ModelPartBuilder.create(), ModelTransform.pivot(0.0f, -4.4981f, 0.5436f))
            val body_r1 = body.addChild(
                "body_r1",
                ModelPartBuilder.create().uv(0, 0)
                    .cuboid(-4.0f, -3.0f, -5.5f, 8.0f, 5.0f, 11.0f, Dilation(0.0f)),
                ModelTransform.of(0.0f, 0.4981f, -0.0436f, -0.0873f, 0.0f, 0.0f)
            )
            val bulb =
                body.addChild("bulb", ModelPartBuilder.create(), ModelTransform.pivot(0.0f, -1.0717f, 2.6764f))
            val bulb_r1 = bulb.addChild(
                "bulb_r1",
                ModelPartBuilder.create().uv(25, 16)
                    .cuboid(-2.5f, -4.5f, -3.0f, 5.0f, 2.0f, 5.0f, Dilation(0.0f))
                    .uv(0, 16).cuboid(-4.0f, -2.5f, -5.0f, 8.0f, 5.0f, 9.0f, Dilation(0.02f)),
                ModelTransform.of(0.0f, -1.4302f, 0.78f, -0.2618f, 0.0f, 0.0f)
            )
            val head = body.addChild(
                "head",
                ModelPartBuilder.create().uv(28, 24)
                    .cuboid(-4.5f, -6.5f, -3.5f, 9.0f, 7.0f, 6.0f, Dilation(0.0f))
                    .uv(50, 14).cuboid(-3.5f, -1.9f, -3.525f, 7.0f, 2.0f, 0.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, -1.2519f, -4.0436f)
            )
            val eyes = head.addChild(
                "eyes",
                ModelPartBuilder.create().uv(56, 0)
                    .cuboid(-0.9875f, -1.5f, -0.9875f, 2.0f, 3.0f, 2.0f, Dilation(0.02f))
                    .uv(56, 0).mirrored().cuboid(6.0125f, -1.5f, -0.9875f, 2.0f, 3.0f, 2.0f, Dilation(0.02f))
                    .mirrored(false),
                ModelTransform.pivot(-3.5125f, -2.75f, -2.5125f)
            )
            val pupils = eyes.addChild(
                "pupils",
                ModelPartBuilder.create().uv(56, 5).mirrored()
                    .cuboid(2.525f, -1.5f, -1.0f, 2.0f, 3.0f, 2.0f, Dilation(0.02f)).mirrored(false)
                    .uv(56, 5).cuboid(-4.525f, -1.5f, -1.0f, 2.0f, 3.0f, 2.0f, Dilation(0.02f)),
                ModelTransform.pivot(3.5125f, 0.0f, -0.0125f)
            )
            val leftear = head.addChild(
                "leftear",
                ModelPartBuilder.create().uv(0, 0)
                    .cuboid(-3.0f, -3.0f, 0.0f, 4.0f, 3.0f, 0.0f, Dilation(0.0f)),
                ModelTransform.of(3.75f, -5.25f, -0.5f, 0.0f, 0.0f, 0.3491f)
            )
            val rightear = head.addChild(
                "rightear",
                ModelPartBuilder.create().uv(0, 3)
                    .cuboid(-1.0f, -3.0f, 0.0f, 4.0f, 3.0f, 0.0f, Dilation(0.0f)),
                ModelTransform.of(-3.75f, -5.25f, -0.5f, 0.0f, 0.0f, -0.3491f)
            )
            val leftleg = body.addChild(
                "leftleg",
                ModelPartBuilder.create().uv(27, 0)
                    .cuboid(-1.5f, -1.0f, -1.5f, 3.0f, 5.0f, 3.0f, Dilation(0.0f)),
                ModelTransform.pivot(2.75f, 0.4981f, -3.5436f)
            )
            val rightleg = body.addChild(
                "rightleg",
                ModelPartBuilder.create().uv(0, 30)
                    .cuboid(-1.5f, -1.0f, -1.5f, 3.0f, 5.0f, 3.0f, Dilation(0.0f)),
                ModelTransform.pivot(-2.75f, 0.4981f, -3.5436f)
            )
            val leftbackleg = body.addChild(
                "leftbackleg",
                ModelPartBuilder.create().uv(12, 30)
                    .cuboid(-1.5f, -1.0f, -1.5f, 3.0f, 4.0f, 3.0f, Dilation(0.0f)),
                ModelTransform.pivot(2.75f, 1.4981f, 3.4564f)
            )
            val rightbackleg = body.addChild(
                "rightbackleg",
                ModelPartBuilder.create().uv(9, 37)
                    .cuboid(-1.5f, -1.0f, -1.5f, 3.0f, 4.0f, 3.0f, Dilation(0.0f)),
                ModelTransform.pivot(-2.75f, 1.4981f, 3.4564f)
            )
            return TexturedModelData.of(meshdefinition, 64, 64)
        }
    }
}