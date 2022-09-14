package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.PoseableEntityState
import com.cablemc.pokemoncobbled.common.entity.PoseType
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class DiglettModel(root: ModelPart) : PokemonPoseableModel() {
    override val rootPart: ModelPart = root.registerChildWithAllChildren("diglett")

    override val portraitScale = 1.65F
    override val portraitTranslation = Vec3d(0.15, -0.7, 0.0)

    override val profileScale = 1.0F
    override val profileTranslation = Vec3d(0.0, 0.0, 0.0)

    override fun registerPoses() {
        registerPose(
            poseName = "stand",
            poseTypes = setOf(PoseType.NONE, PoseType.STAND, PoseType.PROFILE, PoseType.PORTRAIT, PoseType.FLOAT),
            idleAnimations = arrayOf(bedrock("diglett", "ground_idle"))
        )

        registerPose(
            poseName = "walk",
            poseTypes = setOf(PoseType.WALK, PoseType.SWIM),
            idleAnimations = arrayOf(bedrock("diglett", "ground_moving"))
        )
    }

    override fun getFaintAnimation(
        pokemonEntity: PokemonEntity,
        state: PoseableEntityState<PokemonEntity>
    ) = bedrockStateful("diglett", "faint")
}