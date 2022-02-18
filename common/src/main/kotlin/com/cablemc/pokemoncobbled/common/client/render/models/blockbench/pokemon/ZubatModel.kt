package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.EarJoint
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.RangeOfMotion
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.asTransformed
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.BiWingedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.EaredFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.getChildOf
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.PoseType
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.TransformedModelPart.Companion.X_AXIS
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.TransformedModelPart.Companion.Y_AXIS
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.TransformedModelPart.Companion.Z_AXIS
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.wavefunction.sineFunction
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.wavefunction.triangleFunction
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.cablemc.pokemoncobbled.common.util.math.geometry.toRadians
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.util.Mth.PI


class ZubatModel(root: ModelPart) : PokemonPoseableModel(), BiWingedFrame, EaredFrame {
    override val rootPart: ModelPart = registerRelevantPart("zubat", root.getChild("zubat"))
    override val leftWing = rootPart.getChildOf("body", "leftwing")
    override val rightWing = rootPart.getChildOf("body", "rightwing")
    private val leftEar = registerRelevantPart("leftear", rootPart.getChildOf("body", "leftear"))
    private val rightEar = registerRelevantPart("rightear", rootPart.getChildOf("body", "rightear"))
    override val leftEarJoint = EarJoint(leftEar, Z_AXIS, RangeOfMotion(70F.toRadians(), 40F.toRadians()))
    override val rightEarJoint = EarJoint(rightEar, Z_AXIS, RangeOfMotion((-70F).toRadians(), (-40F).toRadians()))

    override fun registerPoses() {
        registerPose(
            poseType = PoseType.WALK,
            idleAnimations = arrayOf(
                rootPart.translation(
                    function = sineFunction(
                        amplitude = 2.5F,
                        period = 1F
                    ),
                    timeVariable = { state, _, _ -> state?.animationSeconds },
                    axis = Y_AXIS
                ),
                rootPart.translation(
                    function = sineFunction(
                        amplitude = 2.5F,
                        period = 2F
                    ),
                    timeVariable = { state, _, _ -> state?.animationSeconds },
                    axis = X_AXIS
                ),
                wingFlap(
                    flapFunction = triangleFunction(
                        amplitude = PI / 3,
                        period = 0.3F
                    ),
                    timeVariable = { state, _, _ -> state?.animationSeconds },
                    axis = Z_AXIS
                )
            ),
            transformedParts = arrayOf(
                rootPart.asTransformed().addRotation(X_AXIS, PI / 9),
                leftWing.asTransformed().addRotation(X_AXIS, PI / 3),
                rightWing.asTransformed().addRotation(X_AXIS, PI / 3)
            )
        )

        registerPose(
            poseType = PoseType.SHOULDER_LEFT,
            idleAnimations = arrayOf(
                leftWing.rotation(
                    function = sineFunction(
                        amplitude = PI / 3,
                        period = 1F
                    ),
                    axis = Z_AXIS,
                    timeVariable = { _, _, ageInTicks -> ageInTicks / 20 },
                )
            ),
            transformedParts = arrayOf(
                rootPart.asTransformed().addRotation(X_AXIS, PI / 9).addPosition(Y_AXIS, 4F).addPosition(Z_AXIS, 3F),
                leftWing.asTransformed().addRotation(X_AXIS, PI / 3),
                rightWing.asTransformed().addRotation(X_AXIS, PI / 3).addRotation(Z_AXIS, -PI / 2)
            )
        )
        registerPose(
            poseType = PoseType.SHOULDER_RIGHT,
            idleAnimations = arrayOf(
                rightWing.rotation(
                    function = sineFunction(
                        amplitude = PI / 3,
                        period = 1F
                    ),
                    axis = Z_AXIS,
                    timeVariable = { _, _, ageInTicks -> ageInTicks / 20 },
                )
            ),
            transformedParts = arrayOf(
                rootPart.asTransformed().addRotation(X_AXIS, PI / 9).addPosition(Y_AXIS, 4F).addPosition(Z_AXIS, 3F),
                leftWing.asTransformed().addRotation(X_AXIS, PI / 3).addRotation(Z_AXIS, PI / 2),
                rightWing.asTransformed().addRotation(X_AXIS, PI / 3)
            )
        )
    }

    companion object {
        
        val LAYER_LOCATION = ModelLayerLocation(cobbledResource("zubat"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshDefinition = MeshDefinition()
            val partDefinition = meshDefinition.root

            val zubat = partDefinition.addOrReplaceChild("zubat", CubeListBuilder.create(), PartPose.offset(0.0f, 24.0f, 0.0f))

            val body = zubat.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(0, 16)
                    .addBox(-2.5f, -8.0f, -1.5f, 5.0f, 7.0f, 3.0f, CubeDeformation(0.0f))
                    .texOffs(24, 29).addBox(-2.0f, -7.5f, -1.55f, 4.0f, 3.0f, 0.0f, CubeDeformation(0.02f)),
                PartPose.offset(0.0f, -7.0f, 0.0f)
            )

            val cube_r1 = body.addOrReplaceChild(
                "cube_r1",
                CubeListBuilder.create().texOffs(22, 7)
                    .addBox(-0.5f, -3.5f, 0.0f, 1.0f, 7.0f, 0.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(1.75f, 2.25f, 0.0f, 0.0f, 0.0f, -0.2618f)
            )

            val cube_r2 = body.addOrReplaceChild(
                "cube_r2",
                CubeListBuilder.create().texOffs(22, 0)
                    .addBox(-0.5f, -3.5f, 0.0f, 1.0f, 7.0f, 0.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(-1.75f, 2.25f, 0.0f, 0.0f, 0.0f, 0.2618f)
            )

            val rightear = body.addOrReplaceChild(
                "rightear",
                CubeListBuilder.create().texOffs(16, 20)
                    .addBox(-1.5f, -4.0f, 0.0f, 3.0f, 4.0f, 0.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(-1.5f, -7.25f, 0.0f, 0.0f, 0.0f, -0.6981f)
            )

            val leftear = body.addOrReplaceChild(
                "leftear",
                CubeListBuilder.create().texOffs(16, 16)
                    .addBox(-1.5f, -4.0f, 0.0f, 3.0f, 4.0f, 0.0f, CubeDeformation(0.02f)),
                PartPose.offsetAndRotation(1.5f, -7.25f, 0.0f, 0.0f, 0.0f, 0.6981f)
            )

            val rightwing = body.addOrReplaceChild(
                "rightwing",
                CubeListBuilder.create().texOffs(0, 8)
                    .addBox(-11.0f, -6.0f, 0.0f, 11.0f, 8.0f, 0.0f, CubeDeformation(0.02f)),
                PartPose.offset(-2.5f, -3.0f, 0.0f)
            )

            val leftwing = body.addOrReplaceChild(
                "leftwing",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(0.0f, -6.0f, 0.0f, 11.0f, 8.0f, 0.0f, CubeDeformation(0.02f)),
                PartPose.offset(2.5f, -3.0f, 0.0f)
            )

            return LayerDefinition.create(meshDefinition, 32, 32)
        }
    }
}