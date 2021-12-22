package com.cablemc.pokemoncobbled.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.client.render.models.blockbench.animation.StatelessAnimation
import com.cablemc.pokemoncobbled.client.render.models.blockbench.frame.ModelFrame
import com.cablemc.pokemoncobbled.client.render.models.blockbench.getChildOf
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pose.PoseType
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pose.TransformedModelPart
import com.cablemc.pokemoncobbled.client.render.models.blockbench.wavefunction.sineFunction
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import com.cablemc.pokemoncobbled.common.util.math.geometry.toRadians
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.resources.ResourceLocation


class MagikarpModel(root: ModelPart) : PokemonPoseableModel() {
    override val rootPart: ModelPart = root.getChild("magikarp")
    private val tail = rootPart.getChildOf("body").getChildOf("tail")
    private val rightFlipper = rootPart.getChildOf("body").getChildOf("rightflipper")
    private val leftFlipper = rootPart.getChildOf("body").getChildOf("leftflipper")

    override fun registerPoses() {
        registerPose(
            poseType = PoseType.SWIM,
            condition = { it.isUnderWater },
            idleAnimations = arrayOf(
                tail.rotation(
                    function = sineFunction(
                        amplitude = 15f.toRadians(),
                        period = 7f
                    ),
                    axis = TransformedModelPart.Y_AXIS,
                    timeVariable = { state, _, _ -> state?.animationSeconds }
                ),
                rightFlipper.rotation(
                    function = sineFunction(
                        amplitude = 5f.toRadians(),
                        period = 7f,
                        verticalShift = (-15f).toRadians()
                    ),
                    axis = TransformedModelPart.Y_AXIS,
                    timeVariable = { state, _, _ -> state?.animationSeconds }
                ),
                leftFlipper.rotation(
                    function = sineFunction(
                        amplitude = (-5f).toRadians(),
                        period = 7f,
                        verticalShift = 15f.toRadians()
                    ),
                    axis = TransformedModelPart.Y_AXIS,
                    timeVariable = { state, _, _ -> state?.animationSeconds }
                )
            ),
            transformedParts = emptyArray()
        )
        registerPose(
            poseType = PoseType.WALK,
            condition = { true },
            idleAnimations = arrayOf<StatelessAnimation<PokemonEntity, out ModelFrame>>(),
            transformedParts = emptyArray()
        )
    }

    companion object {
        // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
        val LAYER_LOCATION = ModelLayerLocation(ResourceLocation("modid", "magikarp"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root
            val magikarp = partdefinition.addOrReplaceChild(
                "magikarp",
                CubeListBuilder.create(),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )
            val body = magikarp.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(18, 13)
                    .addBox(0.0f, -11.8333f, -4.0f, 0.0f, 8.0f, 7.0f, CubeDeformation(0.0f))
                    .texOffs(0, 0).addBox(-2.0f, -3.8333f, -6.25f, 4.0f, 9.0f, 11.0f, CubeDeformation(0.0f))
                    .texOffs(0, 0).addBox(0.0f, 5.1667f, -1.25f, 0.0f, 4.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -9.1667f, -0.25f)
            )
            val eyes = body.addOrReplaceChild(
                "eyes",
                CubeListBuilder.create().texOffs(46, 2)
                    .addBox(2.075f, -1.5f, -1.5f, 0.0f, 3.0f, 3.0f, CubeDeformation(0.0f))
                    .texOffs(46, 2).mirror().addBox(-2.075f, -1.5f, -1.5f, 0.0f, 3.0f, 3.0f, CubeDeformation(0.0f))
                    .mirror(false),
                PartPose.offset(0.0f, -1.0833f, -3.75f)
            )
            val tail = body.addOrReplaceChild(
                "tail",
                CubeListBuilder.create().texOffs(0, 11)
                    .addBox(0.0f, -7.5f, 0.0f, 0.0f, 15.0f, 9.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 0.6667f, 4.75f)
            )
            val rightmustache = body.addOrReplaceChild(
                "rightmustache",
                CubeListBuilder.create().texOffs(19, 1)
                    .addBox(-5.0f, -0.5f, 0.0f, 6.0f, 1.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offset(-2.0f, 1.4167f, -5.25f)
            )
            val rightmustachetip = rightmustache.addOrReplaceChild(
                "rightmustachetip",
                CubeListBuilder.create().texOffs(19, 0)
                    .addBox(-6.0f, -0.5f, 0.0f, 6.0f, 1.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offset(-5.0f, 0.0f, 0.0f)
            )
            val leftmustache = body.addOrReplaceChild(
                "leftmustache",
                CubeListBuilder.create().texOffs(19, 3)
                    .addBox(-1.0f, -0.5f, 0.0f, 6.0f, 1.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offset(2.0f, 1.4167f, -5.25f)
            )
            val leftmustachetip = leftmustache.addOrReplaceChild(
                "leftmustachetip",
                CubeListBuilder.create().texOffs(19, 2)
                    .addBox(0.0f, -0.5f, 0.0f, 6.0f, 1.0f, 0.0f, CubeDeformation(0.0f)),
                PartPose.offset(5.0f, 0.0f, 0.0f)
            )
            val rightlfipper = body.addOrReplaceChild(
                "rightflipper",
                CubeListBuilder.create().texOffs(18, 21)
                    .addBox(0.0f, -2.5f, 0.0f, 0.0f, 5.0f, 7.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-2.0f, 1.9167f, -3.5f, 0.0f, -0.2618f, 0.0f)
            )
            val leftlfipper = body.addOrReplaceChild(
                "leftflipper",
                CubeListBuilder.create().texOffs(18, 26)
                    .addBox(0.0f, -2.5f, 0.0f, 0.0f, 5.0f, 7.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(2.0f, 1.9167f, -3.5f, 0.0f, 0.2618f, 0.0f)
            )
            return LayerDefinition.create(meshdefinition, 64, 64)
        }
    }
}