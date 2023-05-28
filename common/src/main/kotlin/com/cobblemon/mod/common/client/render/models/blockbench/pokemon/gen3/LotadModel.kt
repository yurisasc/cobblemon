/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen3

import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityState
import com.cobblemon.mod.common.client.render.models.blockbench.asTransformed
import com.cobblemon.mod.common.client.render.models.blockbench.frame.QuadrupedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.client.render.models.blockbench.pose.TransformedModelPart
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class LotadModel (root: ModelPart) : PokemonPoseableModel(), QuadrupedFrame {
    override val rootPart = root.registerChildWithAllChildren("lotad")

    override val foreLeftLeg = getPart("leg_left")
    override val foreRightLeg = getPart("leg_right")
    override val hindLeftLeg = getPart("leg_left2")
    override val hindRightLeg = getPart("leg_right2")

    override val portraitScale = 2.5F
    override val portraitTranslation = Vec3d(-0.15, -2.0, 0.0)

    override val profileScale = 1.0F
    override val profileTranslation = Vec3d(0.0, 0.25, 0.0)

    lateinit var standing: PokemonPose
    lateinit var walk: PokemonPose
    lateinit var floating: PokemonPose
    lateinit var swim: PokemonPose
    lateinit var sleep: PokemonPose

    val wateroffset = -1

    override fun registerPoses() {
        val blink = quirk("blink") { bedrockStateful("charmander", "blink").setPreventsIdle(false) }

        standing = registerPose(
            poseName = "standing",
            quirks = arrayOf(blink),
            poseTypes = PoseType.UI_POSES + PoseType.STAND,
            transformTicks = 10,
            idleAnimations = arrayOf(
                bedrock("lotad", "ground_idle")
            )
        )

        walk = registerPose(
            poseName = "walk",
            quirks = arrayOf(blink),
            poseType = PoseType.WALK,
            transformTicks = 10,
            idleAnimations = arrayOf(
                bedrock("lotad", "ground_walk"),
            )
        )

        floating = registerPose(
            poseName = "floating",
            quirks = arrayOf(blink),
            poseType = PoseType.FLOAT,
            transformTicks = 10,
            idleAnimations = arrayOf(
                bedrock("lotad", "water_idle"),
            ),
            transformedParts = arrayOf(
                rootPart.asTransformed().addPosition(TransformedModelPart.Y_AXIS, wateroffset)
            )
        )

        swim = registerPose(
            poseName = "swim",
            quirks = arrayOf(blink),
            poseType = PoseType.SWIM,
            transformTicks = 10,
            idleAnimations = arrayOf(
                bedrock("lotad", "water_swim"),
            ),
            transformedParts = arrayOf(
                rootPart.asTransformed().addPosition(TransformedModelPart.Y_AXIS, wateroffset)
            )
        )

        sleep = registerPose(
            poseName = "sleep",
            quirks = arrayOf(blink),
            poseType = PoseType.SLEEP,
            transformTicks = 10,
            idleAnimations = arrayOf(
                bedrock("lotad", "sleep"),
            )
        )

    }
    override fun getFaintAnimation(
        pokemonEntity: PokemonEntity,
        state: PoseableEntityState<PokemonEntity>
    ) = if (state.isNotPosedIn(sleep)) bedrockStateful("lotad", "faint") else null
}