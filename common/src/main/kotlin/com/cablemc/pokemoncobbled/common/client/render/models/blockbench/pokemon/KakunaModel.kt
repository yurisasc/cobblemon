package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.SingleBoneLookAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.HeadedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.getChildOf
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.PoseType
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.TransformedModelPart
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.wavefunction.parabolaFunction
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.*
import net.minecraft.world.phys.Vec3

class KakunaModel(root: ModelPart) : PokemonPoseableModel() {
    override val rootPart = registerRelevantPart("kakuna", root.getChild("kakuna"))
    override val portraitScale = 1.95F
    override val portraitTranslation = Vec3(-0.05, -0.7, 0.0)

    override val profileScale = 1.0F
    override val profileTranslation = Vec3(0.0, 0.0, 0.0)

    init {
        registerPoses()
    }

    override fun registerPoses() {
        registerPose(
            poseType = PoseType.WALK,
            condition = { it.isMoving.get() },
            transformTicks = 5,
            idleAnimations = arrayOf(
                rootPart.translation(
                    function = parabolaFunction(
                        peak = -4F,
                        period = 0.4F
                    ),
                    timeVariable = { state, _, _ -> state?.animationSeconds },
                    axis = TransformedModelPart.Y_AXIS
                ),
            ),
            transformedParts = arrayOf(),
            )
    }

    companion object {
        val LAYER_LOCATION = ModelLayerLocation(cobbledResource("kakuna"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root
            val kakuna =
                partdefinition.addOrReplaceChild("kakuna", CubeListBuilder.create(), PartPose.offset(0.0f, 24.0f, 0.0f))
            val body = kakuna.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(0, 11)
                    .addBox(-3.0f, -3.5066f, -1.6215f, 6.0f, 7.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, -7.4934f, -0.1285f)
            )
            val body_r1 = body.addOrReplaceChild(
                "body_r1",
                CubeListBuilder.create().texOffs(16, 18)
                    .addBox(-2.0f, -1.0f, -2.0f, 4.0f, 4.0f, 4.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, 4.0368f, 0.2095f, -0.2182f, 0.0f, 0.0f)
            )
            val eye_r1 = body.addOrReplaceChild(
                "eye_r1",
                CubeListBuilder.create().texOffs(24, 0).mirror()
                    .addBox(1.5f, -0.75f, -3.0f, 2.0f, 2.0f, 2.0f, CubeDeformation(0.02f)).mirror(false)
                    .texOffs(24, 0).addBox(-3.5f, -0.75f, -3.0f, 2.0f, 2.0f, 2.0f, CubeDeformation(0.02f))
                    .texOffs(0, 0).addBox(-3.5f, -2.5f, -3.0f, 7.0f, 5.0f, 6.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(0.0f, -5.0066f, -0.3715f, 0.1745f, 0.0f, 0.0f)
            )
            val righteyelid =
                body.addOrReplaceChild("righteyelid", CubeListBuilder.create(), PartPose.offset(0.0f, 0.0f, 0.0f))
            val lefteyelid =
                body.addOrReplaceChild("lefteyelid", CubeListBuilder.create(), PartPose.offset(0.0f, 0.0f, 0.0f))
            return LayerDefinition.create(meshdefinition, 32, 32)
        }
    }
}