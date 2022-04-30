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

class IvysaurModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame, QuadrupedFrame {

    override val rootPart = registerRelevantPart("ivysaur", root.getChild("ivysaur"))
    override val head = registerRelevantPart("head", rootPart.getChild("body").getChild("head"))
    override val hindRightLeg = registerRelevantPart("rightbackleg", rootPart.getChild("body").getChild("rightbackleg"))
    override val hindLeftLeg = registerRelevantPart("leftbackleg", rootPart.getChild("body").getChild("leftbackleg"))
    override val foreRightLeg = registerRelevantPart("rightleg", rootPart.getChild("body").getChild("rightleg"))
    override val foreLeftLeg = registerRelevantPart("leftleg", rootPart.getChild("body").getChild("leftleg"))

    override val portraitScale = 1.65F
    override val portraitTranslation = Vec3d(-0.1, -0.5, 0.0)
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
        val LAYER_LOCATION = EntityModelLayer(cobbledResource("ivysaur"), "main")
        fun createBodyLayer(): TexturedModelData {
            val meshdefinition = ModelData()
            val partdefinition = meshdefinition.root
            val ivysaur = partdefinition.addChild(
                "ivysaur",
                ModelPartBuilder.create(),
                ModelTransform.pivot(0.0f, 24.0f, 0.0f)
            )
            val body =
                ivysaur.addChild("body", ModelPartBuilder.create(), ModelTransform.pivot(0.0f, -5.5528f, 0.2424f))
            val body_r1 = body.addChild(
                "body_r1",
                ModelPartBuilder.create().uv(0, 0)
                    .cuboid(-8.0f, -6.0f, -1.0f, 9.0f, 6.0f, 12.0f, Dilation(0.0f)),
                ModelTransform.of(3.5f, 2.5528f, -5.2424f, -0.0873f, 0.0f, 0.0f)
            )
            val head = body.addChild(
                "head",
                ModelPartBuilder.create().uv(0, 18)
                    .cuboid(-5.0f, -5.5f, -4.25f, 10.0f, 7.0f, 6.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, -2.1972f, -4.2424f)
            )
            val eyes = head.addChild(
                "eyes",
                ModelPartBuilder.create().uv(54, 0).mirrored()
                    .cuboid(-1.0f, -5.0f, -2.0f, 3.0f, 3.0f, 2.0f, Dilation(0.02f)).mirrored(false)
                    .uv(54, 0).cuboid(-8.0f, -5.0f, -2.0f, 3.0f, 3.0f, 2.0f, Dilation(0.02f)),
                ModelTransform.pivot(3.0f, 2.0f, -2.25f)
            )
            val pupils = eyes.addChild(
                "pupils",
                ModelPartBuilder.create().uv(54, 5).mirrored()
                    .cuboid(-0.975f, -5.0f, -2.025f, 3.0f, 3.0f, 2.0f, Dilation(0.02f)).mirrored(false)
                    .uv(54, 5).cuboid(-8.025f, -5.0f, -2.025f, 3.0f, 3.0f, 2.0f, Dilation(0.02f)),
                ModelTransform.pivot(0.0f, 0.0f, 0.0f)
            )
            val rightear = head.addChild(
                "rightear",
                ModelPartBuilder.create().uv(0, 5)
                    .cuboid(0.0f, -3.0f, -2.0f, 0.0f, 3.0f, 4.0f, Dilation(0.0f)),
                ModelTransform.of(-4.5f, -5.5f, -1.25f, 0.0f, 0.0f, -0.0873f)
            )
            val leftear = head.addChild(
                "leftear",
                ModelPartBuilder.create().uv(18, 27)
                    .cuboid(0.0f, -3.0f, -2.0f, 0.0f, 3.0f, 4.0f, Dilation(0.0f)),
                ModelTransform.of(4.5f, -5.5f, -1.25f, 0.0f, 0.0f, 0.0873f)
            )
            val bulb =
                body.addChild("bulb", ModelPartBuilder.create(), ModelTransform.pivot(0.0f, -2.8105f, 2.5804f))
            val bulb_r1 = bulb.addChild(
                "bulb_r1",
                ModelPartBuilder.create().uv(26, 25)
                    .cuboid(-3.5f, -3.5f, -3.75f, 7.0f, 8.0f, 6.0f, Dilation(0.0f)),
                ModelTransform.of(0.0f, -3.9668f, 1.0211f, -0.0436f, 0.0f, 0.0f)
            )
            val frontleaf = bulb.addChild(
                "frontleaf",
                ModelPartBuilder.create(),
                ModelTransform.of(0.25f, -0.7538f, -2.7213f, -1.1345f, 0.0f, 0.0f)
            )
            val frontleaf_r1 = frontleaf.addChild(
                "frontleaf_r1",
                ModelPartBuilder.create().uv(24, 0)
                    .cuboid(-2.25f, 4.5f, -9.5f, 6.0f, 0.0f, 6.0f, Dilation(0.0f)),
                ModelTransform.of(-0.9456f, -4.463f, 3.5f, 0.0436f, 0.0f, 0.0f)
            )
            val frontleaftip = frontleaf.addChild(
                "frontleaftip",
                ModelPartBuilder.create(),
                ModelTransform.of(-0.1956f, 0.578f, -5.7918f, 0.48f, 0.0f, 0.0f)
            )
            val frontleaftip_r1 = frontleaftip.addChild(
                "frontleaftip_r1",
                ModelPartBuilder.create().uv(20, 18)
                    .cuboid(-2.25f, 4.5f, -15.5f, 6.0f, 0.0f, 6.0f, Dilation(0.0f)),
                ModelTransform.of(-0.75f, -5.0275f, 9.3526f, 0.0436f, 0.0f, 0.0f)
            )
            val backleaf =
                bulb.addChild("backleaf", ModelPartBuilder.create(), ModelTransform.pivot(0.25f, -0.2538f, 3.0755f))
            val backleaf_r1 = backleaf.addChild(
                "backleaf_r1",
                ModelPartBuilder.create().uv(0, 31)
                    .cuboid(-2.25f, 4.5f, 3.5f, 6.0f, 0.0f, 6.0f, Dilation(0.0f)),
                ModelTransform.of(-0.9456f, -4.463f, -3.5f, -0.0436f, 0.0f, 0.0f)
            )
            val backleaftip = backleaf.addChild(
                "backleaftip",
                ModelPartBuilder.create(),
                ModelTransform.pivot(-0.1956f, 0.578f, 5.7918f)
            )
            val backleaftip_r1 = backleaftip.addChild(
                "backleaftip_r1",
                ModelPartBuilder.create().uv(24, 6)
                    .cuboid(-2.25f, 4.5f, 9.5f, 6.0f, 0.0f, 6.0f, Dilation(0.0f)),
                ModelTransform.of(-0.75f, -5.041f, -9.2918f, -0.0436f, 0.0f, 0.0f)
            )
            val leftsideleaf = bulb.addChild(
                "leftsideleaf",
                ModelPartBuilder.create(),
                ModelTransform.pivot(3.5f, -0.2538f, 0.0755f)
            )
            val leftsideleaf_r1 = leftsideleaf.addChild(
                "leftsideleaf_r1",
                ModelPartBuilder.create().uv(36, 0)
                    .cuboid(3.5f, 4.5f, -3.75f, 6.0f, 0.0f, 6.0f, Dilation(0.0f)),
                ModelTransform.of(-3.5f, -4.463f, 0.9456f, -0.0436f, 0.0f, 0.0f)
            )
            val leftsideleaftip = leftsideleaf.addChild(
                "leftsideleaftip",
                ModelPartBuilder.create(),
                ModelTransform.pivot(6.0f, 0.0f, 0.0f)
            )
            val leftsideleaftip_r1 = leftsideleaftip.addChild(
                "leftsideleaftip_r1",
                ModelPartBuilder.create().uv(32, 18)
                    .cuboid(9.5f, 4.5f, -3.75f, 6.0f, 0.0f, 6.0f, Dilation(0.0f)),
                ModelTransform.of(-9.5f, -4.463f, 0.9456f, -0.0436f, 0.0f, 0.0f)
            )
            val rightsideleaf = bulb.addChild(
                "rightsideleaf",
                ModelPartBuilder.create(),
                ModelTransform.pivot(-3.5f, -0.2538f, 0.0755f)
            )
            val rightsideleaf_r1 = rightsideleaf.addChild(
                "rightsideleaf_r1",
                ModelPartBuilder.create().uv(36, 6)
                    .cuboid(-9.5f, 4.5f, -3.75f, 6.0f, 0.0f, 6.0f, Dilation(0.0f)),
                ModelTransform.of(3.5f, -4.463f, 0.9456f, -0.0436f, 0.0f, 0.0f)
            )
            val rightsideleaftip = rightsideleaf.addChild(
                "rightsideleaftip",
                ModelPartBuilder.create(),
                ModelTransform.pivot(-6.0f, 0.0f, 0.0f)
            )
            val rightsideleaftip_r1 = rightsideleaftip.addChild(
                "rightsideleaftip_r1",
                ModelPartBuilder.create().uv(36, 12)
                    .cuboid(-15.5f, 4.5f, -3.75f, 6.0f, 0.0f, 6.0f, Dilation(0.0f)),
                ModelTransform.of(9.5f, -4.463f, 0.9456f, -0.0436f, 0.0f, 0.0f)
            )
            val rightleg = body.addChild(
                "rightleg",
                ModelPartBuilder.create().uv(0, 0)
                    .cuboid(-1.5f, -1.0f, -1.5f, 3.0f, 6.0f, 3.0f, Dilation(0.0f)),
                ModelTransform.pivot(-3.5f, 0.5528f, -4.2424f)
            )
            val leftleg = body.addChild(
                "leftleg",
                ModelPartBuilder.create().uv(0, 37)
                    .cuboid(-1.5f, -1.0f, -1.5f, 3.0f, 6.0f, 3.0f, Dilation(0.0f)),
                ModelTransform.pivot(3.5f, 0.5528f, -4.2424f)
            )
            val rightbackleg = body.addChild(
                "rightbackleg",
                ModelPartBuilder.create().uv(27, 39)
                    .cuboid(-1.5f, -1.0f, -1.5f, 3.0f, 6.0f, 3.0f, Dilation(0.0f)),
                ModelTransform.pivot(-3.5f, 0.5528f, 4.2576f)
            )
            val leftbackleg = body.addChild(
                "leftbackleg",
                ModelPartBuilder.create().uv(15, 36)
                    .cuboid(-1.5f, -1.0f, -1.5f, 3.0f, 6.0f, 3.0f, Dilation(0.0f)),
                ModelTransform.pivot(3.5f, 0.5528f, 4.2576f)
            )
            return TexturedModelData.of(meshdefinition, 64, 64)
        }
    }
}