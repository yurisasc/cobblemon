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

class VolcaronaModel (root: ModelPart) : PokemonPoseableModel(), HeadedFrame {
    override val rootPart = root.registerChildWithAllChildren("volcarona")
    override val head = getPart("head")

    override val portraitScale = 1.8F
    override val portraitTranslation = Vec3d(-0.9, 0.78, 0.0)

    override val profileScale = 0.66F
    override val profileTranslation = Vec3d(0.0, 0.7, 0.0)

    lateinit var standing: PokemonPose
    lateinit var walk: PokemonPose

//    override val cryAnimation = CryProvider { _, _ -> bedrockStateful("volcarona", "cry") }

    override fun registerPoses() {
        val blink = quirk { bedrockStateful("volcarona", "blink") }

        standing = registerPose(
            poseName = "standing",
            quirks = arrayOf(blink),
            poseTypes = PoseType.UI_POSES + PoseType.STATIONARY_POSES,
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("volcarona", "ground_idle")
            )
        )

        walk = registerPose(
            poseName = "walk",
            quirks = arrayOf(blink),
            poseTypes = PoseType.MOVING_POSES,
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("volcarona", "ground_idle")
                //bedrock("volcarona", "ground_walk")
            )
        )
    }

//    override fun getFaintAnimation(
//        pokemonEntity: PokemonEntity,
//        state: PoseableEntityState<PokemonEntity>
//    ) = if (state.isPosedIn(standing, walk)) bedrockStateful("volcarona", "faint") else null
}