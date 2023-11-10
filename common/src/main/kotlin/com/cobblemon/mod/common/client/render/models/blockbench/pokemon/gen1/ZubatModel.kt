/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen1

import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.CryProvider
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.client.render.models.blockbench.pose.ModelPartTransformation.Companion.X_AXIS
import com.cobblemon.mod.common.client.render.models.blockbench.pose.TransformedModelPart
import com.cobblemon.mod.common.entity.PoseType
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class ZubatModel(root: ModelPart) : PokemonPoseableModel() {
    override val rootPart = root.registerChildWithAllChildren("zubat")

    val wings_folded = getPart("wings_folded")
    val wings_open = getPart("wings_open")

    override val portraitScale = 1.7F
    override val portraitTranslation = Vec3d(0.0, 0.0, 0.0)

    override val profileScale = 0.7F
    override val profileTranslation = Vec3d(0.0, 0.7, 0.0)

    lateinit var sleep: PokemonPose
    lateinit var standing: PokemonPose
    lateinit var walk: PokemonPose
    lateinit var hover: PokemonPose
    lateinit var fly: PokemonPose
    lateinit var shoulderLeft: PokemonPose
    lateinit var shoulderRight: PokemonPose

    val shoulderOffset = 0

    override val cryAnimation = CryProvider { _, _ -> bedrockStateful("zubat", "cry").setPreventsIdle(false) }

    override fun registerPoses() {
        val twitch = quirk("twitch") { bedrockStateful("zubat", "eartwitch").setPreventsIdle(false) }
        sleep = registerPose(
            poseType = PoseType.SLEEP,
            transformedParts = arrayOf(
                wings_folded.asTransformed().withVisibility(visibility = false),
                wings_open.asTransformed().withVisibility(visibility = true),
            ),
            quirks = arrayOf(twitch),
            idleAnimations = arrayOf(bedrock("zubat", "sleep"))
        )

        standing = registerPose(
            poseName = "standing",
            poseTypes = PoseType.STATIONARY_POSES + PoseType.UI_POSES - PoseType.HOVER,
            transformedParts = arrayOf(
                wings_folded.asTransformed().withVisibility(visibility = false),
                wings_open.asTransformed().withVisibility(visibility = true),
            ),
            transformTicks = 10,
            quirks = arrayOf(twitch),
            idleAnimations = arrayOf(
                bedrock("zubat", "ground_idle")
            )
        )

        walk = registerPose(
            poseName = "walk",
            poseTypes = PoseType.MOVING_POSES - PoseType.FLY,
            transformedParts = arrayOf(
                wings_folded.asTransformed().withVisibility(visibility = false),
                wings_open.asTransformed().withVisibility(visibility = true),
            ),
            transformTicks = 10,
            quirks = arrayOf(twitch),
            idleAnimations = arrayOf(
                bedrock("zubat", "ground_walk")
            )
        )

        hover = registerPose(
            poseName = "hover",
            poseType = PoseType.HOVER,
            transformedParts = arrayOf(
                wings_folded.asTransformed().withVisibility(visibility = false),
                wings_open.asTransformed().withVisibility(visibility = true),
            ),
            transformTicks = 10,
            quirks = arrayOf(twitch),
            idleAnimations = arrayOf(
                bedrock("zubat", "air_idle")
            )
        )

        fly = registerPose(
            poseName = "fly",
            poseType = PoseType.FLY,
            transformedParts = arrayOf(
                wings_folded.asTransformed().withVisibility(visibility = false),
                wings_open.asTransformed().withVisibility(visibility = true),
            ),
            quirks = arrayOf(twitch),
            transformTicks = 10,
            idleAnimations = arrayOf(
                bedrock("zubat", "air_fly")
            )
        )

        shoulderLeft = registerPose(
            poseType = PoseType.SHOULDER_LEFT,
            idleAnimations = arrayOf(
                bedrock("zubat", "shoulder_left")
            ),
            transformedParts = arrayOf(
                rootPart.asTransformed().addPosition(TransformedModelPart.X_AXIS, shoulderOffset)
            )
        )

        shoulderRight = registerPose(
            poseType = PoseType.SHOULDER_RIGHT,
            idleAnimations = arrayOf(
                bedrock("zubat", "shoulder_right")
            ),
            transformedParts = arrayOf(
                rootPart.asTransformed().addPosition(TransformedModelPart.X_AXIS, -shoulderOffset)
            )
        )
    }

//    override fun getFaintAnimation(
//        pokemonEntity: PokemonEntity,
//        state: PoseableEntityState<PokemonEntity>
//    ) = if (state.isPosedIn(standing, walk)) bedrockStateful("zubat", "faint") else null
}