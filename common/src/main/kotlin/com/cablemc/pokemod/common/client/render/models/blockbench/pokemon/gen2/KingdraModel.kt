/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen2

import com.cablemc.pokemod.common.client.render.models.blockbench.asTransformed
import com.cablemc.pokemod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pose.TransformedModelPart.Companion.Y_AXIS
import com.cablemc.pokemod.common.entity.PoseType
import com.cablemc.pokemod.common.entity.PoseType.Companion.UI_POSES
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class KingdraModel(root: ModelPart) : PokemonPoseableModel(), HeadedFrame {
    override val rootPart = root.registerChildWithAllChildren("kingdra")
    override val head = getPart("head")

    override val portraitScale = 1.15F
    override val portraitTranslation = Vec3d(-0.2, 0.25, 0.0)

    override val profileScale = 1.0F
    override val profileTranslation = Vec3d(0.0, 0.0, 0.0)

    lateinit var standing: PokemonPose
    lateinit var walk: PokemonPose
    lateinit var float: PokemonPose
    lateinit var swim: PokemonPose

    override fun registerPoses() {
        standing = registerPose(
            poseName = "standing",
            poseTypes = UI_POSES + PoseType.STAND,
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("0230_kingdra/kingdra", "ground_idle")
            ),
            transformedParts = arrayOf(
                rootPart.asTransformed().addPosition(Y_AXIS, -4.0)
            )
        )

        walk = registerPose(
            poseName = "walk",
            poseType = PoseType.WALK,
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("0230_kingdra/kingdra", "ground_walk")
            ),
            transformedParts = arrayOf(
                rootPart.asTransformed().addPosition(Y_AXIS, -4.0)
            )
        )

        float = registerPose(
            poseName = "float",
            poseType = PoseType.FLOAT,
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("0230_kingdra/kingdra", "water_idle")
            ),
            transformedParts = arrayOf(
                rootPart.asTransformed().addPosition(Y_AXIS, -4.0)
            )
        )

        swim = registerPose(
            poseName = "swim",
            poseType = PoseType.SWIM,
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("0230_kingdra/kingdra", "water_swim")
            ),
            transformedParts = arrayOf(
                rootPart.asTransformed().addPosition(Y_AXIS, -4.0)
            )
        )
    }

//    override fun getFaintAnimation(
//        pokemonEntity: PokemonEntity,
//        state: PoseableEntityState<PokemonEntity>
//    ) = if (state.isPosedIn(standing, walk)) bedrockStateful("0230_kingdra/kingdra", "faint") else null
}