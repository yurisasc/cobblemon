/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1

import com.cablemc.pokemod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cablemc.pokemod.common.entity.PoseType
import com.cablemc.pokemod.common.entity.PoseType.Companion.UI_POSES
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
            poseTypes = UI_POSES + PoseType.STAND,
            condition = { !it.isTouchingWater },
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("0131_lapras/lapras", "ground_idle")
            )
        )

        landMove = registerPose(
            poseName = "land_move",
            poseTypes = setOf(PoseType.WALK),
            condition = { !it.isTouchingWater },
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("0131_lapras/lapras", "ground_walk"),
            )
        )

        surfaceIdle = registerPose(
            poseName = "surface_idle",
            poseTypes = setOf(PoseType.STAND),
            condition = { it.isTouchingWater },
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("0131_lapras/lapras", "water_idle")
            )
        )

        surfaceMove = registerPose(
            poseName = "surface_move",
            poseTypes = setOf(PoseType.WALK),
            condition = { it.isTouchingWater },
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("0131_lapras/lapras", "water_swim"),
            )
        )

        underwaterIdle = registerPose(
            poseName = "underwater_idle",
            poseTypes = setOf(PoseType.FLOAT),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("0131_lapras/lapras", "underwater_idle")
            )
        )

        underwaterMove = registerPose(
            poseName = "underwater_move",
            poseTypes = setOf(PoseType.SWIM),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("0131_lapras/lapras", "underwater_swim")
            )
        )
    }

//    override fun getFaintAnimation(
//        pokemonEntity: PokemonEntity,
//        state: PoseableEntityState<PokemonEntity>
//    ) = if (state.isPosedIn(landIdle, landMove)) bedrockStateful("0131_lapras/lapras", "faint") else null
}