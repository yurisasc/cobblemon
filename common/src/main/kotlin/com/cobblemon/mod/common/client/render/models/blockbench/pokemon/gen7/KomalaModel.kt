/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen7

import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.entity.PoseType
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class KomalaModel (root: ModelPart) : PokemonPoseableModel() {
    override val rootPart = root.registerChildWithAllChildren("komala")

    override val portraitScale = 1.8F
    override val portraitTranslation = Vec3d(0.0, -0.4, 0.0)
    override val profileScale = 0.8F
    override val profileTranslation = Vec3d(0.0, 0.5, 0.0)

    lateinit var standing: PokemonPose

//    override val cryAnimation = CryProvider { _, _ -> bedrockStateful("komala", "cry").setPreventsIdle(false) }

    override fun registerPoses() {
        val doze = quirk("doze", secondsBetweenOccurrences = 60F to 120F) { bedrockStateful("komala", "quirk_doze_off").setPreventsIdle(false)}

        standing = registerPose(
            poseName = "standing",
            quirks = arrayOf(doze),
            poseTypes = PoseType.ALL_POSES,
            idleAnimations = arrayOf(bedrock("komala", "ground_idle"))
        )
    }

//    override fun getFaintAnimation(
//        pokemonEntity: PokemonEntity,
//        state: PosableState<PokemonEntity>
//    ) = if (state.isPosedIn(standing)) bedrockStateful("komala", "faint") else null
}