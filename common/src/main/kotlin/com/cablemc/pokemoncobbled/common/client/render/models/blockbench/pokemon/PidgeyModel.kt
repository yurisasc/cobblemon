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

class PidgeyModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame, BipedFrame, BiWingedFrame{
    override val rootPart = registerRelevantPart("pidgey", root.getChild("pidgey"))
    override val leftWing = registerRelevantPart("leftwing", rootPart.getChildOf("body","leftwing"))
    override val rightWing = registerRelevantPart("rightwing", rootPart.getChildOf("body","rightwing"))
    override val leftLeg = registerRelevantPart("leftleg", rootPart.getChildOf("body","leftleg"))
    override val rightLeg = registerRelevantPart("rightleg", rootPart.getChildOf("body","rightleg"))
    override val head = registerRelevantPart("head", rootPart.getChildOf("body","head"))
    private val tail = registerRelevantPart("tail", rootPart.getChildOf("body","tail"))

    override val portraitScale = 1.95F
    override val portraitTranslation = Vec3d(-0.05, -0.7, 0.0)

    override val profileScale = 1.0F
    override val profileTranslation = Vec3d(0.0, 0.0, 0.0)

    override fun registerPoses() {
        registerPose(
            poseType = PoseType.NONE,
            condition = { !it.isMoving.get() },
            transformTicks = 0,
            idleAnimations = arrayOf(
                SingleBoneLookAnimation(this),
            )
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
            )
        )
    }

    companion object {
        val LAYER_LOCATION = EntityModelLayer(cobbledResource("pidgey"), "main")
        fun createBodyLayer(): TexturedModelData {
            val meshdefinition = ModelData()
            val partdefinition = meshdefinition.root

            val pidgey =
                partdefinition.addChild("pidgey", ModelPartBuilder.create(), ModelTransform.pivot(0.0f, 24.0f, 0.0f))

            val body =
                pidgey.addChild("body", ModelPartBuilder.create(), ModelTransform.pivot(0.0f, -5.3706f, -0.267f))

            val body_r1 = body.addChild(
                "body_r1",
                ModelPartBuilder.create().uv(0, 0)
                    .cuboid(-2.0f, -2.5f, -3.5f, 5.0f, 5.0f, 8.0f, Dilation(0.0f)),
                ModelTransform.of(-0.5f, -0.1294f, -0.483f, -0.2618f, 0.0f, 0.0f)
            )

            val head = body.addChild(
                "head",
                ModelPartBuilder.create().uv(16, 13)
                    .cuboid(-2.0f, -6.5f, -2.0f, 4.0f, 7.0f, 4.0f, Dilation(0.0f))
                    .uv(0, 0).cuboid(-1.0f, -5.25f, -4.0f, 2.0f, 2.0f, 2.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, -0.3794f, -2.233f)
            )

            val head_r1 = head.addChild(
                "head_r1",
                ModelPartBuilder.create().uv(21, 5)
                    .cuboid(-2.5f, 0.0f, 0.0f, 5.0f, 0.0f, 5.0f, Dilation(0.02f)),
                ModelTransform.of(0.0f, -6.5f, -2.0f, 0.2618f, 0.0f, 0.0f)
            )

            val tail = body.addChild(
                "tail",
                ModelPartBuilder.create().uv(13, 0)
                    .cuboid(-3.0f, 0.0f, 0.0f, 6.0f, 0.0f, 5.0f, Dilation(0.02f)),
                ModelTransform.of(0.0f, 1.8706f, 3.517f, 0.1309f, 0.0f, 0.0f)
            )

            val leftwing = body.addChild(
                "leftwing",
                ModelPartBuilder.create().uv(0, 5)
                    .cuboid(0.0f, -2.5f, 0.0f, 0.0f, 5.0f, 8.0f, Dilation(0.02f)),
                ModelTransform.of(2.5f, -1.1294f, -2.983f, -0.4378f, 0.0791f, -0.037f)
            )

            val rightwing = body.addChild(
                "rightwing",
                ModelPartBuilder.create().uv(0, 10)
                    .cuboid(0.0f, -2.5f, 0.0f, 0.0f, 5.0f, 8.0f, Dilation(0.02f)),
                ModelTransform.of(-2.5f, -1.1294f, -2.983f, -0.4378f, -0.0791f, 0.037f)
            )

            val leftleg = body.addChild(
                "leftleg",
                ModelPartBuilder.create().uv(16, 13)
                    .cuboid(-0.5f, -1.0f, -0.5f, 1.0f, 3.0f, 1.0f, Dilation(0.0f)),
                ModelTransform.pivot(1.5f, 2.3706f, 0.767f)
            )

            val leftfoot = leftleg.addChild(
                "leftfoot",
                ModelPartBuilder.create().uv(0, 4)
                    .cuboid(-1.5f, -1.0f, -2.0f, 2.0f, 1.0f, 2.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.5f, 3.0f, 0.5f)
            )

            val backlefttoe = leftfoot.addChild(
                "backlefttoe",
                ModelPartBuilder.create().uv(6, 22)
                    .cuboid(0.0f, -0.5f, 0.0f, 0.0f, 1.0f, 2.0f, Dilation(0.02f)),
                ModelTransform.pivot(-0.5f, -0.5f, 0.0f)
            )

            val frontlefttoe3 = leftfoot.addChild(
                "frontlefttoe3",
                ModelPartBuilder.create().uv(0, 20)
                    .cuboid(0.0f, -0.5f, -3.0f, 0.0f, 1.0f, 3.0f, Dilation(0.02f)),
                ModelTransform.pivot(-1.25f, -0.5f, -2.0f)
            )

            val frontlefttoe4 = leftfoot.addChild(
                "frontlefttoe4",
                ModelPartBuilder.create().uv(0, 4)
                    .cuboid(0.0f, -0.5f, -3.0f, 0.0f, 1.0f, 3.0f, Dilation(0.02f)),
                ModelTransform.pivot(0.25f, -0.5f, -2.0f)
            )

            val rightleg = body.addChild(
                "rightleg",
                ModelPartBuilder.create().uv(11, 23)
                    .cuboid(-0.5f, -1.0f, -0.5f, 1.0f, 3.0f, 1.0f, Dilation(0.0f)),
                ModelTransform.pivot(-1.5f, 2.3706f, 0.767f)
            )

            val rightfoot = rightleg.addChild(
                "rightfoot",
                ModelPartBuilder.create().uv(18, 5)
                    .cuboid(-0.5f, -1.0f, -2.0f, 2.0f, 1.0f, 2.0f, Dilation(0.0f)),
                ModelTransform.pivot(-0.5f, 3.0f, 0.5f)
            )

            val backrighttoe = rightfoot.addChild(
                "backrighttoe",
                ModelPartBuilder.create().uv(15, 22)
                    .cuboid(0.0f, -0.5f, 0.0f, 0.0f, 1.0f, 2.0f, Dilation(0.02f)),
                ModelTransform.pivot(0.5f, -0.5f, 0.0f)
            )

            val frontrighttoe = rightfoot.addChild(
                "frontrighttoe",
                ModelPartBuilder.create().uv(0, 21)
                    .cuboid(0.0f, -0.5f, -3.0f, 0.0f, 1.0f, 3.0f, Dilation(0.02f)),
                ModelTransform.pivot(1.25f, -0.5f, -2.0f)
            )

            val frontrighttoe2 = rightfoot.addChild(
                "frontrighttoe2",
                ModelPartBuilder.create().uv(6, 20)
                    .cuboid(0.0f, -0.5f, -3.0f, 0.0f, 1.0f, 3.0f, Dilation(0.0f)),
                ModelTransform.pivot(-0.25f, -0.5f, -2.0f)
            )

            return TexturedModelData.of(meshdefinition, 64, 64)
        }
    }
}