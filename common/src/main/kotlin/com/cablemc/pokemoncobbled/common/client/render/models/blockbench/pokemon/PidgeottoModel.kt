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
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition


class PidgeottoModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame, BipedFrame, BiWingedFrame {
    override val rootPart = registerRelevantPart("pidgeotto", root.getChild("pidgeotto"))
    override val leftWing = registerRelevantPart("leftwing", rootPart.getChildOf("body","leftwing"))
    override val rightWing = registerRelevantPart("rightwing", rootPart.getChildOf("body","rightwing"))
    override val leftLeg = registerRelevantPart("leftleg", rootPart.getChildOf("body","leftleg"))
    override val rightLeg = registerRelevantPart("rightleg", rootPart.getChildOf("body","rightleg"))
    override val head = registerRelevantPart("head", rootPart.getChildOf("body","head"))
    private val tail = registerRelevantPart("tail", rootPart.getChildOf("body","tail"))

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
        val LAYER_LOCATION = ModelLayerLocation(cobbledResource("pidgeotto"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root

            val pidgeotto = partdefinition.addOrReplaceChild(
                "pidgeotto",
                CubeListBuilder.create(),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )

            val body =
                pidgeotto.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0f, -7.1635f, -0.7418f))

            val body_r1 = body.addOrReplaceChild(
                "body_r1",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-2.5f, -1.5f, -5.0f, 6.0f, 5.0f, 10.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-0.5f, -0.8365f, 0.7418f, -0.2618f, 0.0f, 0.0f)
            )

            val tail = body.addOrReplaceChild(
                "tail",
                CubeListBuilder.create().texOffs(14, 0)
                    .addBox(-3.5f, 0.0f, 0.0f, 7.0f, 0.0f, 8.0f, CubeDeformation(0.02f)),
                PartPose.offset(0.0f, 2.4135f, 4.9918f)
            )

            val head = body.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(20, 15)
                    .addBox(-2.5f, -8.0f, -2.5f, 5.0f, 9.0f, 5.0f, CubeDeformation(0.0f))
                    .texOffs(32, 8).addBox(-1.0f, -5.75f, -4.5f, 2.0f, 2.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -0.3365f, -2.5082f)
            )

            val body_r2 = head.addOrReplaceChild(
                "body_r2",
                CubeListBuilder.create().texOffs(0, 27)
                    .addBox(-3.0f, -3.0f, 0.0f, 7.0f, 5.0f, 0.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(-0.5f, -8.2739f, 2.8572f, -2.0944f, 0.0f, 0.0f)
            )

            val body_r3 = head.addOrReplaceChild(
                "body_r3",
                CubeListBuilder.create().texOffs(30, 29)
                    .addBox(-3.0f, -2.0f, 0.0f, 6.0f, 4.0f, 0.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(0.0f, -8.7562f, -0.8067f, -1.309f, 0.0f, 0.0f)
            )

            val body_r4 = head.addOrReplaceChild(
                "body_r4",
                CubeListBuilder.create().texOffs(0, 32)
                    .addBox(-2.5f, -1.5f, 0.0f, 5.0f, 3.0f, 0.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(0.0f, -6.7443f, -2.8693f, -0.0873f, 0.0f, 0.0f)
            )

            val rightwing = body.addOrReplaceChild(
                "rightwing",
                CubeListBuilder.create().texOffs(0, 11)
                    .addBox(0.0f, -2.5f, 0.0f, 0.0f, 6.0f, 10.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(-3.0f, -1.5865f, -3.5082f, -0.3491f, -0.1222f, 0.0436f)
            )

            val leftwing = body.addOrReplaceChild(
                "leftwing",
                CubeListBuilder.create().texOffs(0, 5)
                    .addBox(0.0f, -2.5f, 0.0f, 0.0f, 6.0f, 10.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(3.0f, -1.5865f, -3.5082f, -0.3491f, 0.1222f, -0.0436f)
            )

            val leftleg = body.addOrReplaceChild(
                "leftleg",
                CubeListBuilder.create().texOffs(10, 28)
                    .addBox(-1.0f, -0.75f, -2.0f, 2.0f, 3.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(2.25f, 1.9135f, 1.2418f)
            )

            val leftknee = leftleg.addOrReplaceChild(
                "leftknee",
                CubeListBuilder.create().texOffs(20, 15)
                    .addBox(-0.5f, -1.0f, -0.5f, 1.0f, 3.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 2.25f, 0.5f)
            )

            val leftfoot = leftknee.addOrReplaceChild(
                "leftfoot",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-1.0f, 0.0f, -2.0f, 2.0f, 1.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 2.0f, 0.0f)
            )

            val leftbacktoe = leftfoot.addOrReplaceChild(
                "leftbacktoe",
                CubeListBuilder.create().texOffs(6, 6)
                    .addBox(0.0f, -0.5f, 0.0f, 0.0f, 1.0f, 2.0f, CubeDeformation(0.02f)),
                PartPose.offset(0.0f, 0.5f, 1.0f)
            )

            val leftfronttoe3 = leftfoot.addOrReplaceChild(
                "leftfronttoe3",
                CubeListBuilder.create().texOffs(0, 6)
                    .addBox(0.0f, -0.5f, -3.0f, 0.0f, 1.0f, 3.0f, CubeDeformation(0.02f)),
                PartPose.offset(0.75f, 0.5f, -2.0f)
            )

            val leftfronttoe4 = leftfoot.addOrReplaceChild(
                "leftfronttoe4",
                CubeListBuilder.create().texOffs(0, 5)
                    .addBox(0.0f, -0.5f, -3.0f, 0.0f, 1.0f, 3.0f, CubeDeformation(0.02f)),
                PartPose.offset(-0.75f, 0.5f, -2.0f)
            )

            val rightleg = body.addOrReplaceChild(
                "rightleg",
                CubeListBuilder.create().texOffs(22, 29)
                    .addBox(-1.0f, -0.75f, -2.0f, 2.0f, 3.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(-2.25f, 1.9135f, 1.2418f)
            )

            val rightknee = rightleg.addOrReplaceChild(
                "rightknee",
                CubeListBuilder.create().texOffs(22, 29)
                    .addBox(-0.5f, -1.0f, -0.5f, 1.0f, 3.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 2.25f, 0.5f)
            )

            val rightfoot = rightknee.addOrReplaceChild(
                "rightfoot",
                CubeListBuilder.create().texOffs(0, 4)
                    .addBox(-1.0f, 0.0f, -2.0f, 2.0f, 1.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 2.0f, 0.0f)
            )

            val rightbacktoe = rightfoot.addOrReplaceChild(
                "rightbacktoe",
                CubeListBuilder.create().texOffs(6, 7)
                    .addBox(0.0f, -0.5f, 0.0f, 0.0f, 1.0f, 2.0f, CubeDeformation(0.02f)),
                PartPose.offset(0.0f, 0.5f, 1.0f)
            )

            val rightfronttoe = rightfoot.addOrReplaceChild(
                "rightfronttoe",
                CubeListBuilder.create().texOffs(22, 6)
                    .addBox(0.0f, -0.5f, -3.0f, 0.0f, 1.0f, 3.0f, CubeDeformation(0.02f)),
                PartPose.offset(-0.75f, 0.5f, -2.0f)
            )

            val rightfronttoe2 = rightfoot.addOrReplaceChild(
                "rightfronttoe2",
                CubeListBuilder.create().texOffs(22, 5)
                    .addBox(0.0f, -0.5f, -3.0f, 0.0f, 1.0f, 3.0f, CubeDeformation(0.02f)),
                PartPose.offset(0.75f, 0.5f, -2.0f)
            )

            return LayerDefinition.create(meshdefinition, 64, 64)
        }
    }
}