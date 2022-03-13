package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokeball

import com.cablemc.pokemoncobbled.common.client.entity.EmptyPokeBallClientDelegate
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.PoseableEntityModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.RootPokeBallLookAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.PokeBallFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.PoseType
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.TransformedModelPart.Companion.Y_AXIS
import com.cablemc.pokemoncobbled.common.entity.pokeball.EmptyPokeBallEntity
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.util.Mth.PI

class PokeBallModel(root: ModelPart) : PoseableEntityModel<EmptyPokeBallEntity>(), PokeBallFrame {
    override val rootPart = registerRelevantPart("root", root.getChild("root"))
    override val subRoot = registerRelevantPart("pokeball", rootPart.getChild("pokeball"))
    override val lid = registerRelevantPart("pokeball_lid", subRoot.getChild("pokeball_lid"))

    override fun registerPoses() {
        registerPose(
            poseType = PoseType.NONE,
            condition = { it.captureState.get() != EmptyPokeBallEntity.CaptureState.NOT.ordinal.toByte() },
            idleAnimations = arrayOf(RootPokeBallLookAnimation(this)),
            transformTicks = 0,
            transformedParts = arrayOf()
        )
        registerPose(
            poseType = PoseType.WALK,
            condition = { it.captureState.get() == EmptyPokeBallEntity.CaptureState.NOT.ordinal.toByte() },
            transformTicks = 0,
            idleAnimations = arrayOf(
                rootPart.rotation(
                    function = { t -> t * PI / 10 }, // 1 rotation per second = 2pi per 20 ticks = 2pi / 20 = pi / 10 per tick
                    axis = Y_AXIS,
                    timeVariable = { _, _, ageInTicks -> ageInTicks }
                ),
                rootPart.translation(
                    function = { t ->
                        if (t > 8) {
                            0F
                        } else {
                            -(8F - t) * 3F
                        }
                    },
                    axis = Y_AXIS,
                    timeVariable = { _, _, ageInTicks -> ageInTicks }
                )
            ),
            transformedParts = arrayOf()
        )
    }

    companion object {
        
        val LAYER_LOCATION = ModelLayerLocation(cobbledResource("pokeball"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root
            val root = partdefinition.addOrReplaceChild(
                "root",
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, PI, 0F, 0F)
            )
            val pokeball = root.addOrReplaceChild(
                "pokeball",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-4.0f, -4.0f, -4.0f, 8.0f, 4.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0F, 0F, 0F)
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

    override fun getState(entity: EmptyPokeBallEntity) = entity.delegate as EmptyPokeBallClientDelegate
}