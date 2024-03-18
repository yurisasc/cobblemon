/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen3

import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityState
import com.cobblemon.mod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class AnorithModel (root: ModelPart) : PokemonPoseableModel() {
    override val rootPart = root.registerChildWithAllChildren("anorith")

    override var portraitTranslation = Vec3d(-0.7, -2.73, 0.0)
    override var portraitScale = 2.71F

    override var profileTranslation = Vec3d(-0.05, 0.11, 0.0)
    override var profileScale = 1.03F

    lateinit var standing: PokemonPose
    lateinit var walking: PokemonPose

    override fun registerPoses() {

        standing = registerPose(
            poseName = "standing",
            poseTypes = PoseType.STATIONARY_POSES + PoseType.UI_POSES,
            transformTicks = 10,
            idleAnimations = arrayOf(
                bedrock("anorith", "ground_idle")
            )
        )

        walking = registerPose(
            poseName = "walking",
            poseTypes = PoseType.MOVING_POSES,
            transformTicks = 10,
            idleAnimations = arrayOf(
                bedrock("anorith", "ground_idle")
            )
        )
    }
//    override fun getFaintAnimation(
//        pokemonEntity: PokemonEntity,
//        state: PoseableEntityState<PokemonEntity>
//    ) = if (state.isPosedIn(standing, walking)) bedrockStateful("anorith", "faint") else null
}