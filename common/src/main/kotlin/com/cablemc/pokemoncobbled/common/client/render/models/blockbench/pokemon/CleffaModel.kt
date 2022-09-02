package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.PoseableEntityState
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.asTransformed
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.TransformedModelPart.Companion.X_AXIS
import com.cablemc.pokemoncobbled.common.entity.PoseType
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class CleffaModel(root: ModelPart) : PokemonPoseableModel() {
    override val rootPart = root.registerChildWithAllChildren("cleffa")

    override val portraitScale = 1.5F
    override val portraitTranslation = Vec3d(0.1, -0.45, 0.0)

    override val profileScale = 0.6F
    override val profileTranslation = Vec3d(0.0, 0.75, 0.0)

    lateinit var standing: PokemonPose
    lateinit var walk: PokemonPose
    lateinit var leftShoulder: PokemonPose
    lateinit var rightShoulder: PokemonPose

    override fun registerPoses() {
        standing = registerPose(
            poseName = "standing",
            poseTypes = setOf(PoseType.NONE, PoseType.PROFILE, PoseType.PORTRAIT, PoseType.STAND, PoseType.FLOAT),
            transformTicks = 10,
            idleAnimations = arrayOf(
                bedrock("cleffa", "ground_idle")
            )
        )

        val shoulderDisplacement = 4.0

        leftShoulder = registerPose(
            poseName = "left_shoulder",
            poseTypes = setOf(PoseType.SHOULDER_LEFT),
            transformTicks = 10,
            idleAnimations = arrayOf(
                bedrock("cleffa", "ground_idle")
            ),
            transformedParts = arrayOf(
                rootPart.asTransformed().addPosition(X_AXIS, shoulderDisplacement)
            )
        )

        rightShoulder = registerPose(
            poseName = "right_shoulder",
            poseTypes = setOf(PoseType.SHOULDER_RIGHT),
            transformTicks = 10,
            idleAnimations = arrayOf(
                bedrock("cleffa", "ground_idle")
            ),
            transformedParts = arrayOf(
                rootPart.asTransformed().addPosition(X_AXIS, -shoulderDisplacement)
            )
        )

        walk = registerPose(
            poseName = "walk",
            poseTypes = setOf(PoseType.WALK, PoseType.SWIM),
            transformTicks = 10,
            idleAnimations = arrayOf(
                bedrock("cleffa", "ground_idle"),
                bedrock("cleffa", "ground_walk")
            )
        )
    }
    override fun getFaintAnimation(
        pokemonEntity: PokemonEntity,
        state: PoseableEntityState<PokemonEntity>
    ) = if (state.isPosedIn(standing, walk)) bedrockStateful("cleffa", "faint") else null
}