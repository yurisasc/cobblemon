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
import net.minecraft.world.phys.Vec3


class PidgeyModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame, BipedFrame, BiWingedFrame{
    override val rootPart = registerRelevantPart("pidgey", root.getChild("pidgey"))
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

    override val portraitScale = 1.95F
    override val portraitTranslation = Vec3(-0.05, -0.5, 0.0)

    companion object {
        val LAYER_LOCATION = ModelLayerLocation(cobbledResource("pidgey"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root

            val pidgey =
                partdefinition.addOrReplaceChild("pidgey", CubeListBuilder.create(), PartPose.offset(0.0f, 24.0f, 0.0f))

            val body =
                pidgey.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0f, -5.3706f, -0.267f))

            val body_r1 = body.addOrReplaceChild(
                "body_r1",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-2.0f, -2.5f, -3.5f, 5.0f, 5.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-0.5f, -0.1294f, -0.483f, -0.2618f, 0.0f, 0.0f)
            )

            val head = body.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(16, 13)
                    .addBox(-2.0f, -6.5f, -2.0f, 4.0f, 7.0f, 4.0f, CubeDeformation(0.0f))
                    .texOffs(0, 0).addBox(-1.0f, -5.25f, -4.0f, 2.0f, 2.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -0.3794f, -2.233f)
            )

            val head_r1 = head.addOrReplaceChild(
                "head_r1",
                CubeListBuilder.create().texOffs(21, 5)
                    .addBox(-2.5f, 0.0f, 0.0f, 5.0f, 0.0f, 5.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(0.0f, -6.5f, -2.0f, 0.2618f, 0.0f, 0.0f)
            )

            val tail = body.addOrReplaceChild(
                "tail",
                CubeListBuilder.create().texOffs(13, 0)
                    .addBox(-3.0f, 0.0f, 0.0f, 6.0f, 0.0f, 5.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(0.0f, 1.8706f, 3.517f, 0.1309f, 0.0f, 0.0f)
            )

            val leftwing = body.addOrReplaceChild(
                "leftwing",
                CubeListBuilder.create().texOffs(0, 5)
                    .addBox(0.0f, -2.5f, 0.0f, 0.0f, 5.0f, 8.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(2.5f, -1.1294f, -2.983f, -0.4378f, 0.0791f, -0.037f)
            )

            val rightwing = body.addOrReplaceChild(
                "rightwing",
                CubeListBuilder.create().texOffs(0, 10)
                    .addBox(0.0f, -2.5f, 0.0f, 0.0f, 5.0f, 8.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(-2.5f, -1.1294f, -2.983f, -0.4378f, -0.0791f, 0.037f)
            )

            val leftleg = body.addOrReplaceChild(
                "leftleg",
                CubeListBuilder.create().texOffs(16, 13)
                    .addBox(-0.5f, -1.0f, -0.5f, 1.0f, 3.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(1.5f, 2.3706f, 0.767f)
            )

            val leftfoot = leftleg.addOrReplaceChild(
                "leftfoot",
                CubeListBuilder.create().texOffs(0, 4)
                    .addBox(-1.5f, -1.0f, -2.0f, 2.0f, 1.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.5f, 3.0f, 0.5f)
            )

            val backlefttoe = leftfoot.addOrReplaceChild(
                "backlefttoe",
                CubeListBuilder.create().texOffs(6, 22)
                    .addBox(0.0f, -0.5f, 0.0f, 0.0f, 1.0f, 2.0f, CubeDeformation(0.02f)),
                PartPose.offset(-0.5f, -0.5f, 0.0f)
            )

            val frontlefttoe3 = leftfoot.addOrReplaceChild(
                "frontlefttoe3",
                CubeListBuilder.create().texOffs(0, 20)
                    .addBox(0.0f, -0.5f, -3.0f, 0.0f, 1.0f, 3.0f, CubeDeformation(0.02f)),
                PartPose.offset(-1.25f, -0.5f, -2.0f)
            )

            val frontlefttoe4 = leftfoot.addOrReplaceChild(
                "frontlefttoe4",
                CubeListBuilder.create().texOffs(0, 4)
                    .addBox(0.0f, -0.5f, -3.0f, 0.0f, 1.0f, 3.0f, CubeDeformation(0.02f)),
                PartPose.offset(0.25f, -0.5f, -2.0f)
            )

            val rightleg = body.addOrReplaceChild(
                "rightleg",
                CubeListBuilder.create().texOffs(11, 23)
                    .addBox(-0.5f, -1.0f, -0.5f, 1.0f, 3.0f, 1.0f, CubeDeformation(0.0f)),
                PartPose.offset(-1.5f, 2.3706f, 0.767f)
            )

            val rightfoot = rightleg.addOrReplaceChild(
                "rightfoot",
                CubeListBuilder.create().texOffs(18, 5)
                    .addBox(-0.5f, -1.0f, -2.0f, 2.0f, 1.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(-0.5f, 3.0f, 0.5f)
            )

            val backrighttoe = rightfoot.addOrReplaceChild(
                "backrighttoe",
                CubeListBuilder.create().texOffs(15, 22)
                    .addBox(0.0f, -0.5f, 0.0f, 0.0f, 1.0f, 2.0f, CubeDeformation(0.02f)),
                PartPose.offset(0.5f, -0.5f, 0.0f)
            )

            val frontrighttoe = rightfoot.addOrReplaceChild(
                "frontrighttoe",
                CubeListBuilder.create().texOffs(0, 21)
                    .addBox(0.0f, -0.5f, -3.0f, 0.0f, 1.0f, 3.0f, CubeDeformation(0.02f)),
                PartPose.offset(1.25f, -0.5f, -2.0f)
            )

            val frontrighttoe2 = rightfoot.addOrReplaceChild(
                "frontrighttoe2",
                CubeListBuilder.create().texOffs(6, 20)
                    .addBox(0.0f, -0.5f, -3.0f, 0.0f, 1.0f, 3.0f, CubeDeformation(0.0f)),
                PartPose.offset(-0.25f, -0.5f, -2.0f)
            )

            return LayerDefinition.create(meshdefinition, 64, 64)
        }
    }
}