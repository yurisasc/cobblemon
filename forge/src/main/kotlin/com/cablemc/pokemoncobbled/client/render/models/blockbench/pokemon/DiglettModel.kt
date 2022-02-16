package com.cablemc.pokemoncobbled.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.client.render.models.blockbench.getChildOf
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pose.PoseType
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pose.TransformedModelPart
import com.cablemc.pokemoncobbled.client.render.models.blockbench.wavefunction.sineFunction
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition


class DiglettModel(root: ModelPart) : PokemonPoseableModel() {
    override val rootPart: ModelPart = registerRelevantPart("diglett", root.getChild("diglett"))
    private val body: ModelPart = registerRelevantPart("body", rootPart.getChildOf("body"))

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
            ),
            transformedParts = arrayOf()
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
            ),
            transformedParts = emptyArray()
        )
    }

    companion object {
        val LAYER_LOCATION = ModelLayerLocation(cobbledResource("diglett"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root
            val diglett = partdefinition.addOrReplaceChild(
                "diglett",
                CubeListBuilder.create(),
                PartPose.offset(0.0f, 23.0f, 0.0f)
            )
            val body = diglett.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(0, 18)
                    .addBox(-2.0f, -1.5f, -2.5f, 4.0f, 2.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(0, 0).addBox(-3.5f, -5.0f, -1.5f, 7.0f, 11.0f, 7.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -5.25f, -2.0f)
            )
            val eyes = body.addOrReplaceChild(
                "eyes",
                CubeListBuilder.create().texOffs(0, 0).mirror()
                    .addBox(1.25f, -1.0f, 0.0f, 1.0f, 2.0f, 0.0f, CubeDeformation(0.0f)).mirror(false)
                    .texOffs(0, 0).addBox(-2.25f, -1.0f, 0.0f, 1.0f, 2.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -2.75f, -1.55f)
            )
            val roc = diglett.addOrReplaceChild(
                "roc",
                CubeListBuilder.create().texOffs(0, 21)
                    .addBox(-5.0f, -1.0f, -4.5f, 10.0f, 6.0f, 9.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, -0.25f)
            )
            return LayerDefinition.create(meshdefinition, 48, 48)
        }
    }
}