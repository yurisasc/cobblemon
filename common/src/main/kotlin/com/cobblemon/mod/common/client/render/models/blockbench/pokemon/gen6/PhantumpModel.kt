/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen6

import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.CryProvider
import com.cobblemon.mod.common.client.render.models.blockbench.pose.Pose
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.client.render.models.blockbench.PosableModel
import com.cobblemon.mod.common.client.render.models.blockbench.PosableState
import com.cobblemon.mod.common.client.render.models.blockbench.repository.RenderContext
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class PhantumpModel (root: ModelPart) : PosableModel() {
    override val rootPart = root.registerChildWithAllChildren("phantump")

    override val portraitScale = 2.0F
    override val portraitTranslation = Vec3d(-0.1, -0.2, 0.0)

    override val profileScale = 0.9F
    override val profileTranslation = Vec3d(0.0, 0.5, 0.0)

    lateinit var standing: Pose
    lateinit var walk: Pose
    lateinit var hover: Pose
    lateinit var fly: Pose
    lateinit var sleep: Pose
    lateinit var battleidle: Pose

    override val cryAnimation = CryProvider { bedrockStateful("phantump", "cry") }

    override fun registerPoses() {
        val blink = quirk { bedrockStateful("phantump", "blink")}
        sleep = registerPose(
            poseType = PoseType.SLEEP,
            idleAnimations = arrayOf(bedrock("phantump", "sleep"))
        )
        standing = registerPose(
            poseName = "stand",
            poseTypes = PoseType.STATIONARY_POSES - PoseType.HOVER - PoseType.FLOAT + PoseType.UI_POSES,
            condition = { (it.entity as? PokemonEntity)?.isBattling == false },
            transformTicks = 10,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                bedrock("phantump", "ground_idle")
            )
        )
        hover = registerPose(
            poseName = "hover",
            poseTypes = setOf(PoseType.HOVER, PoseType.FLOAT),
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                bedrock("phantump", "air_idle")
            )
        )
        fly = registerPose(
            poseName = "fly",
            poseTypes = setOf(PoseType.FLY, PoseType.SWIM),
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                bedrock("phantump", "air_fly")
            )
        )
        walk = registerPose(
            poseName = "walk",
            poseTypes = PoseType.MOVING_POSES - PoseType.FLY - PoseType.SWIM,
            transformTicks = 5,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                bedrock("phantump", "ground_walk")
            )
        )
        battleidle = registerPose(
            poseName = "battle_idle",
            poseTypes = PoseType.STATIONARY_POSES,
            transformTicks = 10,
            quirks = arrayOf(blink),
            condition = { (it.entity as? PokemonEntity)?.isBattling == true },
            idleAnimations = arrayOf(
                bedrock("phantump", "battle_idle")
            )
        )
    }

    override fun getFaintAnimation(state: PosableState) = if (state.isPosedIn(standing, walk, sleep)) bedrockStateful("phantump", "faint") else
        if (state.isPosedIn(battleidle)) bedrockStateful("phantump", "battle_faint")
        else null
}