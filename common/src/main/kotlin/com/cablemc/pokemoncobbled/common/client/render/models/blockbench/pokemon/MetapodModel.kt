package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.PoseType
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class MetapodModel(root: ModelPart) : PokemonPoseableModel() {
    override val rootPart = root.registerChildWithAllChildren("metapod")

    override val portraitScale = 1.65F
    override val portraitTranslation = Vec3d(0.0, -0.6, 0.0)
    override val profileScale = 1.0F
    override val profileTranslation = Vec3d(0.0, 0.0, 0.0)

    override fun registerPoses() {
        registerPose(
            poseName = "standing",
            poseTypes = setOf(PoseType.NONE, PoseType.PROFILE, PoseType.PORTRAIT),
            transformTicks = 10,
            condition = { true },
            idleAnimations = arrayOf(
                bedrock("metapod", "ground_idle")
            )
        )
    }
}