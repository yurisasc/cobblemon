package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.SingleBoneLookAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.BiWingedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.BipedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.HeadedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.getChildOf
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.PoseType
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.TransformedModelPart
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.wavefunction.parabolaFunction
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.wavefunction.sineFunction
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.cablemc.pokemoncobbled.common.util.math.geometry.toRadians
import net.minecraft.client.model.*
import net.minecraft.client.render.entity.model.EntityModelLayer
import net.minecraft.util.math.Vec3d

class PidgeottoModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame, BipedFrame, BiWingedFrame {
    override val rootPart = registerRelevantPart("pidgeotto", root.getChild("pidgeotto"))
    override val leftWing = registerRelevantPart("leftwing", rootPart.getChildOf("body","leftwing"))
    override val rightWing = registerRelevantPart("rightwing", rootPart.getChildOf("body","rightwing"))
    override val leftLeg = registerRelevantPart("leftleg", rootPart.getChildOf("body","leftleg"))
    override val rightLeg = registerRelevantPart("rightleg", rootPart.getChildOf("body","rightleg"))
    override val head = registerRelevantPart("head", rootPart.getChildOf("body","head"))
    private val tail = registerRelevantPart("tail", rootPart.getChildOf("body","tail"))

    override val portraitScale = 1.75F
    override val portraitTranslation = Vec3d(-0.05, -0.3, 0.0)
    override val profileScale = 1.0F
    override val profileTranslation = Vec3d(0.0, 0.0, 0.0)

    override fun registerPoses() {
        registerPose(
            poseType = PoseType.NONE,
            condition = { !it.isMoving.get() },
            transformTicks = 0,
            idleAnimations = arrayOf(
                SingleBoneLookAnimation(this),
            ),
            transformedParts = arrayOf()
        )
        registerPose(
            poseType = PoseType.WALK,
            condition = { it.isMoving.get() },
            transformTicks = 5,
            idleAnimations = arrayOf(
                SingleBoneLookAnimation(this),
                rootPart.translation(
                    function = parabolaFunction(
                        peak = -4F,
                        period = 0.4F
                    ),
                    timeVariable = { state, _, _ -> state?.animationSeconds },
                    axis = TransformedModelPart.Y_AXIS
                ),
                head.translation(
                    function = sineFunction(
                        amplitude = (-20F).toRadians(),
                        period = 1F,
                        verticalShift = (-10F).toRadians()
                    ),
                    axis = TransformedModelPart.X_AXIS,
                    timeVariable = { state, _, _ -> state?.animationSeconds }
                ),
                leftLeg.rotation(
                    function = parabolaFunction(
                        tightness = -20F,
                        phaseShift = 0F,
                        verticalShift = (30F).toRadians()
                    ),
                    axis = TransformedModelPart.X_AXIS,
                    timeVariable = { _, _, ageInTicks -> ageInTicks / 20 },
                ),
                rightLeg.rotation(
                    function = parabolaFunction(
                        tightness = -20F,
                        phaseShift = 0F,
                        verticalShift = (30F).toRadians()
                    ),
                    axis = TransformedModelPart.X_AXIS,
                    timeVariable = { _, _, ageInTicks -> ageInTicks / 20 },
                ),
                tail.rotation(
                    function = sineFunction(
                        amplitude = (-5F).toRadians(),
                        period = 1F
                    ),
                    axis = TransformedModelPart.X_AXIS,
                    timeVariable = { _, _, ageInTicks -> ageInTicks / 20 },
                ),
                wingFlap(
                    flapFunction = sineFunction(
                        amplitude = (-5F).toRadians(),
                        period = 0.4F,
                        phaseShift = 0.00F,
                        verticalShift = (-20F).toRadians()
                    ),
                    timeVariable = { state, _, _ -> state?.animationSeconds },
                    axis = TransformedModelPart.Z_AXIS
                ),
                rightWing.translation(
                    function = parabolaFunction(
                        tightness = -10F,
                        phaseShift = 30F,
                        verticalShift = (25F).toRadians()
                    ),
                    axis = TransformedModelPart.Y_AXIS,
                    timeVariable = { _, _, ageInTicks -> ageInTicks / 20 },
                ),
                leftWing.translation(
                    function = parabolaFunction(
                        tightness = -10F,
                        phaseShift = 30F,
                        verticalShift = (25F).toRadians()
                    ),
                    axis = TransformedModelPart.Y_AXIS,
                    timeVariable = { _, _, ageInTicks -> ageInTicks / 20 },
                ),
            ),
            transformedParts = arrayOf(),
        )
    }

    companion object {
        val LAYER_LOCATION = EntityModelLayer(cobbledResource("pidgeotto"), "main")
        fun createBodyLayer(): TexturedModelData {
            val meshdefinition = ModelData()
            val partdefinition = meshdefinition.root

            val pidgeotto = partdefinition.addChild(
                "pidgeotto",
                ModelPartBuilder.create(),
                ModelTransform.pivot(0.0f, 24.0f, 0.0f)
            )

            val body =
                pidgeotto.addChild("body", ModelPartBuilder.create(), ModelTransform.pivot(0.0f, -7.1635f, -0.7418f))

            val body_r1 = body.addChild(
                "body_r1",
                ModelPartBuilder.create().uv(0, 0)
                    .cuboid(-2.5f, -1.5f, -5.0f, 6.0f, 5.0f, 10.0f, Dilation(0.0f)),
                ModelTransform.of(-0.5f, -0.8365f, 0.7418f, -0.2618f, 0.0f, 0.0f)
            )

            val tail = body.addChild(
                "tail",
                ModelPartBuilder.create().uv(14, 0)
                    .cuboid(-3.5f, 0.0f, 0.0f, 7.0f, 0.0f, 8.0f, Dilation(0.02f)),
                ModelTransform.pivot(0.0f, 2.4135f, 4.9918f)
            )

            val head = body.addChild(
                "head",
                ModelPartBuilder.create().uv(20, 15)
                    .cuboid(-2.5f, -8.0f, -2.5f, 5.0f, 9.0f, 5.0f, Dilation(0.0f))
                    .uv(32, 8).cuboid(-1.0f, -5.75f, -4.5f, 2.0f, 2.0f, 2.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, -0.3365f, -2.5082f)
            )

            val body_r2 = head.addChild(
                "body_r2",
                ModelPartBuilder.create().uv(0, 27)
                    .cuboid(-3.0f, -3.0f, 0.0f, 7.0f, 5.0f, 0.0f, Dilation(0.02f)),
                ModelTransform.of(-0.5f, -8.2739f, 2.8572f, -2.0944f, 0.0f, 0.0f)
            )

            val body_r3 = head.addChild(
                "body_r3",
                ModelPartBuilder.create().uv(30, 29)
                    .cuboid(-3.0f, -2.0f, 0.0f, 6.0f, 4.0f, 0.0f, Dilation(0.02f)),
                ModelTransform.of(0.0f, -8.7562f, -0.8067f, -1.309f, 0.0f, 0.0f)
            )

            val body_r4 = head.addChild(
                "body_r4",
                ModelPartBuilder.create().uv(0, 32)
                    .cuboid(-2.5f, -1.5f, 0.0f, 5.0f, 3.0f, 0.0f, Dilation(0.02f)),
                ModelTransform.of(0.0f, -6.7443f, -2.8693f, -0.0873f, 0.0f, 0.0f)
            )

            val rightwing = body.addChild(
                "rightwing",
                ModelPartBuilder.create().uv(0, 11)
                    .cuboid(0.0f, -2.5f, 0.0f, 0.0f, 6.0f, 10.0f, Dilation(0.02f)),
                ModelTransform.of(-3.0f, -1.5865f, -3.5082f, -0.3491f, -0.1222f, 0.0436f)
            )

            val leftwing = body.addChild(
                "leftwing",
                ModelPartBuilder.create().uv(0, 5)
                    .cuboid(0.0f, -2.5f, 0.0f, 0.0f, 6.0f, 10.0f, Dilation(0.02f)),
                ModelTransform.of(3.0f, -1.5865f, -3.5082f, -0.3491f, 0.1222f, -0.0436f)
            )

            val leftleg = body.addChild(
                "leftleg",
                ModelPartBuilder.create().uv(10, 28)
                    .cuboid(-1.0f, -0.75f, -2.0f, 2.0f, 3.0f, 4.0f, Dilation(0.0f)),
                ModelTransform.pivot(2.25f, 1.9135f, 1.2418f)
            )

            val leftknee = leftleg.addChild(
                "leftknee",
                ModelPartBuilder.create().uv(20, 15)
                    .cuboid(-0.5f, -1.0f, -0.5f, 1.0f, 3.0f, 1.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 2.25f, 0.5f)
            )

            val leftfoot = leftknee.addChild(
                "leftfoot",
                ModelPartBuilder.create().uv(0, 0)
                    .cuboid(-1.0f, 0.0f, -2.0f, 2.0f, 1.0f, 3.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 2.0f, 0.0f)
            )

            val leftbacktoe = leftfoot.addChild(
                "leftbacktoe",
                ModelPartBuilder.create().uv(6, 6)
                    .cuboid(0.0f, -0.5f, 0.0f, 0.0f, 1.0f, 2.0f, Dilation(0.02f)),
                ModelTransform.pivot(0.0f, 0.5f, 1.0f)
            )

            val leftfronttoe3 = leftfoot.addChild(
                "leftfronttoe3",
                ModelPartBuilder.create().uv(0, 6)
                    .cuboid(0.0f, -0.5f, -3.0f, 0.0f, 1.0f, 3.0f, Dilation(0.02f)),
                ModelTransform.pivot(0.75f, 0.5f, -2.0f)
            )

            val leftfronttoe4 = leftfoot.addChild(
                "leftfronttoe4",
                ModelPartBuilder.create().uv(0, 5)
                    .cuboid(0.0f, -0.5f, -3.0f, 0.0f, 1.0f, 3.0f, Dilation(0.02f)),
                ModelTransform.pivot(-0.75f, 0.5f, -2.0f)
            )

            val rightleg = body.addChild(
                "rightleg",
                ModelPartBuilder.create().uv(22, 29)
                    .cuboid(-1.0f, -0.75f, -2.0f, 2.0f, 3.0f, 4.0f, Dilation(0.0f)),
                ModelTransform.pivot(-2.25f, 1.9135f, 1.2418f)
            )

            val rightknee = rightleg.addChild(
                "rightknee",
                ModelPartBuilder.create().uv(22, 29)
                    .cuboid(-0.5f, -1.0f, -0.5f, 1.0f, 3.0f, 1.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 2.25f, 0.5f)
            )

            val rightfoot = rightknee.addChild(
                "rightfoot",
                ModelPartBuilder.create().uv(0, 4)
                    .cuboid(-1.0f, 0.0f, -2.0f, 2.0f, 1.0f, 3.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 2.0f, 0.0f)
            )

            val rightbacktoe = rightfoot.addChild(
                "rightbacktoe",
                ModelPartBuilder.create().uv(6, 7)
                    .cuboid(0.0f, -0.5f, 0.0f, 0.0f, 1.0f, 2.0f, Dilation(0.02f)),
                ModelTransform.pivot(0.0f, 0.5f, 1.0f)
            )

            val rightfronttoe = rightfoot.addChild(
                "rightfronttoe",
                ModelPartBuilder.create().uv(22, 6)
                    .cuboid(0.0f, -0.5f, -3.0f, 0.0f, 1.0f, 3.0f, Dilation(0.02f)),
                ModelTransform.pivot(-0.75f, 0.5f, -2.0f)
            )

            val rightfronttoe2 = rightfoot.addChild(
                "rightfronttoe2",
                ModelPartBuilder.create().uv(22, 5)
                    .cuboid(0.0f, -0.5f, -3.0f, 0.0f, 1.0f, 3.0f, Dilation(0.02f)),
                ModelTransform.pivot(0.75f, 0.5f, -2.0f)
            )

            return TexturedModelData.of(meshdefinition, 64, 64)
        }
    }
}