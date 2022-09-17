/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.PoseableEntityState
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.HeadedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.QuadrupedFrame
import com.cablemc.pokemoncobbled.common.entity.PoseType
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class BulbasaurModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame, QuadrupedFrame {
    override val rootPart = root.registerChildWithAllChildren("bulbasaur")
    override val head = getPart("head")
    override val foreLeftLeg = getPart("leg_front_left")
    override val foreRightLeg = getPart("leg_front_right")
    override val hindLeftLeg = getPart("leg_back_left")
    override val hindRightLeg = getPart("leg_back_right")

    override val portraitScale = 1.65F
    override val portraitTranslation = Vec3d(0.0, -0.6, 0.0)
    override val profileScale = 1.0F
    override val profileTranslation = Vec3d(0.0, 0.15, 0.0)

    lateinit var standing: PokemonPose
    lateinit var walk: PokemonPose

    override fun registerPoses() {
        standing = registerPose(
            poseName = "standing",
            poseTypes = setOf(PoseType.NONE, PoseType.PROFILE),
            transformTicks = 10,
            condition = { !it.isMoving.get() },
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("bulbasaur", "ground_idle")
            )
        )

        walk = registerPose(
            poseType = PoseType.WALK,
            transformTicks = 10,
            condition = { it.isMoving.get() },
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("bulbasaur", "ground_walk")
            )
        )
    }

    override fun getFaintAnimation(
        pokemonEntity: PokemonEntity,
        state: PoseableEntityState<PokemonEntity>
    ) = if (state.isPosedIn(standing, walk)) bedrockStateful("bulbasaur", "faint") else null
}