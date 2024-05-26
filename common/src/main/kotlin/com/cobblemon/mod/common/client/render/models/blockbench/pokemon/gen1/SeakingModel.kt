/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen1

import com.cobblemon.mod.common.client.render.models.blockbench.PosableModel
import com.cobblemon.mod.common.client.render.models.blockbench.pose.Pose
import com.cobblemon.mod.common.entity.PoseType
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class SeakingModel(root: ModelPart) : PosableModel(root) {
    override val rootPart = root.registerChildWithAllChildren("seaking")

    override var portraitScale = 2.8F
    override var portraitTranslation = Vec3d(-0.1, -2.4, 0.0)

    override var profileScale = 1.4F
    override var profileTranslation = Vec3d(0.0, -0.5, 0.0)

    lateinit var standing: Pose
    lateinit var walk: Pose
    lateinit var float: Pose
    lateinit var swim: Pose

    override fun registerPoses() {
        standing = registerPose(
            poseName = "standing",
            poseType = PoseType.STAND,
            idleAnimations = arrayOf(
                bedrock("seaking", "ground_idle")
            )
        )

        walk = registerPose(
            poseName = "walk",
            poseType = PoseType.WALK,
            idleAnimations = arrayOf(
                bedrock("seaking", "ground_idle")
            )
        )

        float = registerPose(
            poseName = "float",
            poseTypes = setOf(PoseType.FLOAT, PoseType.HOVER),
            idleAnimations = arrayOf(
                bedrock("seaking", "water_idle")
            )
        )

        swim = registerPose(
            poseName = "swim",
            poseTypes = setOf(PoseType.SWIM, PoseType.FLY),
            idleAnimations = arrayOf(
                bedrock("seaking", "water_swim")
            )
        )
    }

//    override fun getFaintAnimation(
//        pokemonEntity: PokemonEntity,
//        state: PosableState<PokemonEntity>
//    ) = if (state.isPosedIn(standing, walk)) bedrockStateful("seaking", "faint") else null
}