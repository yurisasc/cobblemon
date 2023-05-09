/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen3

import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.entity.PoseType
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class ChimechoModel(root: ModelPart) : PokemonPoseableModel() {
    override val rootPart = root.registerChildWithAllChildren("chimecho")

    override val portraitScale = 3.6F
    override val portraitTranslation = Vec3d(-0.1, -0.4, 0.0)

    override val profileScale = 1.0F
    override val profileTranslation = Vec3d(0.0, 0.3, 0.0)

    lateinit var sleep: PokemonPose
    lateinit var standing: PokemonPose
    lateinit var walk: PokemonPose

    override fun registerPoses() {
        val blink = quirk("blink") { bedrockStateful("chimecho", "blink").setPreventsIdle(false) }

        standing = registerPose(
            poseName = "hover",
            poseTypes = PoseType.ALL_POSES,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                bedrock("chimecho", "idle")
            )
        )
    }
}