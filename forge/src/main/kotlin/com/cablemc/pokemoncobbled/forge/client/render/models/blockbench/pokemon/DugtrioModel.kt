package com.cablemc.pokemoncobbled.forge.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.forge.client.render.models.blockbench.getChildOf
import com.cablemc.pokemoncobbled.forge.client.render.models.blockbench.pose.PoseType
import com.cablemc.pokemoncobbled.forge.client.render.models.blockbench.pose.TransformedModelPart
import com.cablemc.pokemoncobbled.forge.client.render.models.blockbench.wavefunction.sineFunction
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition

class DugtrioModel(root: ModelPart) : PokemonPoseableModel() {
    override val rootPart: ModelPart = registerRelevantPart("dugtrio", root.getChild("dugtrio"))
    private val body1: ModelPart = registerRelevantPart("body1", rootPart.getChildOf("body"))
    private val body2: ModelPart = registerRelevantPart("body2", rootPart.getChildOf("body2"))
    private val body3: ModelPart = registerRelevantPart("body3", rootPart.getChildOf("body3"))

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
            ),
            transformedParts = emptyArray()
        )
    }

    companion object {
        val LAYER_LOCATION = ModelLayerLocation(cobbledResource("dugtrio"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root
            val diglett = partdefinition.addOrReplaceChild(
                "dugtrio",
                CubeListBuilder.create(),
                PartPose.offset(0.0f, 23.0f, 0.0f)
            )
            val body = diglett.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(0, 18)
                    .addBox(-2.0f, -1.5f, -2.5f, 4.0f, 2.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(0, 0).addBox(-3.5f, -5.0f, -1.5f, 7.0f, 11.0f, 7.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-4.0f, -5.25f, -5.25f, 0.0f, -0.2182f, 0.0f)
            )
            val eyes = body.addOrReplaceChild(
                "eyes",
                CubeListBuilder.create().texOffs(0, 0).mirror()
                    .addBox(1.25f, -1.0f, 0.0f, 1.0f, 2.0f, 0.0f, CubeDeformation(0.0f)).mirror(false)
                    .texOffs(0, 0).addBox(-2.25f, -1.0f, 0.0f, 1.0f, 2.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -2.75f, -1.55f)
            )
            val body2 = diglett.addOrReplaceChild(
                "body2",
                CubeListBuilder.create().texOffs(0, 18)
                    .addBox(-2.0f, -1.5f, -2.5f, 4.0f, 2.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(0, 0).addBox(-3.5f, -5.0f, -1.5f, 7.0f, 11.0f, 7.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(4.5f, -3.25f, -6f, 0.0f, 0.0873f, 0.1309f)
            )
            val eyes2 = body2.addOrReplaceChild(
                "eyes2",
                CubeListBuilder.create().texOffs(0, 0).mirror()
                    .addBox(1.25f, -1.0f, 0.0f, 1.0f, 2.0f, 0.0f, CubeDeformation(0.0f)).mirror(false)
                    .texOffs(0, 0).addBox(-2.25f, -1.0f, 0.0f, 1.0f, 2.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -2.75f, -1.55f)
            )
            val body3 = diglett.addOrReplaceChild(
                "body3",
                CubeListBuilder.create().texOffs(0, 18)
                    .addBox(-2.0f, -1.75f, -2.5f, 4.0f, 2.0f, 1.0f, CubeDeformation(0.0f))
                    .texOffs(36, 42).addBox(-3.5f, -5.0f, -1.5f, 7.0f, 15.0f, 7.0f, CubeDeformation(0.0f)),
                PartPose.offset(1.0f, -10.5f, 2.5f)
            )
            val eyes3 = body3.addOrReplaceChild(
                "eyes3",
                CubeListBuilder.create().texOffs(0, 0).mirror()
                    .addBox(1.25f, -1.0f, 0.0f, 1.0f, 2.0f, 0.0f, CubeDeformation(0.0f)).mirror(false)
                    .texOffs(0, 0).addBox(-2.25f, -1.0f, 0.0f, 1.0f, 2.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -3.0f, -1.55f)
            )
            val roc = diglett.addOrReplaceChild(
                "roc",
                CubeListBuilder.create().texOffs(0, 18)
                    .addBox(-9.0f, -1.0f, -8.5f, 18.0f, 5.0f, 17.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.0f, 0.0f)
            )
            return LayerDefinition.create(meshdefinition, 74, 64)
        }
    }
}