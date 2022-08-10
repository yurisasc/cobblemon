package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.PoseableEntityState
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.HeadedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.QuadrupedFrame
import com.cablemc.pokemoncobbled.common.entity.PoseType
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class VenusaurModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame, QuadrupedFrame {
    override val rootPart = root.registerChildWithAllChildren("venusaur")
    override val head = getPart("head")
    override val foreLeftLeg = getPart("leg_front_left")
    override val foreRightLeg = getPart("leg_front_right")
    override val hindLeftLeg = getPart("leg_back_left")
    override val hindRightLeg = getPart("leg_back_right")

    override val portraitScale = 1.75F
    override val portraitTranslation = Vec3d(-0.7, -0.9, 0.0)

    override val profileScale = 1.0F
    override val profileTranslation = Vec3d(0.0, 0.0, 0.0)

    lateinit var standing: PokemonPose
    lateinit var walk: PokemonPose

    override fun registerPoses() {
        standing = registerPose(
            poseName = "standing",
            poseTypes = setOf(PoseType.NONE, PoseType.PROFILE, PoseType.PORTRAIT),
            transformTicks = 10,
            condition = { !it.isMoving.get() },
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("venusaur", "ground_idle")
            )
        )

        walk = registerPose(
            poseType = PoseType.WALK,
            transformTicks = 10,
            condition = { it.isMoving.get() },
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("venusaur", "ground_idle"),
                bedrock("venusaur", "ground_walk")
            )
        )
    }

    override fun getFaintAnimation(
        pokemonEntity: PokemonEntity,
        state: PoseableEntityState<PokemonEntity>
    ) = if (state.isPosedIn(standing, walk)) bedrockStateful("venusaur", "faint") else null
}