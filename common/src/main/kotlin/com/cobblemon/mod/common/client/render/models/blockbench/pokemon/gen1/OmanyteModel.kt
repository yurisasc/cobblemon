/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen1

import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.entity.PoseType.Companion.ALL_POSES
import com.cobblemon.mod.common.entity.PoseType.Companion.MOVING_POSES
import com.cobblemon.mod.common.entity.PoseType.Companion.STANDING_POSES
import com.cobblemon.mod.common.entity.PoseType.Companion.UI_POSES
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class OmanyteModel(root: ModelPart) : PokemonPoseableModel() {
    override val rootPart = root.registerChildWithAllChildren("omanyte")

    override val portraitScale = 3.3F
    override val portraitTranslation = Vec3d(0.0, -3.2, 0.0)

    override val profileScale = 1.6F
    override val profileTranslation = Vec3d(0.0, -0.7, 0.0)

    lateinit var standing: PokemonPose
//    lateinit var walk: PokemonPose
//    lateinit var float: PokemonPose
//    lateinit var swim: PokemonPose

    override fun registerPoses() {
        standing = registerPose(
            poseName = "standing",
            poseTypes = ALL_POSES,
            idleAnimations = arrayOf(
//                bedrock("omanyte", "ground_idle")
            )
        )

//        walk = registerPose(
//            poseName = "walk",
//            poseTypes = MOVING_POSES,
//            idleAnimations = arrayOf(
//                 bedrock("omanyte", "ground_walk")
//            )
//        )
    }

//    override fun getFaintAnimation(
//        pokemonEntity: PokemonEntity,
//        state: PoseableEntityState<PokemonEntity>
//    ) = if (state.isPosedIn(standing, walk)) bedrockStateful("omanyte", "faint") else null
}