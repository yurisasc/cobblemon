/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen3

import com.cobblemon.mod.common.client.render.models.blockbench.pose.Pose
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.client.render.models.blockbench.PosableModel
import com.cobblemon.mod.common.client.render.models.blockbench.PosableState
import com.cobblemon.mod.common.client.render.models.blockbench.repository.RenderContext
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class ChimechoModel(root: ModelPart) : PosableModel() {
    override val rootPart = root.registerChildWithAllChildren("chimecho")

    override val portraitScale = 3.6F
    override val portraitTranslation = Vec3d(-0.1, -0.4, 0.0)

    override val profileScale = 1.0F
    override val profileTranslation = Vec3d(0.0, 0.3, 0.0)

    lateinit var sleep: Pose
    lateinit var hover: Pose
    lateinit var fly: Pose

    override fun registerPoses() {
        val blink = quirk { bedrockStateful("chimecho", "blink") }
        sleep = registerPose(
            poseType = PoseType.SLEEP,
            idleAnimations = arrayOf(bedrock("chimecho", "sleep"))
        )

        hover = registerPose(
            poseName = "hover",
            poseTypes = PoseType.STATIONARY_POSES + PoseType.UI_POSES,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                bedrock("chimecho", "air_idle")
            )
        )

        fly = registerPose(
            poseName = "flying",
            poseTypes = PoseType.MOVING_POSES,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                bedrock("chimecho", "air_fly")
            )
        )
    }
    override fun getFaintAnimation(state: PosableState) = if (state.isPosedIn(hover, fly, sleep)) bedrockStateful("chimecho", "faint") else null
}