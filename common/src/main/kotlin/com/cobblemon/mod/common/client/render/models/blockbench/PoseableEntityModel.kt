/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench

import com.cobblemon.mod.common.api.scheduling.afterOnClient
import com.cobblemon.mod.common.client.entity.PokemonClientDelegate
import com.cobblemon.mod.common.client.render.ModelLayer
import com.cobblemon.mod.common.client.render.models.blockbench.animation.PoseTransitionAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.animation.PrimaryAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.animation.RotationFunctionStatelessAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.animation.StatefulAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.animation.StatelessAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.animation.TranslationFunctionStatelessAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.bedrock.animation.BedrockAnimationRepository
import com.cobblemon.mod.common.client.render.models.blockbench.bedrock.animation.BedrockStatefulAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.bedrock.animation.BedrockStatelessAnimation
import com.cobblemon.mod.common.client.render.models.blockbench.frame.ModelFrame
import com.cobblemon.mod.common.client.render.models.blockbench.pose.Bone
import com.cobblemon.mod.common.client.render.models.blockbench.pose.ModelPartTransformation
import com.cobblemon.mod.common.client.render.models.blockbench.pose.Pose
import com.cobblemon.mod.common.client.render.models.blockbench.quirk.ModelQuirk
import com.cobblemon.mod.common.client.render.models.blockbench.quirk.SimpleQuirk
import com.cobblemon.mod.common.client.render.models.blockbench.repository.RenderContext
import com.cobblemon.mod.common.client.render.models.blockbench.wavefunction.WaveFunction
import com.cobblemon.mod.common.entity.PoseType
import com.cobblemon.mod.common.entity.Poseable
import com.cobblemon.mod.common.entity.pokeball.EmptyPokeBallEntity
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import net.minecraft.client.model.ModelPart
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.RenderPhase
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats
import net.minecraft.client.render.entity.model.EntityModel
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.util.Identifier
import net.minecraft.util.math.RotationAxis

/**
 * A model that can be posed and animated using [StatelessAnimation]s and [StatefulAnimation]s. This
 * requires poses to be registered and should implement any [ModelFrame] interfaces that apply to this
 * model. Implementing the render functions is possible but not necessary.
 *
 * @author Hiroku
 * @since December 5th, 2021
 */
abstract class PosableEntityModel<T : Entity>(
    renderTypeFunc: (Identifier) -> RenderLayer = RenderLayer::getEntityCutout
) : EntityModel<T>(renderTypeFunc), ModelFrame {
    open fun getOverlayTexture(entity: Entity?): Int? {
        return if (entity is LivingEntity) {
            OverlayTexture.packUv(
                OverlayTexture.getU(0F),
                OverlayTexture.getV(entity.hurtTime > 0 || entity.deathTime > 0)
            )
        } else if (entity != null) {
            OverlayTexture.DEFAULT_UV
        } else {
            null
        }
    }

    override fun render(
        stack: MatrixStack,
        buffer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        r: Float,
        g: Float,
        b: Float,
        a: Float
    ) {
        render(context, stack, buffer, packedLight, packedOverlay, r, g, b, a)
    }

    //This is used to set additional entity type specific context
    open fun setupEntityTypeContext(entity: T?) {}

    override fun setAngles(
        entity: T,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        headYaw: Float,
        headPitch: Float
    ) {
        setupAnimStateful(entity, getState(entity), limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch)
    }
}