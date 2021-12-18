package com.cablemc.pokemoncobbled.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.client.render.models.blockbench.getChildOf
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pose.PoseType
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pose.TransformedModelPart
import com.cablemc.pokemoncobbled.client.render.models.blockbench.wavefunction.sineFunction
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.resources.ResourceLocation


// Made with Blockbench 4.0.5
// Exported for Minecraft version 1.17 with Mojang mappings
// Paste this class into your mod and generate all required imports
class DiglettModel(root: ModelPart) : PokemonPoseableModel() {
    override val rootPart: ModelPart = root.getChild("diglett")
    private val body: ModelPart = rootPart.getChildOf("body")

    override fun registerPoses() {
        registerPose(
            poseType = PoseType.WALK,
            condition = { true },
            idleAnimations = arrayOf(
                body.translation(
                    function = sineFunction(
                        amplitude = -1F,
                        period = 4F,
                        verticalShift = -4F
                    ),
                    axis = TransformedModelPart.Y_AXIS
                )
            ),
            transformedParts = emptyArray()
        )
    }

    companion object {
        // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
        val LAYER_LOCATION = ModelLayerLocation(ResourceLocation("modid", "diglett"), "main")
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