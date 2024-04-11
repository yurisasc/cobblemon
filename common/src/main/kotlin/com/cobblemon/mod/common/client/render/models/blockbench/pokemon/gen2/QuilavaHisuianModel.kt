/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench.pokemon.gen2

import com.cobblemon.mod.common.client.render.models.blockbench.animation.PrimaryAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.CryProvider
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cobblemon.mod.common.entity.PoseType
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class QuilavaHisuianModel (root: ModelPart) : PokemonPoseableModel(), HeadedFrame {
    override val rootPart = root.registerChildWithAllChildren("quilava_hisuian")
    override val head = getPart("head")

    override var portraitScale = 1.5F
    override var portraitTranslation = Vec3d(-0.65, -0.6, 0.0)

    override var profileScale = 0.7F
    override var profileTranslation = Vec3d(0.0, 0.65, 0.0)

    lateinit var standing: PokemonPose
    lateinit var walking: PokemonPose
    lateinit var sleep: PokemonPose
    lateinit var battleidle: PokemonPose

    override val cryAnimation = CryProvider { entity, _ -> if (entity.isBattling) bedrockStateful("quilava_hisuian", "battle_cry") else PrimaryAnimation(bedrockStateful("quilava_hisuian", "cry")) }

    override fun registerPoses() {
        val blink = quirk { bedrockStateful("quilava_hisuian", "blink") }

        sleep = registerPose(
                poseType = PoseType.SLEEP,
                idleAnimations = arrayOf(bedrock("quilava_hisuian", "sleep"))
        )

        standing = registerPose(
                poseName = "standing",
                poseTypes = PoseType.STATIONARY_POSES + PoseType.UI_POSES,
                transformTicks = 10,
                condition = { !it.isBattling },
                quirks = arrayOf(blink),
                idleAnimations = arrayOf(
                        singleBoneLook(),
                        bedrock("quilava_hisuian", "fire_idle"),
                        bedrock("quilava_hisuian", "ground_idle")
                )
        )

        walking = registerPose(
                poseName = "walking",
                poseTypes = PoseType.MOVING_POSES,
                transformTicks = 10,
                quirks = arrayOf(blink),
                idleAnimations = arrayOf(
                        singleBoneLook(),
                        bedrock("quilava_hisuian", "fire_idle"),
                        bedrock("quilava_hisuian", "ground_walk")
                )
        )

        battleidle = registerPose(
                poseName = "battle_idle",
                poseTypes = PoseType.STATIONARY_POSES,
                transformTicks = 10,
                quirks = arrayOf(blink),
                condition = { it.isBattling },
                idleAnimations = arrayOf(
                        singleBoneLook(),
                        bedrock("quilava_hisuian", "fire_idle"),
                        bedrock("quilava_hisuian", "battle_idle")
                )
        )
    }
//    override fun getFaintAnimation(
//        pokemonEntity: PokemonEntity,
//        state: PoseableEntityState<PokemonEntity>
//    ) = if (state.isPosedIn(standing, walking, battleidle, sleep)) bedrockStateful("quilava_hisuian", "faint") else null
}