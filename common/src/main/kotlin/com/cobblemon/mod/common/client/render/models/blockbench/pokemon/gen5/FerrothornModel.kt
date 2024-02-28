package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen5

import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.client.render.models.blockbench.pose.Bone
import com.cobblemon.mod.common.entity.PoseType
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class FerrothornModel(root: ModelPart) : PokemonPoseableModel() {
    override val rootPart = root.registerChildWithAllChildren("ferrothorn")
    override var profileScale = 0.40F
    override var profileTranslation = Vec3d(-0.0075, 0.92, 0.0)
    override var portraitTranslation = Vec3d(0.0, 0.85, 0.0)

    lateinit var standing: PokemonPose
    lateinit var walk: PokemonPose
    override fun registerPoses() {
        val blink = quirk { bedrockStateful("ferrothorn", "blink") }

        standing = registerPose(
            poseName = "standing",
            poseTypes = PoseType.STATIONARY_POSES + PoseType.UI_POSES,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                bedrock("ferrothorn", "ground_idle")
            )
        )

        walk = registerPose(
            poseName = "walk",
            poseTypes = PoseType.MOVING_POSES,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                bedrock("ferrothorn", "ground_idle"),
            )
        )
    }

}