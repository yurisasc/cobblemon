/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1

import com.cablemc.pokemod.common.client.render.models.blockbench.PoseableEntityState
import com.cablemc.pokemod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cablemc.pokemod.common.entity.PoseType.Companion.MOVING_POSES
import com.cablemc.pokemod.common.entity.PoseType.Companion.STATIONARY_POSES
import com.cablemc.pokemod.common.entity.PoseType.Companion.UI_POSES
import com.cablemc.pokemod.common.entity.pokemon.PokemonEntity
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d
class WeedleModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame {
    override val rootPart = root.registerChildWithAllChildren("weedle")
    override val head = getPart("head")

    override val portraitScale = 1.65F
    override val portraitTranslation = Vec3d(0.0, -0.6, 0.0)
    override val profileScale = 1.0F
    override val profileTranslation = Vec3d(0.0, 0.0, 0.0)

    lateinit var standing: PokemonPose
    lateinit var walk: PokemonPose

    override fun registerPoses() {
        standing = registerPose(
            poseName = "standing",
            poseTypes = STATIONARY_POSES + UI_POSES,
            transformTicks = 4,
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("0013_weedle/weedle", "ground_idle2")
            )
        )

        walk = registerPose(
            poseName = "walk",
            poseTypes = MOVING_POSES,
            transformTicks = 10,
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("0013_weedle/weedle", "ground_walk")
            )
        )
    }

    override fun getFaintAnimation(
        pokemonEntity: PokemonEntity,
        state: PoseableEntityState<PokemonEntity>
    ) = bedrockStateful("0013_weedle/weedle", "faint")
}