/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen2

import com.cobblemon.mod.common.client.render.models.blockbench.PosableModel
import com.cobblemon.mod.common.client.render.models.blockbench.animation.BipedWalkAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.frame.BipedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pose.Pose
import com.cobblemon.mod.common.client.render.models.blockbench.repository.RenderContext
import com.cobblemon.mod.common.entity.PoseType.Companion.FLYING_POSES
import com.cobblemon.mod.common.entity.PoseType.Companion.MOVING_POSES
import com.cobblemon.mod.common.entity.PoseType.Companion.STATIONARY_POSES
import com.cobblemon.mod.common.entity.PoseType.Companion.UI_POSES
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class GligarModel(root: ModelPart) : PosableModel(), BipedFrame {
    override val rootPart = root.registerChildWithAllChildren("gligar")

    override val portraitScale = 2.4F
    override val portraitTranslation = Vec3d(-0.1, -0.8, 0.0)

    override val leftLeg = getPart("left_upper_leg")
    override val rightLeg = getPart("right_upper_leg")

    override val profileScale = 0.8F
    override val profileTranslation = Vec3d(0.0, 0.6, 0.0)

    lateinit var standing: Pose
    lateinit var battling: Pose
    lateinit var walk: Pose

    override fun registerPoses() {

        val blink = quirk { bedrockStateful("gligar", "blink") }

        standing = registerPose(
            poseName = "standing",
            poseTypes = STATIONARY_POSES + UI_POSES + FLYING_POSES,
            transformTicks = 10,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                bedrock("gligar", "ground_idle")
            )
        )

        battling = registerPose(
            poseName = "battling",
            poseTypes = STATIONARY_POSES,
            transformTicks = 10,
            quirks = arrayOf(blink),
            condition = { (it.entity as? PokemonEntity)?.isBattling == true },
            idleAnimations = arrayOf(
                bedrock("gligar", "battle_idle")
            )
        )

        walk = registerPose(
            poseName = "walk",
            poseTypes = MOVING_POSES,
            transformTicks = 10,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                bedrock("gligar", "ground_idle"),
                BipedWalkAnimation(this, amplitudeMultiplier = 0.6F, periodMultiplier = 1F)
                //bedrock("gligar", "ground_walk")
            )
        )
    }

//    override fun getFaintAnimation(
//        pokemonEntity: PokemonEntity,
//        state: PosableState<PokemonEntity>
//    ) = if (state.isPosedIn(standing, walk)) bedrockStateful("gligar", "faint") else null
}