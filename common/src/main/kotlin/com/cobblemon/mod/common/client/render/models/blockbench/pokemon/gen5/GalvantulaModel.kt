/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen5

import com.cobblemon.mod.common.client.render.models.blockbench.PosableModel
import com.cobblemon.mod.common.client.render.models.blockbench.pose.Pose
import com.cobblemon.mod.common.entity.PoseType
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class GalvantulaModel(root: ModelPart) : PosableModel(root) {
    override val rootPart = root.registerChildWithAllChildren("galvantula")

    override var portraitScale = 2.0F
    override var portraitTranslation = Vec3d(-0.35, -1.3, 0.0)

    override var profileScale = 0.75F
    override var profileTranslation = Vec3d(0.0, 0.55, 0.0)

    lateinit var standing: Pose
    lateinit var walk: Pose

    override fun registerPoses() {
        val blink1 = quirk { bedrockStateful("galvantula", "blink1") }
        val blink2 = quirk { bedrockStateful("galvantula", "blink2") }
        standing = registerPose(
                poseName = "standing",
                poseTypes = PoseType.STATIONARY_POSES + PoseType.UI_POSES,
                transformTicks = 10,
                quirks = arrayOf(blink1, blink2),
                idleAnimations = arrayOf(
                        bedrock("galvantula", "ground_idle")
                )
        )

        walk = registerPose(
                poseName = "walk",
                poseTypes = PoseType.MOVING_POSES,
                transformTicks = 10,
                quirks = arrayOf(blink1, blink2),
                idleAnimations = arrayOf(
                        bedrock("galvantula", "ground_walk")
                )
        )
    }
}