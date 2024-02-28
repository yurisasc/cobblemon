/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen5

import com.cobblemon.mod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.entity.PoseType
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class JellicentFemaleModel (root: ModelPart) : PokemonPoseableModel(), HeadedFrame {
    override val rootPart = root.registerChildWithAllChildren("jellicent_female")
    override val head = getPart("head")

    override var portraitScale = 2.0F
    override var portraitTranslation = Vec3d(-0.3, -1.2, 0.0)

    override var profileScale = 0.8F
    override var profileTranslation = Vec3d(0.0, 0.5, 0.0)

    lateinit var standing: PokemonPose
    lateinit var walk: PokemonPose

    override fun registerPoses() {
        val blink = quirk { bedrockStateful("jellicent_female", "blink") }

        standing = registerPose(
            poseName = "standing",
            poseTypes = PoseType.STATIONARY_POSES + PoseType.UI_POSES,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("jellicent_female", "ground_idle")
            )
        )

        walk = registerPose(
            poseName = "walk",
            poseTypes = PoseType.MOVING_POSES,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("jellicent_female", "ground_idle")
            )
        )
    }

//    override fun getFaintAnimation(
//        pokemonEntity: PokemonEntity,
//        state: PoseableEntityState<PokemonEntity>
//    ) = if (state.isPosedIn(standing, walk)) bedrockStateful("jellicent_female", "faint") else null
}