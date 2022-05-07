package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.BimanualSwingAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.BipedWalkAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.SingleBoneLookAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.BimanualFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.BipedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.HeadedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.PoseType
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.TransformedModelPart
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.wavefunction.sineFunction
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.withRotation
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.cablemc.pokemoncobbled.common.util.math.geometry.toRadians
import net.minecraft.client.model.*
import net.minecraft.client.render.entity.model.EntityModelLayer
import net.minecraft.util.math.Vec3d

class SquirtleModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame, BipedFrame, BimanualFrame {
    override val rootPart = registerRelevantPart("squirtle", root.getChild("squirtle"))
    val body = registerRelevantPart("body", rootPart.getChild("body"))
    override val head = registerRelevantPart("head", body.getChild("head"))
    override val rightLeg = registerRelevantPart("rightleg", body.getChild("rightleg"))
    override val leftLeg = registerRelevantPart("leftleg", body.getChild("leftleg"))
    override val rightArm = registerRelevantPart("rightarm", body.getChild("rightarm"))
    override val leftArm = registerRelevantPart("leftarm", body.getChild("leftarm"))
    private val tail = registerRelevantPart("tail", body.getChild("tail"))

    override val portraitScale = 1.6F
    override val portraitTranslation = Vec3d(0.0, 0.10, 0.0)

    override val profileScale = 1.0F
    override val profileTranslation = Vec3d(0.0, 0.0, 0.0)

    override fun registerPoses() {
        registerPose(
                poseType = PoseType.WALK,
                condition = { true },
                idleAnimations = arrayOf(
                        BipedWalkAnimation(this),
                        BimanualSwingAnimation(this),
                        SingleBoneLookAnimation(this),
                        tail.rotation(
                                function = sineFunction(
                                        amplitude = 0.5F,
                                        period = 5F
                                ),
                                axis = TransformedModelPart.Y_AXIS,
                                timeVariable = { _, _, ageInTicks -> ageInTicks / 20 },
                        )
                ),
                transformedParts = arrayOf(
                        leftArm.withRotation(2, 70f.toRadians()),
                        rightArm.withRotation(2, (-70f).toRadians()),
                )
        )
    }

    companion object {
        val LAYER_LOCATION = EntityModelLayer(cobbledResource("squirtle"), "main")
        fun createBodyLayer(): TexturedModelData {
            val meshdefinition = ModelData()
            val partdefinition = meshdefinition.root
            val squirtle = partdefinition.addChild(
                "squirtle",
                ModelPartBuilder.create(),
                ModelTransform.pivot(0.0f, 24.0f, 0.0f)
            )
            val body = squirtle.addChild(
                "body",
                ModelPartBuilder.create().uv(0, 0)
                    .cuboid(-4.0f, -5.0f, -3.5f, 8.0f, 10.0f, 7.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, -8.0f, 0.0f)
            )
            val tail = body.addChild(
                "tail",
                ModelPartBuilder.create().uv(0, 19)
                    .cuboid(0.0f, -5.5f, 0.0f, 0.0f, 8.0f, 11.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 3.0f, 3.5f)
            )
            val head = body.addChild(
                "head",
                ModelPartBuilder.create().uv(0, 17)
                    .cuboid(-3.5f, -6.5f, -4.0f, 7.0f, 6.0f, 7.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, -4.5f, -0.5f)
            )
            val eyes = head.addChild(
                "eyes",
                ModelPartBuilder.create().uv(0, 17)
                    .cuboid(1.5f, -1.5f, -1.0f, 2.0f, 3.0f, 2.0f, Dilation(0.02f))
                    .uv(0, 17).mirrored().cuboid(-3.5f, -1.5f, -1.0f, 2.0f, 3.0f, 2.0f, Dilation(0.02f))
                    .mirrored(false),
                ModelTransform.pivot(0.0f, -3.75f, -3.0f)
            )
            val leftarm = body.addChild(
                "leftarm",
                ModelPartBuilder.create().uv(21, 17)
                    .cuboid(-1.0f, -1.5f, -1.5f, 7.0f, 3.0f, 3.0f, Dilation(0.0f)),
                ModelTransform.pivot(4.0f, -3.25f, -1.25f)
            )
            val rightarm = body.addChild(
                "rightarm",
                ModelPartBuilder.create().uv(23, 0)
                    .cuboid(-6.0f, -1.5f, -1.5f, 7.0f, 3.0f, 3.0f, Dilation(0.0f)),
                ModelTransform.pivot(-4.0f, -3.25f, -1.25f)
            )
            val leftleg = body.addChild(
                "leftleg",
                ModelPartBuilder.create().uv(25, 27)
                    .cuboid(-1.5f, -1.0f, -1.5f, 3.0f, 6.0f, 3.0f, Dilation(0.0f)),
                ModelTransform.pivot(3.0f, 3.0f, -0.5f)
            )
            val rightleg = body.addChild(
                "rightleg",
                ModelPartBuilder.create().uv(30, 6)
                    .cuboid(-1.5f, -1.0f, -1.5f, 3.0f, 6.0f, 3.0f, Dilation(0.0f)),
                ModelTransform.pivot(-3.0f, 3.0f, -0.5f)
            )
            return TexturedModelData.of(meshdefinition, 64, 64)
        }
    }
}