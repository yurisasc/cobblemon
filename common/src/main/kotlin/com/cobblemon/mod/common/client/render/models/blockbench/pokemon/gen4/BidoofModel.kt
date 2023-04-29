/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen4

import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityState
import com.cobblemon.mod.common.client.render.models.blockbench.animation.QuadrupedWalkAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.frame.QuadrupedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.entity.PoseType.Companion.MOVING_POSES
import com.cobblemon.mod.common.entity.PoseType.STAND
import com.cobblemon.mod.common.entity.PoseType.Companion.SWIMMING_POSES
import com.cobblemon.mod.common.entity.PoseType.Companion.UI_POSES
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class BidoofModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame, QuadrupedFrame {
    override val rootPart = root.registerChildWithAllChildren("bidoof")
    override val head = getPart("head")

    override val hindLeftLeg = getPart("leg_back_left")
    override val hindRightLeg = getPart("leg_back_right")
    override val foreLeftLeg = getPart("leg_front_left")
    override val foreRightLeg = getPart("leg_front_right")

    override val portraitScale = 2.2F
    override val portraitTranslation = Vec3d(-0.58, -1.3, 0.0)

    override val profileScale = 0.85F
    override val profileTranslation = Vec3d(0.0, 0.43, 0.0)

    lateinit var standing: PokemonPose
    lateinit var walk: PokemonPose
    lateinit var floating: PokemonPose

    override fun registerPoses() {
        val blink = quirk("blink") { bedrockStateful("bidoof", "blink").setPreventsIdle(false) }

        standing = registerPose(
            poseName = "standing",
            poseTypes = UI_POSES + STAND,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("bidoof", "ground_idle")
            )
        )

        walk = registerPose(
            poseName = "walk",
            poseTypes = MOVING_POSES,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                singleBoneLook(),
                //QuadrupedWalkAnimation(this, periodMultiplier = 0.8F, amplitudeMultiplier = 0.6F)
                bedrock("bidoof", "ground_walk")
            )
        )

        floating = registerPose(
            poseName = "float",
            poseTypes = SWIMMING_POSES,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("bidoof", "ground_idle"),
                bedrock("bidoof", "water_float")
            )
        )
    }

    override fun getFaintAnimation(
        pokemonEntity: PokemonEntity,
        state: PoseableEntityState<PokemonEntity>
    ) = if (state.isPosedIn(standing, walk)) bedrockStateful("bidoof", "faint") else null
}