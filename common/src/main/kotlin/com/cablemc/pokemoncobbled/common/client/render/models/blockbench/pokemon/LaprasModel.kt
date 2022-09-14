package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.HeadedFrame
import com.cablemc.pokemoncobbled.common.entity.PoseType
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class LaprasModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame {
    override val rootPart = root.registerChildWithAllChildren("lapras")
    override val head = getPart("head_ai")

    override val portraitScale = 1.8F
    override val portraitTranslation = Vec3d(-0.35, 0.4, 0.0)

    override val profileScale = 0.9F
    override val profileTranslation = Vec3d(-0.0, 0.25, 0.0)

    lateinit var landIdle: PokemonPose
    lateinit var landMove: PokemonPose
    lateinit var surfaceIdle: PokemonPose
    lateinit var surfaceMove: PokemonPose
    lateinit var underwaterIdle: PokemonPose
    lateinit var underwaterMove: PokemonPose

    override fun registerPoses() {
        landIdle = registerPose(
            poseName = "land_idle",
            poseTypes = setOf(PoseType.NONE, PoseType.STAND, PoseType.PROFILE, PoseType.PORTRAIT),
            transformTicks = 10,
            condition = { !it.isTouchingWater },
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("lapras", "ground_idle")
            )
        )

        landMove = registerPose(
            poseName = "land_move",
            poseTypes = setOf(PoseType.WALK),
            transformTicks = 10,
            condition = { !it.isTouchingWater },
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("lapras", "ground_walk"),
            )
        )

        surfaceIdle = registerPose(
            poseName = "surface_idle",
            poseTypes = setOf(PoseType.STAND),
            transformTicks = 10,
            condition = { it.isTouchingWater },
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("lapras", "water_idle")
            )
        )

        surfaceMove = registerPose(
            poseName = "surface_move",
            poseTypes = setOf(PoseType.WALK),
            transformTicks = 10,
            condition = { it.isTouchingWater },
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("lapras", "water_swim"),
            )
        )

        underwaterIdle = registerPose(
            poseName = "underwater_idle",
            poseTypes = setOf(PoseType.FLOAT),
            transformTicks = 10,
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("lapras", "underwater_idle")
            )
        )

        underwaterMove = registerPose(
            poseName = "underwater_move",
            poseTypes = setOf(PoseType.SWIM),
            transformTicks = 10,
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("lapras", "underwater_swim")
            )
        )
    }

//    override fun getFaintAnimation(
//        pokemonEntity: PokemonEntity,
//        state: PoseableEntityState<PokemonEntity>
//    ) = if (state.isPosedIn(landIdle, landMove)) bedrockStateful("lapras", "faint") else null
}