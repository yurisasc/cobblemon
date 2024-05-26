/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen3

import com.cobblemon.mod.common.client.render.models.blockbench.PosableModel
import com.cobblemon.mod.common.client.render.models.blockbench.PosableState
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.CryProvider
import com.cobblemon.mod.common.client.render.models.blockbench.pose.Pose
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.cobblemon.mod.common.util.isBattling
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class ShedinjaModel(root: ModelPart) : PosableModel(root) {
    override val rootPart = root.registerChildWithAllChildren("shedinja")

    override var portraitScale = 1.8F
    override var portraitTranslation = Vec3d(-0.2, 0.2, 0.0)

    override var profileScale = 0.9F
    override var profileTranslation = Vec3d(0.0, 0.4, 0.0)

    lateinit var sleep: Pose
    lateinit var stand: Pose
    lateinit var walk: Pose
    lateinit var hover: Pose
    lateinit var fly: Pose
    lateinit var battleidle: Pose

    override val cryAnimation = CryProvider { bedrockStateful("shedinja", "cry") }

    override fun registerPoses() {
        val blink = quirk { bedrockStateful("shedinja", "blink_WIP") }

        sleep = registerPose(
            poseType = PoseType.SLEEP,
            idleAnimations = arrayOf(bedrock("shedinja", "sleep"))
        )

        stand = registerPose(
            poseName = "standing",
            poseTypes = PoseType.UI_POSES + PoseType.STAND,
            transformTicks = 10,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                bedrock("shedinja", "ground_idle")
            )
        )

        hover = registerPose(
            poseName = "hover",
            poseType = PoseType.HOVER,
            transformTicks = 10,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                bedrock("shedinja", "air_idle")
            )
        )

        fly = registerPose(
            poseName = "fly",
            poseType = PoseType.FLY,
            transformTicks = 10,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                bedrock("shedinja", "air_fly")
            )
        )

        walk = registerPose(
            poseName = "walking",
            poseType = PoseType.WALK,
            transformTicks = 10,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                bedrock("shedinja", "ground_walk")
            )
        )
    }
    override fun getFaintAnimation(state: PosableState) = bedrockStateful("shedinja", "faint")
}