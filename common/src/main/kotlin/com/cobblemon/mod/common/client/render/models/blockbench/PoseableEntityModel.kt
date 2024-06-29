/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.models.blockbench

import com.cobblemon.mod.common.client.render.models.blockbench.repository.RenderContext
import com.cobblemon.mod.common.entity.PosableEntity
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.entity.LivingEntityRenderer
import net.minecraft.client.render.entity.model.EntityModel
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.resources.ResourceLocation

/**
 * A wrapping around a [PosableModel] that presents as an [EntityModel]. This is used to continue using
 * the [LivingEntityRenderer] system while still being able to use the [PosableModel] system. Subclasses
 * just lock in the type of [EntityModel].
 *
 * @author Hiroku
 * @since January 5th, 2024
 */
abstract class PosableEntityModel<T : Entity>(
    renderTypeFunc: (ResourceLocation) -> RenderType = RenderType::getEntityCutout
) : EntityModel<T>(renderTypeFunc) {
    val context: RenderContext = RenderContext().also {
        it.put(RenderContext.RENDER_STATE, RenderContext.RenderState.WORLD)
    }

    lateinit var posableModel: PosableModel

    override fun render(
        stack: PoseStack,
        buffer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        color: Int
    ) {
        val entity = context.request(RenderContext.ENTITY)
        val overlay = getOverlayTexture(entity) ?: packedOverlay
        posableModel.render(context, stack, buffer, packedLight, overlay, color)
    }

    open fun getOverlayTexture(entity: Entity?): Int? {
        return if (entity is LivingEntity) {
            OverlayTexture.packUv(
                OverlayTexture.getU(0F),
                OverlayTexture.getV(entity.hurtTime > 0 || entity.deathTime > 0)
            )
        } else if (entity != null) {
            OverlayTexture.NO_OVERLAY
        } else {
            null
        }
    }

    // Called by LivingEntityRenderer's render method before calling model.render (which is this.render in this case)
    override fun setAngles(
        entity: T,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        headYaw: Float,
        headPitch: Float
    ) {
        setupEntityTypeContext(entity)
        if (entity is PosableEntity) {
            val state = entity.delegate as PosableState
            posableModel.applyAnimations(entity, state, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch)
        }
    }

    open fun setupEntityTypeContext(entity: Entity?) {
        entity?.let {
            context.put(RenderContext.ENTITY, entity)
            if (it is PosableEntity) {
                context.put(RenderContext.POSABLE_STATE, it.delegate as PosableState)
            }
        }
    }
}