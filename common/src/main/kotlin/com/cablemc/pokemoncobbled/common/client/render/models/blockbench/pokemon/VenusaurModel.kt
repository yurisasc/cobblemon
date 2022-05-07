package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.QuadrupedWalkAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.SingleBoneLookAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.HeadedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.QuadrupedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.PoseType
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.client.model.*
import net.minecraft.client.render.entity.model.EntityModelLayer
import net.minecraft.util.math.Vec3d

class VenusaurModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame, QuadrupedFrame {

    override val rootPart = registerRelevantPart("venusaur", root.getChild("venusaur"))
    override val head = registerRelevantPart("head", rootPart.getChild("body").getChild("head"))
    override val hindRightLeg = registerRelevantPart("rightbackleg", rootPart.getChild("body").getChild("rightbackleg"))
    override val hindLeftLeg = registerRelevantPart("leftbackleg", rootPart.getChild("body").getChild("leftbackleg"))
    override val foreRightLeg = registerRelevantPart("rightleg", rootPart.getChild("body").getChild("rightleg"))
    override val foreLeftLeg = registerRelevantPart("leftleg", rootPart.getChild("body").getChild("leftleg"))

    override val portraitScale = 1.75F
    override val portraitTranslation = Vec3d(-0.7, -0.9, 0.0)

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
        val LAYER_LOCATION = EntityModelLayer(cobbledResource("venusaur"), "main")
        fun createBodyLayer(): TexturedModelData {
            val meshdefinition = ModelData()
            val partdefinition = meshdefinition.root
            val venusaur = partdefinition.addChild(
                "venusaur",
                ModelPartBuilder.create(),
                ModelTransform.pivot(0.0f, 24.0f, 0.0f)
            )
            val body = venusaur.addChild(
                "body",
                ModelPartBuilder.create().uv(39, 35)
                    .cuboid(-2.5f, -12.2502f, -2.5109f, 5.0f, 9.0f, 5.0f, Dilation(0.0f))
                    .uv(106, 0).cuboid(-2.5f, -10.2502f, -2.5109f, 5.0f, 0.0f, 5.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, -6.7498f, 0.5109f)
            )
            val body_r1 = body.addChild(
                "body_r1",
                ModelPartBuilder.create().uv(0, 0)
                    .cuboid(-7.5f, -3.75f, -8.5f, 15.0f, 7.0f, 17.0f, Dilation(0.0f)),
                ModelTransform.of(0.0f, 0.2498f, -0.0109f, -0.0436f, 0.0f, 0.0f)
            )
            val backleaf = body.addChild(
                "backleaf",
                ModelPartBuilder.create().uv(54, 24)
                    .cuboid(-3.5f, 0.0f, 0.0f, 7.0f, 0.0f, 6.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, -4.0002f, 2.4891f)
            )
            val backleaftip = backleaf.addChild(
                "backleaftip",
                ModelPartBuilder.create().uv(0, 54)
                    .cuboid(-3.5f, 0.0f, 0.0f, 7.0f, 0.0f, 6.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 0.0f, 6.0f)
            )
            val leftleaf = body.addChild(
                "leftleaf",
                ModelPartBuilder.create().uv(48, 49)
                    .cuboid(-3.5f, 0.0f, 0.0f, 7.0f, 0.0f, 6.0f, Dilation(0.0f)),
                ModelTransform.of(2.5f, -4.2502f, -0.0109f, 0.0f, 1.5708f, 0.0f)
            )
            val leftleaftip = leftleaf.addChild(
                "leftleaftip",
                ModelPartBuilder.create().uv(34, 49)
                    .cuboid(-3.5f, 0.0f, 0.0f, 7.0f, 0.0f, 6.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 0.0f, 6.0f)
            )
            val rightleaf = body.addChild(
                "rightleaf",
                ModelPartBuilder.create().uv(41, 11)
                    .cuboid(-3.5f, 0.0f, 0.0f, 7.0f, 0.0f, 6.0f, Dilation(0.0f)),
                ModelTransform.of(-2.5f, -4.2502f, -0.0109f, 0.0f, -1.5708f, 0.0f)
            )
            val rightleaftip = rightleaf.addChild(
                "rightleaftip",
                ModelPartBuilder.create().uv(29, 24)
                    .cuboid(-3.5f, 0.0f, 0.0f, 7.0f, 0.0f, 6.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 0.0f, 6.0f)
            )
            val frontleaf = body.addChild(
                "frontleaf",
                ModelPartBuilder.create().uv(53, 41)
                    .cuboid(-3.5f, 0.0f, 0.0f, 7.0f, 0.0f, 6.0f, Dilation(0.0f)),
                ModelTransform.of(0.0f, -4.0002f, -2.5109f, -3.0107f, 0.0f, 3.1416f)
            )
            val frontleaftip = frontleaf.addChild(
                "frontleaftip",
                ModelPartBuilder.create().uv(53, 35)
                    .cuboid(-3.5f, 0.0f, 0.0f, 7.0f, 0.0f, 6.0f, Dilation(0.0f)),
                ModelTransform.of(0.0f, 0.0f, 6.0f, -0.0873f, 0.0f, 0.0f)
            )
            val flowerpetal = body.addChild(
                "flowerpetal",
                ModelPartBuilder.create().uv(59, 16)
                    .cuboid(-8.0f, 0.0f, -2.5f, 8.0f, 0.0f, 5.0f, Dilation(0.0f)),
                ModelTransform.pivot(-2.5f, -10.2502f, -0.0109f)
            )
            val flowerpetal6 = body.addChild(
                "flowerpetal6",
                ModelPartBuilder.create().uv(35, 55)
                    .cuboid(0.0f, 0.0f, -2.5f, 8.0f, 0.0f, 5.0f, Dilation(0.0f)),
                ModelTransform.pivot(2.5f, -10.2502f, -0.0109f)
            )
            val flowerpetal2 = body.addChild(
                "flowerpetal2",
                ModelPartBuilder.create().uv(15, 58)
                    .cuboid(-8.0f, 0.0f, -2.5f, 8.0f, 0.0f, 5.0f, Dilation(0.0f)),
                ModelTransform.of(-2.25f, -10.0002f, 2.2391f, 0.0f, 0.6981f, 0.0f)
            )
            val flowerpetal5 = body.addChild(
                "flowerpetal5",
                ModelPartBuilder.create().uv(51, 55)
                    .cuboid(0.0f, 0.0f, -2.5f, 8.0f, 0.0f, 5.0f, Dilation(0.0f)),
                ModelTransform.of(2.25f, -10.0002f, 2.2391f, 0.0f, -0.6981f, 0.0f)
            )
            val flowerpetal3 = body.addChild(
                "flowerpetal3",
                ModelPartBuilder.create().uv(58, 0)
                    .cuboid(-8.0f, 0.0f, -2.5f, 8.0f, 0.0f, 5.0f, Dilation(0.0f)),
                ModelTransform.of(-2.25f, -10.0002f, -2.2609f, 0.0f, -0.6981f, 0.0f)
            )
            val flowerpetal4 = body.addChild(
                "flowerpetal4",
                ModelPartBuilder.create().uv(56, 11)
                    .cuboid(0.0f, 0.0f, -2.5f, 8.0f, 0.0f, 5.0f, Dilation(0.0f)),
                ModelTransform.of(2.25f, -10.0002f, -2.2609f, 0.0f, 0.6981f, 0.0f)
            )
            val head = body.addChild(
                "head",
                ModelPartBuilder.create().uv(0, 24)
                    .cuboid(-6.25f, -4.5f, -8.5f, 13.0f, 7.0f, 9.0f, Dilation(0.0f)),
                ModelTransform.of(-0.25f, -0.0002f, -8.0109f, -0.0437f, 0.0436f, -0.0019f)
            )
            val head_r1 = head.addChild(
                "head_r1",
                ModelPartBuilder.create().uv(2, 26).mirrored()
                    .cuboid(-1.5f, -1.5f, 0.0f, 3.0f, 3.0f, 0.0f, Dilation(0.0f)).mirrored(false),
                ModelTransform.of(-4.25f, -1.75f, -8.6f, 0.0f, 0.0f, 0.2182f)
            )
            val head_r2 = head.addChild(
                "head_r2",
                ModelPartBuilder.create().uv(2, 26)
                    .cuboid(-1.5f, -1.5f, 0.0f, 3.0f, 3.0f, 0.0f, Dilation(0.0f)),
                ModelTransform.of(4.75f, -1.75f, -8.6f, 0.0f, 0.0f, -0.2182f)
            )
            val eyes = head.addChild("eyes", ModelPartBuilder.create(), ModelTransform.pivot(0.25f, -2.0f, -7.5f))
            val rightear = head.addChild(
                "rightear",
                ModelPartBuilder.create().uv(0, 4)
                    .cuboid(0.0f, -4.0f, -2.5f, 0.0f, 4.0f, 5.0f, Dilation(0.02f)),
                ModelTransform.of(-5.5f, -4.5f, -3.0f, 0.0341f, -0.1264f, -0.264f)
            )
            val leftear = head.addChild(
                "leftear",
                ModelPartBuilder.create().uv(0, 0)
                    .cuboid(0.0f, -4.0f, -2.5f, 0.0f, 4.0f, 5.0f, Dilation(0.02f)),
                ModelTransform.of(6.0f, -4.5f, -3.0f, 0.0341f, 0.1264f, 0.264f)
            )
            val rightleg = body.addChild(
                "rightleg",
                ModelPartBuilder.create().uv(0, 3)
                    .cuboid(0.0f, 6.5f, -3.5f, 0.0f, 1.0f, 1.0f, Dilation(0.0f))
                    .uv(2, 3).cuboid(-2.0f, 6.5f, -3.5f, 0.0f, 1.0f, 1.0f, Dilation(0.0f))
                    .uv(4, 0).cuboid(2.0f, 6.5f, -3.5f, 0.0f, 1.0f, 1.0f, Dilation(0.0f))
                    .uv(20, 44).mirrored().cuboid(-2.5f, -1.5f, -2.5f, 5.0f, 9.0f, 5.0f, Dilation(0.0f))
                    .mirrored(false),
                ModelTransform.of(-6.0f, -0.7502f, -5.0109f, 0.0f, 0.0436f, 0.0f)
            )
            val leftleg = body.addChild(
                "leftleg",
                ModelPartBuilder.create().uv(20, 44)
                    .cuboid(-2.5f, -1.5f, -2.5f, 5.0f, 9.0f, 5.0f, Dilation(0.0f))
                    .uv(4, 3).cuboid(2.0f, 6.5f, -3.5f, 0.0f, 1.0f, 1.0f, Dilation(0.0f))
                    .uv(4, 2).cuboid(-2.0f, 6.5f, -3.5f, 0.0f, 1.0f, 1.0f, Dilation(0.0f))
                    .uv(4, 1).cuboid(0.0f, 6.5f, -3.5f, 0.0f, 1.0f, 1.0f, Dilation(0.0f)),
                ModelTransform.of(6.0f, -0.7502f, -5.0109f, 0.0f, -0.0436f, 0.0f)
            )
            val rightbackleg = body.addChild(
                "rightbackleg",
                ModelPartBuilder.create().uv(0, 0)
                    .cuboid(2.0f, 5.5f, -3.0f, 0.0f, 1.0f, 1.0f, Dilation(0.0f))
                    .uv(31, 60).mirrored().cuboid(-2.5f, 3.5f, -2.0f, 5.0f, 3.0f, 5.0f, Dilation(0.0f))
                    .mirrored(false)
                    .uv(47, 0).mirrored().cuboid(-2.5f, -1.5f, -3.0f, 5.0f, 5.0f, 6.0f, Dilation(0.0f))
                    .mirrored(false)
                    .uv(0, 1).cuboid(-2.0f, 5.5f, -3.0f, 0.0f, 1.0f, 1.0f, Dilation(0.0f))
                    .uv(0, 2).cuboid(0.0f, 5.5f, -3.0f, 0.0f, 1.0f, 1.0f, Dilation(0.0f)),
                ModelTransform.of(-6.0f, 0.2498f, 5.2391f, 0.0f, 0.0436f, 0.0f)
            )
            val leftbackleg = body.addChild(
                "leftbackleg",
                ModelPartBuilder.create().uv(31, 60)
                    .cuboid(-2.5f, 3.5f, -2.0f, 5.0f, 3.0f, 5.0f, Dilation(0.0f))
                    .uv(47, 0).cuboid(-2.5f, -1.5f, -3.0f, 5.0f, 5.0f, 6.0f, Dilation(0.0f))
                    .uv(2, 2).cuboid(-2.0f, 5.5f, -3.0f, 0.0f, 1.0f, 1.0f, Dilation(0.0f))
                    .uv(2, 1).cuboid(2.0f, 5.5f, -3.0f, 0.0f, 1.0f, 1.0f, Dilation(0.0f))
                    .uv(2, 0).cuboid(0.0f, 5.5f, -3.0f, 0.0f, 1.0f, 1.0f, Dilation(0.0f)),
                ModelTransform.of(6.0f, 0.2498f, 5.2391f, 0.0f, -0.0436f, 0.0f)
            )
            return TexturedModelData.of(meshdefinition, 128, 128)
        }
    }
}