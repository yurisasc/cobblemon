/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1

import com.cablemc.pokemod.common.client.render.models.blockbench.PoseableEntityState
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cablemc.pokemod.common.entity.PoseType
import com.cablemc.pokemod.common.entity.pokemon.PokemonEntity
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class DiglettModel(root: ModelPart) : PokemonPoseableModel() {
    override val rootPart: ModelPart = root.registerChildWithAllChildren("diglett")

    override val portraitScale = 1.65F
    override val portraitTranslation = Vec3d(0.15, -0.7, 0.0)

    override val profileScale = 1.0F
    override val profileTranslation = Vec3d(0.0, 0.0, 0.0)

    override fun registerPoses() {
        registerPose(
            poseName = "stand",
            poseTypes = setOf(PoseType.NONE, PoseType.STAND, PoseType.PROFILE, PoseType.PORTRAIT, PoseType.FLOAT),
            idleAnimations = arrayOf(bedrock("0050_diglett/diglett", "ground_idle"))
        )

        registerPose(
            poseName = "walk",
            poseTypes = setOf(PoseType.WALK, PoseType.SWIM),
            idleAnimations = arrayOf(bedrock("0050_diglett/diglett", "ground_moving"))
        )
    }

    override fun getFaintAnimation(
        pokemonEntity: PokemonEntity,
        state: PoseableEntityState<PokemonEntity>
    ) = bedrockStateful("0050_diglett/diglett", "faint")
}