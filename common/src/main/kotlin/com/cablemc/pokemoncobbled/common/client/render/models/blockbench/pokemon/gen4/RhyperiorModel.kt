package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.gen4

import com.cablemc.pokemoncobbled.common.entity.PoseType
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.HeadedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class RhyperiorModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame {
    override val rootPart = root.registerChildWithAllChildren("rhyperior")
    override val head = getPart("head")

    override val portraitScale = 1.0F
    override val portraitTranslation = Vec3d(0.0, 0.0, 0.0)

    override val profileScale = 1.0F
    override val profileTranslation = Vec3d(0.0, 0.0, 0.0)

    lateinit var standing: PokemonPose
    lateinit var walk: PokemonPose

    override fun registerPoses() {
        standing = registerPose(
            poseName = "standing",
            poseTypes = setOf(PoseType.NONE, PoseType.PROFILE, PoseType.PORTRAIT, PoseType.STAND, PoseType.FLOAT),
            transformTicks = 10,
            idleAnimations = arrayOf(
                singleBoneLook()
                // bedrock("0464_rhyperior/rhyperior", "ground_idle")
            )
        )

        walk = registerPose(
            poseName = "walk",
            poseTypes = setOf(PoseType.WALK, PoseType.SWIM),
            transformTicks = 10,
            idleAnimations = arrayOf(
                singleBoneLook()
                // bedrock("0464_rhyperior/rhyperior", "ground_walk")
            )
        )
    }

//    override fun getFaintAnimation(
//        pokemonEntity: PokemonEntity,
//        state: PoseableEntityState<PokemonEntity>
//    ) = if (state.isPosedIn(standing, walk)) bedrockStateful("0464_rhyperior/rhyperior", "faint") else null
}