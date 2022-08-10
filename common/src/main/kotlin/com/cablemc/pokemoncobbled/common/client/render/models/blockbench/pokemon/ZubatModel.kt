package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.EarJoint
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.RangeOfMotion
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.asTransformed
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.BiWingedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.EaredFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.getChildOf
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.TransformedModelPart.Companion.X_AXIS
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.TransformedModelPart.Companion.Y_AXIS
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.TransformedModelPart.Companion.Z_AXIS
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.wavefunction.sineFunction
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.wavefunction.triangleFunction
import com.cablemc.pokemoncobbled.common.entity.PoseType
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import com.cablemc.pokemoncobbled.common.util.math.geometry.toRadians
import net.minecraft.client.model.Dilation
import net.minecraft.client.model.ModelData
import net.minecraft.client.model.ModelPart
import net.minecraft.client.model.ModelPartBuilder
import net.minecraft.client.model.ModelTransform
import net.minecraft.client.model.TexturedModelData
import net.minecraft.client.render.entity.model.EntityModelLayer
import net.minecraft.util.math.MathConstants.PI
import net.minecraft.util.math.Vec3d

class ZubatModel(root: ModelPart) : PokemonPoseableModel(), BiWingedFrame, EaredFrame {
    override val rootPart: ModelPart = registerRelevantPart("zubat", root.getChild("zubat"))
    override val leftWing = rootPart.getChildOf("body", "leftwing")
    override val rightWing = rootPart.getChildOf("body", "rightwing")
    private val leftEar = registerRelevantPart("leftear", rootPart.getChildOf("body", "leftear"))
    private val rightEar = registerRelevantPart("rightear", rootPart.getChildOf("body", "rightear"))
    override val leftEarJoint = EarJoint(leftEar, Z_AXIS, RangeOfMotion(70F.toRadians(), 40F.toRadians()))
    override val rightEarJoint = EarJoint(rightEar, Z_AXIS, RangeOfMotion((-70F).toRadians(), (-40F).toRadians()))

    override val portraitScale = 2.05F
    override val portraitTranslation = Vec3d(-0.22, -0.75, 0.0)
    override val profileScale = 1.0F
    override val profileTranslation = Vec3d(0.0, 0.0, 0.0)

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
        
        val LAYER_LOCATION = EntityModelLayer(cobbledResource("zubat"), "main")
        fun createBodyLayer(): TexturedModelData {
            val meshDefinition = ModelData()
            val partDefinition = meshDefinition.root

            val zubat = partDefinition.addChild("zubat", ModelPartBuilder.create(), ModelTransform.pivot(0.0f, 24.0f, 0.0f))

            val body = zubat.addChild(
                "body",
                ModelPartBuilder.create().uv(0, 16)
                    .cuboid(-2.5f, -8.0f, -1.5f, 5.0f, 7.0f, 3.0f, Dilation(0.0f))
                    .uv(24, 29).cuboid(-2.0f, -7.5f, -1.55f, 4.0f, 3.0f, 0.0f, Dilation(0.02f)),
                ModelTransform.pivot(0.0f, -7.0f, 0.0f)
            )

            val cube_r1 = body.addChild(
                "cube_r1",
                ModelPartBuilder.create().uv(22, 7)
                    .cuboid(-0.5f, -3.5f, 0.0f, 1.0f, 7.0f, 0.0f, Dilation(0.02f)),
                ModelTransform.of(1.75f, 2.25f, 0.0f, 0.0f, 0.0f, -0.2618f)
            )

            val cube_r2 = body.addChild(
                "cube_r2",
                ModelPartBuilder.create().uv(22, 0)
                    .cuboid(-0.5f, -3.5f, 0.0f, 1.0f, 7.0f, 0.0f, Dilation(0.02f)),
                ModelTransform.of(-1.75f, 2.25f, 0.0f, 0.0f, 0.0f, 0.2618f)
            )

            val rightear = body.addChild(
                "rightear",
                ModelPartBuilder.create().uv(16, 20)
                    .cuboid(-1.5f, -4.0f, 0.0f, 3.0f, 4.0f, 0.0f, Dilation(0.02f)),
                ModelTransform.of(-1.5f, -7.25f, 0.0f, 0.0f, 0.0f, -0.6981f)
            )

            val leftear = body.addChild(
                "leftear",
                ModelPartBuilder.create().uv(16, 16)
                    .cuboid(-1.5f, -4.0f, 0.0f, 3.0f, 4.0f, 0.0f, Dilation(0.02f)),
                ModelTransform.of(1.5f, -7.25f, 0.0f, 0.0f, 0.0f, 0.6981f)
            )

            val rightwing = body.addChild(
                "rightwing",
                ModelPartBuilder.create().uv(0, 8)
                    .cuboid(-11.0f, -6.0f, 0.0f, 11.0f, 8.0f, 0.0f, Dilation(0.02f)),
                ModelTransform.pivot(-2.5f, -3.0f, 0.0f)
            )

            val leftwing = body.addChild(
                "leftwing",
                ModelPartBuilder.create().uv(0, 0)
                    .cuboid(0.0f, -6.0f, 0.0f, 11.0f, 8.0f, 0.0f, Dilation(0.02f)),
                ModelTransform.pivot(2.5f, -3.0f, 0.0f)
            )

            return TexturedModelData.of(meshDefinition, 32, 32)
        }
    }
}