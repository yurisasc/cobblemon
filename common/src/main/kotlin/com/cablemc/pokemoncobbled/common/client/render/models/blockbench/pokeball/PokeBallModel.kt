/*
 * Copyright (C) 2022 Pokemon Cobbled Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pokeball

import com.cablemc.pokemoncobbled.common.client.entity.EmptyPokeBallClientDelegate
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.PoseableEntityModel
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.animation.RootPokeBallLookAnimation
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.frame.PokeBallFrame
import com.cablemc.pokemoncobbled.common.client.render.models.blockbench.pose.TransformedModelPart.Companion.Y_AXIS
import com.cablemc.pokemoncobbled.common.entity.PoseType
import com.cablemc.pokemoncobbled.common.entity.pokeball.EmptyPokeBallEntity
import com.cablemc.pokemoncobbled.common.util.cobbledResource
import net.minecraft.client.model.Dilation
import net.minecraft.client.model.ModelData
import net.minecraft.client.model.ModelPart
import net.minecraft.client.model.ModelPartBuilder
import net.minecraft.client.model.ModelTransform
import net.minecraft.client.model.TexturedModelData
import net.minecraft.client.render.entity.model.EntityModelLayer
import net.minecraft.util.math.MathHelper.PI

class PokeBallModel(root: ModelPart) : PoseableEntityModel<EmptyPokeBallEntity>(), PokeBallFrame {
    override val rootPart = registerRelevantPart("root", root.getChild("root"))
    override val subRoot = registerRelevantPart("pokeball", rootPart.getChild("pokeball"))
    override val lid = registerRelevantPart("pokeball_lid", subRoot.getChild("pokeball_lid"))

    override fun registerPoses() {
        registerPose(
            poseName = "sit",
            poseTypes = setOf(PoseType.NONE),
            condition = { it.captureState.get() != EmptyPokeBallEntity.CaptureState.NOT.ordinal.toByte() },
            idleAnimations = arrayOf(RootPokeBallLookAnimation(this)),
            transformTicks = 0
        )
        registerPose(
            poseName = "flying",
            poseTypes = setOf(PoseType.NONE),
            condition = { it.captureState.get() == EmptyPokeBallEntity.CaptureState.NOT.ordinal.toByte() },
            transformTicks = 0,
            idleAnimations = arrayOf(
                rootPart.rotation(
                    function = { t -> t * PI / 10 }, // 1 rotation per second = 2pi per 20 ticks = 2pi / 20 = pi / 10 per tick
                    axis = Y_AXIS,
                    timeVariable = { _, _, ageInTicks -> ageInTicks }
                ),
                rootPart.translation(
                    function = { t ->
                        if (t > 4) {
                            0F
                        } else {
                            -(4F - t) * 2F
                        }
                    },
                    axis = Y_AXIS,
                    timeVariable = { _, _, ageInTicks -> ageInTicks }
                )
            )
        )
    }

    companion object {
        
        val LAYER_LOCATION = EntityModelLayer(cobbledResource("pokeball"), "main")
        fun createBodyLayer(): TexturedModelData {
            val meshdefinition = ModelData()
            val partdefinition = meshdefinition.root
            val root = partdefinition.addChild(
                "root",
                ModelPartBuilder.create(),
                ModelTransform.of(0.0f, 0.0f, 0.0f, PI, 0F, 0F)
            )
            val pokeball = root.addChild(
                "pokeball",
                ModelPartBuilder.create().uv(0, 0)
                    .cuboid(-4.0f, -4.0f, -4.0f, 8.0f, 4.0f, 8.0f, Dilation.NONE),
                ModelTransform.of(0.0f, 0.0f, 0.0f, 0F, 0F, 0F)
            )
            val pokeball_lid = pokeball.addChild(
                "pokeball_lid",
                ModelPartBuilder.create().uv(0, 12)
                    .cuboid(-4.0f, -4.0f, -8.0f, 8.0f, 4.0f, 8.0f, Dilation.NONE),
                ModelTransform.pivot(0.0f, -4.0f, 4.0f)
            )
            return TexturedModelData.of(meshdefinition, 32, 32)
        }
    }

    override fun getState(entity: EmptyPokeBallEntity) = entity.delegate as EmptyPokeBallClientDelegate
}