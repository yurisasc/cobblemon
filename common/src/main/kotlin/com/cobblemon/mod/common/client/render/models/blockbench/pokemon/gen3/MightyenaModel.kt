/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen3

import com.cobblemon.mod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.CryProvider
import com.cobblemon.mod.common.client.render.models.blockbench.pose.Pose
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.client.render.models.blockbench.PosableModel
import com.cobblemon.mod.common.client.render.models.blockbench.PosableState
import com.cobblemon.mod.common.client.render.models.blockbench.repository.RenderContext
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class MightyenaModel(root: ModelPart) : PosableModel(), HeadedFrame {
    override val rootPart = root.registerChildWithAllChildren("mightyena")
    override val head = getPart("head")

    override val portraitScale = 2.2F
    override val portraitTranslation = Vec3d(-0.8, 0.6, 0.0)

    override val profileScale = 0.6F
    override val profileTranslation = Vec3d(0.0, 0.8, 0.0)

    lateinit var sleep: Pose
    lateinit var standing: Pose
    lateinit var walk: Pose
    lateinit var battleidle: Pose

    override val cryAnimation = CryProvider { bedrockStateful("mightyena", "cry") }

    override fun registerPoses() {
        val blink = quirk { bedrockStateful("mightyena", "blink") }
        val laugh_out_battle = quirk(secondsBetweenOccurrences = 120F to 240F) { bedrockStateful("mightyena", "quirk") }
        val laugh_in_battle = quirk(secondsBetweenOccurrences = 30F to 60F) { bedrockStateful("mightyena", "quirk") }
        val sleep_quirk = quirk(secondsBetweenOccurrences = 30F to 60F) { bedrockStateful("mightyena", "sleep_quirk") }

        sleep = registerPose(
            poseType = PoseType.SLEEP,
            quirks = arrayOf(sleep_quirk),
            idleAnimations = arrayOf(bedrock("mightyena", "sleep"))
        )

        standing = registerPose(
            poseName = "standing",
            poseTypes = PoseType.STATIONARY_POSES + PoseType.UI_POSES,
            transformTicks = 10,
            condition = { (it.entity as? PokemonEntity)?.isBattling == false },
            quirks = arrayOf(blink,laugh_out_battle),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("mightyena", "ground_idle")
            )
        )

        walk = registerPose(
            poseName = "walk",
            poseTypes = PoseType.MOVING_POSES,
            transformTicks = 10,
            quirks = arrayOf(blink),
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("mightyena", "ground_walk")
            )
        )

        battleidle = registerPose(
            poseName = "battle_idle",
            poseTypes = PoseType.STATIONARY_POSES,
            transformTicks = 10,
            quirks = arrayOf(blink,laugh_in_battle),
            condition = { (it.entity as? PokemonEntity)?.isBattling == true },
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("mightyena", "battle_idle")
            )
        )
    }
    override fun getFaintAnimation(state: PosableState) = bedrockStateful("mightyena", "faint")
}