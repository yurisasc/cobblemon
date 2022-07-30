package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.getChildOf
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.PoseType
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.TransformedModelPart
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.wavefunction.sineFunction
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.client.render.entity.model.EntityModelLayer
import net.minecraft.client.model.ModelPart
import net.minecraft.client.model.ModelTransform
import net.minecraft.client.model.Dilation
import net.minecraft.client.model.ModelPartBuilder
import net.minecraft.client.model.TexturedModelData
import net.minecraft.client.model.ModelData
import net.minecraft.util.math.Vec3d

class DiglettModel(root: ModelPart) : PokemonPoseableModel() {
    override val rootPart: ModelPart = registerRelevantPart("diglett", root.getChild("diglett"))
    private val body: ModelPart = registerRelevantPart("body", rootPart.getChildOf("body"))

    override val portraitScale = 1.65F
    override val portraitTranslation = Vec3d(0.15, -0.7, 0.0)

    override val profileScale = 1.0F
    override val profileTranslation = Vec3d(0.0, 0.0, 0.0)

    override fun registerPoses() {
        registerPose(
            poseType = PoseType.NONE,
            condition = { it.isMoving.get() },
            transformTicks = 0,
            idleAnimations = arrayOf(
                body.translation(
                    function = sineFunction(
                        amplitude = -1F,
                        period = 0.6F,
                        verticalShift = 0.5F
                    ),
                    axis = TransformedModelPart.Y_AXIS,
                    timeVariable = { state, _, _ -> state?.animationSeconds }
                )
            )
        )

        registerPose(
            poseType = PoseType.WALK,
            condition = { !it.isMoving.get() },
            transformTicks = 0,
            idleAnimations = arrayOf(
                body.translation(
                    function = sineFunction(
                        amplitude = -1F,
                        period = 1F,
                        verticalShift = 0.5F
                    ),
                    axis = TransformedModelPart.Y_AXIS,
                    timeVariable = { state, _, _ -> state?.animationSeconds }
                )
            )
        )
    }

    companion object {
        val LAYER_LOCATION = EntityModelLayer(cobbledResource("diglett"), "main")
        fun createBodyLayer(): TexturedModelData {
            val meshdefinition = ModelData()
            val partdefinition = meshdefinition.root
            val diglett = partdefinition.addChild(
                "diglett",
                ModelPartBuilder.create(),
                ModelTransform.pivot(0.0f, 23.0f, 0.0f)
            )
            val body = diglett.addChild(
                "body",
                ModelPartBuilder.create().uv(0, 18)
                    .cuboid(-2.0f, -1.5f, -2.5f, 4.0f, 2.0f, 1.0f, Dilation(0.0f))
                    .uv(0, 0).cuboid(-3.5f, -5.0f, -1.5f, 7.0f, 11.0f, 7.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, -5.25f, -2.0f)
            )
            val eyes = body.addChild(
                "eyes",
                ModelPartBuilder.create().uv(0, 0).mirrored()
                    .cuboid(1.25f, -1.0f, 0.0f, 1.0f, 2.0f, 0.0f, Dilation(0.0f)).mirrored(false)
                    .uv(0, 0).cuboid(-2.25f, -1.0f, 0.0f, 1.0f, 2.0f, 0.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, -2.75f, -1.55f)
            )
            val roc = diglett.addChild(
                "roc",
                ModelPartBuilder.create().uv(0, 21)
                    .cuboid(-5.0f, -1.0f, -4.5f, 10.0f, 6.0f, 9.0f, Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 0.0f, -0.25f)
            )
            return TexturedModelData.of(meshdefinition, 48, 48)
        }
    }
}