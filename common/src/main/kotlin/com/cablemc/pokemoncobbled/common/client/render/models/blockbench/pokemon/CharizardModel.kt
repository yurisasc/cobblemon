/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokemon

import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.PoseableEntityState
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.asTransformed
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.BiWingedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.BimanualFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.BipedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.HeadedFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.TransformedModelPart.Companion.Y_AXIS
import com.cablemc.pokemoncobbled.common.entity.PoseType
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonBehaviourFlag
import com.cablemc.pokemoncobbled.common.entity.pokemon.PokemonEntity
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class CharizardModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame, BipedFrame, BimanualFrame, BiWingedFrame {
    override val rootPart = root.registerChildWithAllChildren("charizard")
    override val head = getPart("head_ai")
    override val rightArm = getPart("arm_right")
    override val leftArm = getPart("arm_left")
    override val rightLeg = getPart("leg_right")
    override val leftLeg = getPart("leg_left")
    override val leftWing = getPart("wing_left")
    override val rightWing = getPart("wing_right")

    override val portraitScale = 1.75F
    override val portraitTranslation = Vec3d(-0.4, 1.6, 0.0)

    override val profileScale = 0.7F
    override val profileTranslation = Vec3d(0.0, 0.68, 0.0)

    lateinit var standing: PokemonPose
    lateinit var walk: PokemonPose
    lateinit var flyIdle: PokemonPose
    lateinit var fly: PokemonPose

    override fun registerPoses() {
        standing = registerPose(
            poseName = "standing",
            poseTypes = setOf(PoseType.NONE, PoseType.PROFILE, PoseType.PORTRAIT, PoseType.STAND, PoseType.FLOAT),
            transformTicks = 10,
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("charizard", "ground_idle")
            )
        )

        walk = registerPose(
            poseName = "walk",
            poseTypes = setOf(PoseType.WALK, PoseType.SWIM),
            transformTicks = 10,
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("charizard", "ground_idle"),
                bedrock("charizard", "ground_walk")
            )
        )

        flyIdle = registerPose(
            poseName = "hover",
            poseTypes = setOf(PoseType.HOVER),
            transformTicks = 10,
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("charizard", "air_idle")
            ),
            transformedParts = arrayOf(rootPart.asTransformed().addPosition(Y_AXIS, -2F))
        )

        fly = registerPose(
            poseName = "fly",
            poseTypes = setOf(PoseType.FLY),
            transformTicks = 10,
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("charizard", "air_fly")
            ),
            transformedParts = arrayOf(rootPart.asTransformed().addPosition(Y_AXIS, 6F))
        )
    }

    override fun getFaintAnimation(
        pokemonEntity: PokemonEntity,
        state: PoseableEntityState<PokemonEntity>
    ) = if (state.isPosedIn(standing, walk)) bedrockStateful("charizard", "faint") else null
}