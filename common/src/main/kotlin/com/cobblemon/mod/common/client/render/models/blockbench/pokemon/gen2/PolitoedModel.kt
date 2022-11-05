/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen2

import com.cobblemon.mod.common.client.render.models.blockbench.frame.BipedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.entity.PoseType.Companion.MOVING_POSES
import com.cobblemon.mod.common.entity.PoseType.Companion.STATIONARY_POSES
import com.cobblemon.mod.common.entity.PoseType.Companion.UI_POSES
import net.minecraft.client.model.ModelPart
class PolitoedModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame, BipedFrame {
    override val rootPart = root.registerChildWithAllChildren("politoed")
    override val head = getPart("head")

    override val leftLeg = getPart("leftleg")
    override val rightLeg = getPart("rightleg")

    lateinit var standing: PokemonPose
    lateinit var walking: PokemonPose

    override fun registerPoses() {
        standing = registerPose(
            poseName = "standing",
            poseTypes = STATIONARY_POSES + UI_POSES,
            idleAnimations = arrayOf(
                singleBoneLook(),
                //bedrock("0186_politoed/politoed", "ground_idle")
            )
        )

        walking = registerPose(
            poseName = "walking",
            poseTypes = MOVING_POSES,
            idleAnimations = arrayOf(
                singleBoneLook()
                //bedrock("0186_politoed/politoed", "ground_walk")
            )
        )
    }

    //    override fun getFaintAnimation(
//        pokemonEntity: PokemonEntity,
//        state: PoseableEntityState<PokemonEntity>
//    ) = if (state.isPosedIn(standing, walk)) bedrockStateful("0186_politoed/politoed", "faint") else null
}