/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen7


import com.cobblemon.mod.common.client.render.models.blockbench.createTransformation
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.client.render.models.blockbench.pose.ModelPartTransformation
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.entity.PoseType.Companion.MOVING_POSES
import com.cobblemon.mod.common.entity.PoseType.Companion.STATIONARY_POSES
import com.cobblemon.mod.common.entity.PoseType.Companion.UI_POSES
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d
class CutieflyModel(root: ModelPart) : PokemonPoseableModel(){
    override val rootPart = root.registerChildWithAllChildren("cutiefly")

    override val portraitScale = 2.4F
    override val portraitTranslation = Vec3d(-0.3, 1.5, 0.0)

    override val profileScale = 0.7F
    override val profileTranslation = Vec3d(0.0, 1.2, 0.0)

    lateinit var standing: PokemonPose
    lateinit var walk: PokemonPose
    lateinit var sleep: PokemonPose

    override fun registerPoses() {

        sleep = registerPose(
            poseType = PoseType.SLEEP,
            idleAnimations = arrayOf(bedrock("cutiefly", "sleep"))
        )

       standing = registerPose(
            poseName = "standing",
            poseTypes = STATIONARY_POSES + UI_POSES,
            idleAnimations = arrayOf(
                bedrock("cutiefly", "ground_idle")
            ),
           transformedParts = arrayOf(rootPart.createTransformation().addPosition(ModelPartTransformation.Y_AXIS, -12F))
        )

        walk = registerPose(
            poseName = "walk",
            poseTypes = MOVING_POSES,
            idleAnimations = arrayOf(
                bedrock("cutiefly", "ground_walk"),
            ),
            transformedParts = arrayOf(rootPart.createTransformation().addPosition(ModelPartTransformation.Y_AXIS, -12F))
        )
    }
        override fun getFaintAnimation(
            pokemonEntity: PokemonEntity,
            state: PosableState<PokemonEntity>
    ) = if (state.isPosedIn(standing, walk, sleep)) bedrockStateful("cutiefly", "faint") else null
}