/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen1

import com.cobblemon.mod.common.client.render.models.blockbench.pose.Pose
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.client.render.models.blockbench.PosableModel
import com.cobblemon.mod.common.client.render.models.blockbench.PosableState
import com.cobblemon.mod.common.client.render.models.blockbench.repository.RenderContext
import com.cobblemon.mod.common.entity.PoseType.Companion.MOVING_POSES
import com.cobblemon.mod.common.entity.PoseType.Companion.STATIONARY_POSES
import com.cobblemon.mod.common.entity.PoseType.Companion.UI_POSES
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class DiglettModel(root: ModelPart) : PosableModel() {
    override val rootPart = root.registerChildWithAllChildren("diglett")

    override val portraitScale = 1.8F
    override val portraitTranslation = Vec3d(0.05, -1.0, 0.0)

    override val profileScale = 0.9F
    override val profileTranslation = Vec3d(0.0, 0.15, 0.0)

    lateinit var stand: Pose
    lateinit var walk: Pose
    lateinit var sleep: Pose
    override fun registerPoses() {
        val blink = quirk { bedrockStateful("diglett", "blink")}
        stand = registerPose(
            poseName = "stand",
            poseTypes = STATIONARY_POSES + UI_POSES,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(bedrock("diglett", "ground_idle"))
        )

        sleep = registerPose(
                poseType = PoseType.SLEEP,
                idleAnimations = arrayOf(bedrock("diglett", "sleep"))
        )

        walk = registerPose(
            poseName = "walk",
            poseTypes = MOVING_POSES,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(bedrock("diglett", "ground_walk"))
        )
    }

    override fun getFaintAnimation(state: PosableState) = bedrockStateful("diglett", "faint")
}