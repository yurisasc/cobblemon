/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen9

import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityState
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class TandemausModel (root: ModelPart) : PokemonPoseableModel() {
    override val rootPart = root.registerChildWithAllChildren("tandemaus")

    override val portraitScale = 4.2F
    override val portraitTranslation = Vec3d(0.0, -4.7, 0.0)

    override val profileScale = 1.25F
    override val profileTranslation = Vec3d(0.0, -0.15, 0.0)

    lateinit var standing: PokemonPose
    lateinit var walk: PokemonPose
    lateinit var sleep: PokemonPose

    override fun registerPoses() {
        val blink1 = quirk("blink1") { bedrockStateful("tandemaus", "blink1").setPreventsIdle(false)}
        val blink2 = quirk("blink2") { bedrockStateful("tandemaus", "blink2").setPreventsIdle(false)}
        standing = registerPose(
            poseName = "standing",
            poseTypes = PoseType.STATIONARY_POSES + PoseType.UI_POSES,
            transformTicks = 10,
            quirks = arrayOf(blink1, blink2),
            idleAnimations = arrayOf(
                bedrock("tandemaus", "ground_idle")
            )
        )

        walk = registerPose(
            poseName = "walk",
            poseTypes = PoseType.MOVING_POSES,
            transformTicks = 10,
            quirks = arrayOf(blink1, blink2),
            idleAnimations = arrayOf(
                bedrock("tandemaus", "ground_walk")
            )
        )
    }
    override fun getFaintAnimation(
        pokemonEntity: PokemonEntity,
        state: PoseableEntityState<PokemonEntity>
    ) = if (state.isNotPosedIn(sleep)) bedrockStateful("tandemaus", "faint") else null
}