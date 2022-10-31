/*
 * Copyright (C) 2022 Pokemod Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.gen1

import com.cablemc.pokemod.common.client.render.models.blockbench.EarJoint
import com.cablemc.pokemod.common.client.render.models.blockbench.RangeOfMotion
import com.cablemc.pokemod.common.client.render.models.blockbench.frame.EaredFrame
import com.cablemc.pokemod.common.client.render.models.blockbench.frame.HeadedFrame
import com.cablemc.pokemod.common.client.render.models.blockbench.frame.QuadrupedFrame
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.PokemonPose
import com.cablemc.pokemod.common.client.render.models.blockbench.pokemon.PokemonPoseableModel
import com.cablemc.pokemod.common.client.render.models.blockbench.pose.TransformedModelPart.Companion.Z_AXIS
import com.cablemc.pokemod.common.entity.PoseType.Companion.MOVING_POSES
import com.cablemc.pokemod.common.entity.PoseType.Companion.STATIONARY_POSES
import com.cablemc.pokemod.common.util.math.geometry.toRadians
import net.minecraft.client.model.ModelPart
import net.minecraft.util.math.Vec3d

class EeveeModel(root: ModelPart) : PokemonPoseableModel(), EaredFrame, HeadedFrame, QuadrupedFrame {
    override val rootPart = root.registerChildWithAllChildren("eevee")
    val body = getPart("body")
    override val head = getPart("head")
    override val hindRightLeg = getPart("leg_back_right")
    override val hindLeftLeg = getPart("leg_back_left")
    override val foreRightLeg = getPart("leg_front_right")
    override val foreLeftLeg = getPart("leg_front_left")
    override val leftEarJoint = EarJoint(getPart("ear_left"), Z_AXIS, RangeOfMotion(50F.toRadians(), 0F))
    override val rightEarJoint = EarJoint(getPart("ear_right"), Z_AXIS, RangeOfMotion((-50F).toRadians(), 0F))

    override val portraitScale = 1.55F
    override val portraitTranslation = Vec3d(-0.15, 0.1, 0.0)

    override val profileScale = 0.8F
    override val profileTranslation = Vec3d(0.0, 0.4, 0.0)

    lateinit var stand: PokemonPose
    lateinit var walk: PokemonPose

    override fun registerPoses() {
        stand = registerPose(
            poseName = "standing",
            poseTypes = STATIONARY_POSES,
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("0133_eevee/eevee", "ground_idle")
            )
        )

        walk = registerPose(
            poseName = "walk",
            poseTypes = MOVING_POSES,
            idleAnimations = arrayOf(
                singleBoneLook(),
                bedrock("0133_eevee/eevee", "ground_walk")
            )
        )

        registerShoulderPoses(idleAnimations = arrayOf(singleBoneLook()))
    }
}