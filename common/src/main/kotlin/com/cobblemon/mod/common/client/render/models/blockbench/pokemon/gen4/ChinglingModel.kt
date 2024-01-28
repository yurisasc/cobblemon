/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen4

import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.CryProvider
import com.cobblemon.mod.common.client.render.models.blockbench.pose.Pose
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.client.render.models.blockbench.PosableModel
import com.cobblemon.mod.common.client.render.models.blockbench.PosableState
import com.cobblemon.mod.common.client.render.models.blockbench.repository.RenderContext
import com.cobblemon.mod.common.entity.PoseType.Companion.MOVING_POSES
import com.cobblemon.mod.common.entity.PoseType.Companion.SHOULDER_POSES
import com.cobblemon.mod.common.entity.PoseType.Companion.STATIONARY_POSES
import com.cobblemon.mod.common.entity.PoseType.Companion.UI_POSES
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class ChinglingModel(root: ModelPart) : PosableModel() {
    override val rootPart = root.registerChildWithAllChildren("chingling")

    override val portraitScale = 2.8F
    override val portraitTranslation = Vec3d(0.0, -2.4, 0.0)

    override val profileScale = 1.3F
    override val profileTranslation = Vec3d(0.0, -0.2, 0.0)

    lateinit var sleep: Pose
    lateinit var standing: Pose
    lateinit var walk: Pose
    lateinit var hover: Pose
    lateinit var fly: Pose

    override val cryAnimation = CryProvider { bedrockStateful("chingling", "cry") }

    override fun registerPoses() {
        val blink = quirk { bedrockStateful("chingling", "blink") }
        val sleepquirk = quirk { bedrockStateful("chingling", "sleep_quirk") }
        sleep = registerPose(
            poseType = PoseType.SLEEP,
            quirks = arrayOf(sleepquirk),
            idleAnimations = arrayOf(bedrock("chingling", "sleep"))
        )

        standing = registerPose(
            poseName = "standing",
            poseTypes = STATIONARY_POSES - PoseType.HOVER + UI_POSES + SHOULDER_POSES,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                bedrock("chingling", "ground_idle")
            )
        )

        walk = registerPose(
            poseName = "walk",
            poseTypes = MOVING_POSES - PoseType.FLY,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                bedrock("chingling", "ground_walk")
            )
        )

        hover = registerPose(
            poseName = "hover",
            poseType = PoseType.HOVER,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                bedrock("chingling", "air_idle")
            )
        )

        fly = registerPose(
            poseName = "fly",
            poseType = PoseType.FLY,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                bedrock("chingling", "air_fly")
            )
        )
    }

    override fun getFaintAnimation(state: PosableState) = if (state.isPosedIn(standing, walk, hover, fly, sleep )) bedrockStateful("chingling", "faint") else null
}