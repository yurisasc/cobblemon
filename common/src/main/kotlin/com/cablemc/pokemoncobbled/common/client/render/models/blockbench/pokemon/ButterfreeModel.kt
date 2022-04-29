package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.SingleBoneLookAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.WingFlapIdleAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.BiWingedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.HeadedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.getChildOf
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.PoseType
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.TransformedModelPart.Companion.X_AXIS
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.TransformedModelPart.Companion.Y_AXIS
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.wavefunction.sineFunction
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.withRotation
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.cablemc.pokemoncobbled.common.util.math.geometry.toRadians
import net.minecraft.client.render.entity.model.EntityModelLayer
import net.minecraft.client.model.ModelPart
import net.minecraft.client.model.ModelTransform
import net.minecraft.client.model.Dilation
import net.minecraft.client.model.ModelPartBuilder
import net.minecraft.client.model.TexturedModelData
import net.minecraft.client.model.ModelData
import net.minecraft.util.math.Vec3d
class ButterfreeModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame, BiWingedFrame {
    override val rootPart = registerRelevantPart("butterfree", root.getChild("butterfree"))
    override val head = registerRelevantPart("head", rootPart.getChildOf("body", "head"))

    override val leftWing = registerRelevantPart("leftwing", rootPart.getChildOf("body", "leftwing"))
    override val rightWing = registerRelevantPart("rightwing", rootPart.getChildOf("body", "rightwing"))

    val leftWingBack = registerRelevantPart("leftwingback", rootPart.getChildOf("body", "leftwingback"))
    val rightWingBack = registerRelevantPart("rightwingback", rootPart.getChildOf("body", "rightwingback"))

    override val portraitScale = 1.5F
    override val portraitTranslation = Vec3d(0.1, 0.2, 0.0)

    override val profileScale = 1.0F
    override val profileTranslation = Vec3d(0.0, 0.0, 0.0)

    override fun registerPoses() {
        registerPose(
            poseType = PoseType.NONE,
            condition = { !it.isMoving.get() },
            transformTicks = 3,
            idleAnimations = arrayOf(
                SingleBoneLookAnimation(this),
                WingFlapIdleAnimation(
                    frame = this,
                    timeVariable = { state, _, _ -> state?.animationSeconds ?: 0F },
                    axis = Y_AXIS,
                    flapFunction = sineFunction(
                        amplitude = (-30F).toRadians(),
                        period = 0.4F,
                        verticalShift = (-35F).toRadians()
                    )
                ),
                WingFlapIdleAnimation(
                    frame = object : BiWingedFrame {
                        override val leftWing = leftWingBack
                        override val rightWing = rightWingBack
                        override val rootPart = this@ButterfreeModel.rootPart
                    },
                    timeVariable = { state, _, _ -> state?.animationSeconds ?: 0F },
                    axis = Y_AXIS,
                    flapFunction = sineFunction(
                        amplitude = (-30F).toRadians(),
                        period = 0.4F,
                        phaseShift = 0.00F,
                        verticalShift = (-45F).toRadians()
                    )
                )
            ),
            transformedParts = arrayOf()
        )

        registerPose(
            poseType = PoseType.WALK,
            condition = { it.isMoving.get() },
            transformTicks = 3,
            idleAnimations = arrayOf(
                SingleBoneLookAnimation(this),
                WingFlapIdleAnimation(
                    frame = this,
                    timeVariable = { state, _, _ -> state?.animationSeconds ?: 0F },
                    axis = Y_AXIS,
                    flapFunction = sineFunction(
                        amplitude = (-30F).toRadians(),
                        period = 0.3F,
                        verticalShift = (-35F).toRadians()
                    )
                ),
                WingFlapIdleAnimation(
                    frame = object : BiWingedFrame {
                        override val leftWing = leftWingBack
                        override val rightWing = rightWingBack
                        override val rootPart = this@ButterfreeModel.rootPart
                    },
                    timeVariable = { state, _, _ -> state?.animationSeconds ?: 0F },
                    axis = Y_AXIS,
                    flapFunction = sineFunction(
                        amplitude = (-30F).toRadians(),
                        period = 0.3F,
                        phaseShift = 0.00F,
                        verticalShift = (-45F).toRadians()
                    )
                )
            ),
            transformedParts = arrayOf(rootPart.withRotation(X_AXIS, 20F.toRadians()))
        )
    }

    companion object {
        val LAYER_LOCATION: EntityModelLayer = EntityModelLayer(cobbledResource("butterfree"), "main")
        fun createBodyLayer(): TexturedModelData {
            val meshdefinition = ModelData()
            val partdefinition = meshdefinition.root

            val butterfree = partdefinition.addChild(
                "butterfree",
                ModelPartBuilder.create(),
                ModelTransform.pivot(0.0f, 23.0f, 0.0f)
            )

            val body = butterfree.addChild(
                "body",
                ModelPartBuilder.create().uv(38, 11)
                    .cuboid(-3.0f, -3.0f, -2.0f, 6.0f, 6.0f, 4.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, -9.0f, 0.0f)
            )

            val head = body.addChild(
                "head",
                ModelPartBuilder.create().uv(42, 33)
                    .cuboid(-1.5f, -2.5f, -3.75f, 3.0f, 2.0f, 1.0f, Dilation(0.0f))
                    .uv(38, 0).cuboid(-4.0f, -6.0f, -3.0f, 8.0f, 6.0f, 5.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, -3.0f, 0.0f)
            )

            val leftattena = head.addChild(
                "leftattena",
                ModelPartBuilder.create().uv(40, 0)
                    .cuboid(-0.5f, -5.0f, 0.0f, 1.0f, 5.0f, 0.0f, Dilation(0.02f)),
                ModelTransform.pivot(2.5f, -6.0f, -1.0f)
            )

            val leftattenatip = leftattena.addChild(
                "leftattenatip",
                ModelPartBuilder.create().uv(38, 40)
                    .cuboid(-1.5f, -7.0f, 0.0f, 2.0f, 7.0f, 0.0f, Dilation(0.02f)),
                ModelTransform.pivot(0.0f, -5.0f, 0.0f)
            )

            val rightattena = head.addChild(
                "rightattena",
                ModelPartBuilder.create().uv(38, 0)
                    .cuboid(-0.5f, -5.0f, 0.0f, 1.0f, 5.0f, 0.0f, Dilation(0.02f)),
                ModelTransform.pivot(-2.5f, -6.0f, -1.0f)
            )

            val rightattenatip = rightattena.addChild(
                "rightattenatip",
                ModelPartBuilder.create().uv(38, 33)
                    .cuboid(-0.5f, -7.0f, 0.0f, 2.0f, 7.0f, 0.0f, Dilation(0.02f)),
                ModelTransform.pivot(0.0f, -5.0f, 0.0f)
            )

            val rightwingback = body.addChild(
                "rightwingback",
                ModelPartBuilder.create().uv(96, 0)
                    .cuboid(-16.0f, -4.0f, 0.0f, 16.0f, 11.0f, 0.0f, Dilation(0.02f)),
                ModelTransform.of(0.0f, 1.0f, 2.0f, 0.0f, 0.1309f, 0.0f)
            )

            val leftwingback = body.addChild(
                "leftwingback",
                ModelPartBuilder.create().uv(96, 0).mirrored()
                    .cuboid(0.0f, -4.0f, 0.0f, 16.0f, 11.0f, 0.0f, Dilation(0.02f)).mirrored(false),
                ModelTransform.of(0.0f, 1.0f, 2.0f, 0.0f, -0.1309f, 0.0f)
            )

            val rightwing = body.addChild(
                "rightwing",
                ModelPartBuilder.create().uv(0, 0)
                    .cuboid(-19.0f, -14.0f, 0.0f, 19.0f, 19.0f, 0.0f, Dilation(0.02f)),
                ModelTransform.of(0.0f, -3.0f, 2.0f, 0.0f, 0.0873f, 0.0f)
            )

            val leftwing = body.addChild(
                "leftwing",
                ModelPartBuilder.create().uv(0, 0).mirrored()
                    .cuboid(0.0f, -14.0f, 0.0f, 19.0f, 19.0f, 0.0f, Dilation(0.02f)).mirrored(false),
                ModelTransform.of(0.0f, -3.0f, 2.0f, 0.0f, -0.0873f, 0.0f)
            )

            val lefthand = body.addChild(
                "lefthand",
                ModelPartBuilder.create().uv(42, 40)
                    .cuboid(0.0f, -1.0f, -2.0f, 1.0f, 2.0f, 2.0f, Dilation(0.0f)),
                ModelTransform.of(3.0f, -1.25f, -0.5f, 0.0f, 0.2618f, 0.0f)
            )

            val righthand = body.addChild(
                "righthand",
                ModelPartBuilder.create().uv(42, 40).mirrored()
                    .cuboid(-1.0f, -1.0f, -2.0f, 1.0f, 2.0f, 2.0f, Dilation(0.0f)).mirrored(false),
                ModelTransform.of(-3.0f, -1.25f, -0.5f, 0.0f, -0.2618f, 0.0f)
            )

            val leftleg = body.addChild(
                "leftleg",
                ModelPartBuilder.create().uv(38, 27)
                    .cuboid(-1.0f, 0.0f, 0.0f, 3.0f, 6.0f, 0.0f, Dilation(0.02f)),
                ModelTransform.pivot(1.75f, 3.0f, 0.0f)
            )

            val rightleg = body.addChild(
                "rightleg",
                ModelPartBuilder.create().uv(38, 21)
                    .cuboid(-2.0f, 0.0f, 0.0f, 3.0f, 6.0f, 0.0f, Dilation(0.02f)),
                ModelTransform.pivot(-1.75f, 3.0f, 0.0f)
            )

            return TexturedModelData.of(meshdefinition, 128, 128)
        }
    }
}