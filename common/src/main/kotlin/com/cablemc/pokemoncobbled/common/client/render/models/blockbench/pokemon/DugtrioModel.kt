package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.getChildOf
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.PoseType
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.TransformedModelPart
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.wavefunction.sineFunction
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.client.model.*
import net.minecraft.client.render.entity.model.EntityModelLayer
import net.minecraft.util.math.Vec3d

class DugtrioModel(root: ModelPart) : PokemonPoseableModel() {
    override val rootPart: ModelPart = registerRelevantPart("dugtrio", root.getChild("dugtrio"))
    private val body1: ModelPart = registerRelevantPart("body1", rootPart.getChildOf("body"))
    private val body2: ModelPart = registerRelevantPart("body2", rootPart.getChildOf("body2"))
    private val body3: ModelPart = registerRelevantPart("body3", rootPart.getChildOf("body3"))

    override val portraitScale = 1.7F
    override val portraitTranslation = Vec3d(-0.2, -0.7, 0.0)

    override val profileScale = 1.0F
    override val profileTranslation = Vec3d(0.0, 0.0, 0.0)

    override fun registerPoses() {
        registerPose(
            poseType = PoseType.NONE,
            condition = { true },
            idleAnimations = arrayOf(
                body1.translation(
                    function = sineFunction(
                        amplitude = -2F,
                        period = 1.4F,
                        verticalShift = 1F
                    ),
                    axis = TransformedModelPart.Y_AXIS,
                    timeVariable = { state, _, _ -> state?.animationSeconds }
                ),
                body2.translation(
                    function = sineFunction(
                        amplitude = 1F,
                        period = 1.2F,
                        phaseShift = 0.5F,
                        verticalShift = 0F
                    ),
                    axis = TransformedModelPart.Y_AXIS,
                    timeVariable = { state, _, _ -> state?.animationSeconds }
                ),
                body3.translation(
                    function = sineFunction(
                        amplitude = -1.5F,
                        period = 1F,
                        verticalShift = 2F
                    ),
                    axis = TransformedModelPart.Y_AXIS,
                    timeVariable = { state, _, _ -> state?.animationSeconds }
                )
            )
        )
    }

    companion object {
        val LAYER_LOCATION = EntityModelLayer(cobbledResource("dugtrio"), "main")
        fun createBodyLayer(): TexturedModelData {
            val meshdefinition = ModelData()
            val partdefinition = meshdefinition.root
            val diglett = partdefinition.addChild(
                "dugtrio",
                ModelPartBuilder.create(),
                ModelTransform.pivot(0.0f, 23.0f, 0.0f)
            )
            val body = diglett.addChild(
                "body",
                ModelPartBuilder.create().uv(0, 18)
                    .cuboid(-2.0f, -1.5f, -2.5f, 4.0f, 2.0f, 1.0f, Dilation(0.0f))
                    .uv(0, 0).cuboid(-3.5f, -5.0f, -1.5f, 7.0f, 11.0f, 7.0f, Dilation(0.0f)),
                ModelTransform.of(-4.0f, -5.25f, -5.25f, 0.0f, -0.2182f, 0.0f)
            )
            val eyes = body.addChild(
                "eyes",
                ModelPartBuilder.create().uv(0, 0).mirrored()
                    .cuboid(1.25f, -1.0f, 0.0f, 1.0f, 2.0f, 0.0f, Dilation(0.0f)).mirrored(false)
                    .uv(0, 0).cuboid(-2.25f, -1.0f, 0.0f, 1.0f, 2.0f, 0.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, -2.75f, -1.55f)
            )
            val body2 = diglett.addChild(
                "body2",
                ModelPartBuilder.create().uv(0, 18)
                    .cuboid(-2.0f, -1.5f, -2.5f, 4.0f, 2.0f, 1.0f, Dilation(0.0f))
                    .uv(0, 0).cuboid(-3.5f, -5.0f, -1.5f, 7.0f, 11.0f, 7.0f, Dilation(0.0f)),
                ModelTransform.of(4.5f, -3.25f, -6f, 0.0f, 0.0873f, 0.1309f)
            )
            val eyes2 = body2.addChild(
                "eyes2",
                ModelPartBuilder.create().uv(0, 0).mirrored()
                    .cuboid(1.25f, -1.0f, 0.0f, 1.0f, 2.0f, 0.0f, Dilation(0.0f)).mirrored(false)
                    .uv(0, 0).cuboid(-2.25f, -1.0f, 0.0f, 1.0f, 2.0f, 0.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, -2.75f, -1.55f)
            )
            val body3 = diglett.addChild(
                "body3",
                ModelPartBuilder.create().uv(0, 18)
                    .cuboid(-2.0f, -1.75f, -2.5f, 4.0f, 2.0f, 1.0f, Dilation(0.0f))
                    .uv(36, 42).cuboid(-3.5f, -5.0f, -1.5f, 7.0f, 15.0f, 7.0f, Dilation(0.0f)),
                ModelTransform.pivot(1.0f, -10.5f, 2.5f)
            )
            val eyes3 = body3.addChild(
                "eyes3",
                ModelPartBuilder.create().uv(0, 0).mirrored()
                    .cuboid(1.25f, -1.0f, 0.0f, 1.0f, 2.0f, 0.0f, Dilation(0.0f)).mirrored(false)
                    .uv(0, 0).cuboid(-2.25f, -1.0f, 0.0f, 1.0f, 2.0f, 0.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, -3.0f, -1.55f)
            )
            val roc = diglett.addChild(
                "roc",
                ModelPartBuilder.create().uv(0, 18)
                    .cuboid(-9.0f, -1.0f, -8.5f, 18.0f, 5.0f, 17.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 0.0f, 0.0f)
            )
            return TexturedModelData.of(meshdefinition, 74, 64)
        }
    }
}