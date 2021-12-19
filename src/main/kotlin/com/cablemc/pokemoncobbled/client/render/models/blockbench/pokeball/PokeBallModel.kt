package com.cablemc.pokemoncobbled.client.render.models.blockbench.pokeball

import com.cablemc.pokemoncobbled.client.entity.PokeBallClientDelegate
import com.cablemc.pokemoncobbled.client.render.models.blockbench.PoseableEntityModel
import com.cablemc.pokemoncobbled.client.render.models.blockbench.animation.RotationFunctionStatelessAnimation
import com.cablemc.pokemoncobbled.client.render.models.blockbench.frame.PokeBallFrame
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pose.PoseType
import com.cablemc.pokemoncobbled.client.render.models.blockbench.pose.TransformedModelPart.Companion.Y_AXIS
import com.cablemc.pokemoncobbled.common.entity.pokeball.PokeBallEntity
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth.PI

class PokeBallModel(root: ModelPart) : PoseableEntityModel<PokeBallEntity>(), PokeBallFrame {
    override val rootPart = registerRelevantPart(root.getChild("pokeball"))
    override val lid = registerRelevantPart(root.getChild("pokeball").getChild("pokeball_lid"))

    override fun registerPoses() {
        registerPose(
            poseType = PoseType.NONE,
            condition = { true },
            idleAnimations = arrayOf(
                rootPart.rotation(
                    function = { t -> t * PI / 10 }, // 1 rotation per second = 2pi per 20 ticks = 2pi / 20 = pi / 10 per tick
                    axis = Y_AXIS,
                    timeVariable = { _, _, ageInTicks -> ageInTicks }
                )
            ),
            transformedParts = arrayOf()
        )
    }

    companion object {
        // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
        val LAYER_LOCATION = ModelLayerLocation(ResourceLocation("modid", "pokeball"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root
            val pokeball = partdefinition.addOrReplaceChild(
                "pokeball",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-4.0f, -4.0f, -4.0f, 8.0f, 4.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 24.0f, 0.0f, PI, 0F, 0F)
            )
            val pokeball_lid = pokeball.addOrReplaceChild(
                "pokeball_lid",
                CubeListBuilder.create().texOffs(0, 12)
                    .addBox(-4.0f, -4.0f, -8.0f, 8.0f, 4.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -4.0f, 4.0f)
            )
            return LayerDefinition.create(meshdefinition, 32, 32)
        }
    }

    override fun getState(entity: PokeBallEntity) = entity.delegate as PokeBallClientDelegate
}