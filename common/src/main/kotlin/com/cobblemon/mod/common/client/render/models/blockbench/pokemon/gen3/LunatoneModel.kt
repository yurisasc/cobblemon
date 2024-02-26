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

class LunatoneModel (root: ModelPart) : PokemonPoseableModel() {
    override val rootPart = root.registerChildWithAllChildren("lunatone")

    override val portraitScale = 3.6F
    override val portraitTranslation = Vec3d(-0.1, -0.4, 0.0)

    override val profileScale = 1.0F
    override val profileTranslation = Vec3d(0.0, 0.3, 0.0)

    lateinit var sleep: PokemonPose
    lateinit var standing: PokemonPose

    override fun registerPoses() {
        val blink = quirk { bedrockStateful("lunatone", "blink") }
        sleep = registerPose(
            poseType = PoseType.SLEEP,
            idleAnimations = arrayOf(bedrock("lunatone", "sleep"))
        )

        standing = registerPose(
            poseName = "standing",
            poseTypes = PoseType.ALL_POSES - PoseType.SLEEP,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                bedrock("lunatone", "ground_idle")
            )
        )
    }
//    override fun getFaintAnimation(
//        pokemonEntity: PokemonEntity,
//        state: PoseableEntityState<PokemonEntity>
//    ) = if (state.isPosedIn(sleep)) bedrockStateful("lunatone", "faint") else null
}