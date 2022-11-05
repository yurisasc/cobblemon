/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.pokeball

import com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityModel
import com.cobblemon.mod.common.client.render.models.blockbench.repository.PokeBallModelRepository
import com.cobblemon.mod.common.entity.pokeball.EmptyPokeBallEntity
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRenderer
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.item.ItemRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
class PokeBallRenderer(context: EntityRendererFactory.Context) : EntityRenderer<EmptyPokeBallEntity>(context) {

    override fun getTexture(pEntity: EmptyPokeBallEntity): Identifier {
        return PokeBallModelRepository.getModelTexture(pEntity.pokeBall)
    }

    override fun render(entity: EmptyPokeBallEntity, yaw: Float, partialTicks: Float, poseStack: MatrixStack, buffer: VertexConsumerProvider, packedLight: Int) {
        val model = PokeBallModelRepository.getModel(entity.pokeBall).entityModel as PoseableEntityModel<EmptyPokeBallEntity>
        poseStack.push()
        poseStack.scale(0.7F, 0.7F, 0.7F)
        val vertexConsumer = ItemRenderer.getDirectItemGlintConsumer(buffer, model.getLayer(getTexture(entity)), false, false)
        model.setAngles(entity, 0f, 0f, entity.age + partialTicks, 0F, 0F)
        model.render(poseStack, vertexConsumer, packedLight, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f)
        poseStack.pop()
        super.render(entity, yaw, partialTicks, poseStack, buffer, packedLight)
    }
}