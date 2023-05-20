/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen7


import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.entity.PoseType.Companion.MOVING_POSES
import com.cobblemon.mod.common.entity.PoseType.Companion.STATIONARY_POSES
import com.cobblemon.mod.common.entity.PoseType.Companion.UI_POSES
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d
class CutieflyModel(root: ModelPart) : PokemonPoseableModel(){
    override val rootPart = root.registerChildWithAllChildren("cutiefly")

    override val portraitScale = 2.8F
    override val portraitTranslation = Vec3d(-0.1, 0.1, 0.0)

    override val profileScale = 0.7F
    override val profileTranslation = Vec3d(0.1, 0.8, 0.0)

    lateinit var idle: PokemonPose
    lateinit var walk: PokemonPose

    override fun registerPoses() {

//        sleep = registerPose(
//            poseType = PoseType.SLEEP,
//            idleAnimations = arrayOf(bedrock("cutiefly", "sleep"))
//        )

       idle = registerPose(
            poseName = "idle",
            poseTypes = STATIONARY_POSES + UI_POSES,
            idleAnimations = arrayOf(
                bedrock("cutiefly", "ground_idle")
            )
        )

        walk = registerPose(
            poseName = "walk",
            poseTypes = MOVING_POSES,
            idleAnimations = arrayOf(
                bedrock("cutiefly", "ground_idle")
            )
        )
    }
}