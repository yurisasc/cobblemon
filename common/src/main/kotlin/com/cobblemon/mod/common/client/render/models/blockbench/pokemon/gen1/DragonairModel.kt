/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen1

import com.cobblemon.mod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.entity.PoseType.Companion.MOVING_POSES
import com.cobblemon.mod.common.entity.PoseType.Companion.STATIONARY_POSES
import com.cobblemon.mod.common.entity.PoseType.Companion.UI_POSES
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class DragonairModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame {
    override val rootPart = root.registerChildWithAllChildren("dragonair")
    override val head = getPart("head")

    override val portraitScale = 2.3F
    override val portraitTranslation = Vec3d(-0.2, 1.7, 0.0)

    override val profileScale = 0.65F
    override val profileTranslation = Vec3d(0.1, 0.9, 0.0)

    lateinit var standing: PokemonPose
    lateinit var walking: PokemonPose
    lateinit var water_idle: PokemonPose
    lateinit var water_swim: PokemonPose
    lateinit var sleep: PokemonPose
    lateinit var water_sleep: PokemonPose
    lateinit var surface_swim: PokemonPose
    lateinit var surface_float: PokemonPose
    lateinit var battle_idle: PokemonPose

    override fun registerPoses() {
        val blink = quirk { bedrockStateful("dragonair", "blink") }

        sleep = registerPose(
            poseName = "sleep",
            poseType = PoseType.SLEEP,
            condition = { !it.isTouchingWater },
            idleAnimations = arrayOf(
                bedrock("dragonair", "sleep")
            )
        )

        water_sleep = registerPose(
            poseName = "water_sleep",
            poseType = PoseType.SLEEP,
            condition = { it.isTouchingWater },
            idleAnimations = arrayOf(
                bedrock("dragonair", "water_sleep")
            )
        )

        standing = registerPose(
            poseName = "standing",
            poseTypes = STATIONARY_POSES + UI_POSES - PoseType.FLOAT,
            quirks = arrayOf(blink),
            condition = { !it.isBattling },
            transformTicks = 10,
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("dragonair", "ground_idle")
            )
        )

        walking = registerPose(
            poseName = "walking",
            poseTypes = MOVING_POSES - PoseType.SWIM,
            quirks = arrayOf(blink),
            transformTicks = 10,
            idleAnimations = arrayOf(
                bedrock("dragonair", "ground_walk")
            )
        )

        water_idle = registerPose(
            poseName = "water_idle",
            poseType = PoseType.FLOAT,
            transformTicks = 10,
            idleAnimations = arrayOf(
                bedrock("dragonair", "water_idle")
            )
        )

        water_swim = registerPose(
            poseName = "water_swim",
            poseType = PoseType.SWIM,
            transformTicks = 10,
            idleAnimations = arrayOf(
                bedrock("dragonair", "water_swim")
            )
        )

        /* Anim doesn't exist
        surface_float = registerPose(
            poseName = "surface_float",
            poseTypes = PoseType.STATIONARY_POSES,
            condition = { !it.isSubmergedInWater && it.isTouchingWater},
            transformTicks = 10,
            idleAnimations = arrayOf(
                bedrock("dragonair", "surfacewater_float")
            )
        )

         */

        surface_swim = registerPose(
            poseName = "surface_swim",
            poseTypes = PoseType.MOVING_POSES,
            condition = { !it.isSubmergedInWater && it.isTouchingWater},
            transformTicks = 10,
            idleAnimations = arrayOf(
                bedrock("dragonair", "surfacewater_swim")
            )
        )

        battle_idle = registerPose(
            poseName = "battle_idle",
            poseTypes = PoseType.STATIONARY_POSES,
            transformTicks = 10,
            condition = { it.isBattling },
            idleAnimations = arrayOf(
                bedrock("dragonair", "battle_idle")
            )
        )
    }

//    override fun getFaintAnimation(
//        pokemonEntity: PokemonEntity,
//        state: PoseableEntityState<PokemonEntity>
//    ) = if (state.isPosedIn(standing, walk)) bedrockStateful("dragonair", "faint") else null
}