/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.HeadedFrame
import com.cablemc.pokemoncobbled.common.entity.PoseType
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class BellossomModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame {
    override val rootPart = root.registerChildWithAllChildren("bellossom")
    override val head = getPart("head")

    override val portraitScale = 1.5F
    override val portraitTranslation = Vec3d(0.05, 0.1, 0.0)

    override val profileScale = 0.8F
    override val profileTranslation = Vec3d(0.0, 0.5, 0.0)

    lateinit var standing: PokemonPose
    lateinit var walk: PokemonPose

    override fun registerPoses() {
        standing = registerPose(
            poseName = "standing",
            poseTypes = setOf(PoseType.SHOULDER_RIGHT, PoseType.SHOULDER_LEFT, PoseType.NONE, PoseType.PROFILE, PoseType.PORTRAIT, PoseType.STAND, PoseType.FLOAT),
            transformTicks = 10,
            idleAnimations = arrayOf(
                bedrock("bellossom", "ground_idle"),
                singleBoneLook()
            )
        )

        walk = registerPose(
            poseName = "walk",
            poseTypes = setOf(PoseType.WALK, PoseType.SWIM),
            transformTicks = 10,
            idleAnimations = arrayOf(
                bedrock("bellossom", "ground_walk"),
                singleBoneLook()
            )
        )
    }

//    override fun getFaintAnimation(
//        pokemonEntity: PokemonEntity,
//        state: PoseableEntityState<PokemonEntity>
//    ) = if (state.isPosedIn(standing, walk)) bedrockStateful("bellossom", "faint") else null
}