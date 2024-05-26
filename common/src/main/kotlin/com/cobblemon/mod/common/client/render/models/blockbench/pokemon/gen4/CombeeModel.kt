/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen4

import com.cobblemon.mod.common.client.render.models.blockbench.PosableModel
import com.cobblemon.mod.common.client.render.models.blockbench.PosableState
import com.cobblemon.mod.common.client.render.models.blockbench.pose.Pose
import com.cobblemon.mod.common.entity.PoseType
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class CombeeModel (root: ModelPart) : PosableModel(root) {
    override val rootPart = root.registerChildWithAllChildren("combee")

    override var portraitScale = 1.8F
    override var portraitTranslation = Vec3d(-0.11, -0.77, 0.0)

    override var profileScale = 0.9F
    override var profileTranslation = Vec3d(0.0, 0.35, 0.0)

    lateinit var hover: Pose
    lateinit var fly: Pose
    lateinit var sleep: Pose

    override fun registerPoses() {
        val blink1 = quirk { bedrockStateful("combee", "blink_right") }
        val blink2 = quirk { bedrockStateful("combee", "blink_gender") }
        val blink3 = quirk { bedrockStateful("combee", "blink_left") }

        sleep = registerPose(
            poseType = PoseType.SLEEP,
            idleAnimations = arrayOf(bedrock("combee", "air_sleep"))
        )

        hover = registerPose(
            poseName = "hover",
            poseTypes = PoseType.UI_POSES + PoseType.STATIONARY_POSES,
            quirks = arrayOf(blink1, blink2, blink3),
            idleAnimations = arrayOf(
                bedrock("combee", "air_idle")
            )
        )

        fly = registerPose(
            poseName = "fly",
            poseTypes = PoseType.MOVING_POSES,
            quirks = arrayOf(blink1, blink2, blink3),
            idleAnimations = arrayOf(
                bedrock("combee", "air_fly")
            )
        )
    }

    override fun getFaintAnimation(state: PosableState) = if (state.isPosedIn(hover, fly, sleep)) bedrockStateful("combee", "faint") else null
}