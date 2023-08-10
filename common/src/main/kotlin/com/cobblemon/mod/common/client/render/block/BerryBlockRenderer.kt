/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.block

import com.cobblemon.mod.common.block.BerryBlock
import com.cobblemon.mod.common.block.entity.BerryBlockEntity
import com.cobblemon.mod.common.client.render.models.blockbench.repository.BerryModelRepository
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.RotationAxis

class BerryBlockRenderer(private val context: BlockEntityRendererFactory.Context) : BlockEntityRenderer<BerryBlockEntity> {

    override fun render(entity: BerryBlockEntity, tickDelta: Float, matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, light: Int, overlay: Int) {
        val blockState = entity.cachedState
        val age = blockState.get(BerryBlock.AGE)
        if (age <= BerryBlock.MATURE_AGE) {
            return
        }
        matrices.push()
        val isFlower = age == BerryBlock.FLOWER_AGE
        for ((berry, growthPoint) in entity.berryAndGrowthPoint()) {
            val model = (if (isFlower) BerryModelRepository.modelOf(berry.flowerModelIdentifier) else BerryModelRepository.modelOf(berry.fruitModelIdentifier)) ?: continue
            val texture = if (isFlower) berry.flowerTexture else berry.fruitTexture
            val layer = RenderLayer.getEntityCutoutNoCull(texture)
            val vertexConsumer = vertexConsumers.getBuffer(layer)
            matrices.push()
            matrices.scale(1F, -1F, 1F)
            matrices.translate(1F, 0F, 1F)
            matrices.translate(-growthPoint.position.x / 16.0, -(growthPoint.position.y / 16.0), -growthPoint.position.z / 16.0)
            matrices.push()
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(growthPoint.rotation.z.toFloat()))
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(growthPoint.rotation.y.toFloat()))
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(growthPoint.rotation.x.toFloat()))
            matrices.push()
            model.render(matrices, vertexConsumer, light, overlay)
            matrices.pop()
            matrices.pop()
            matrices.pop()
        }
        matrices.pop()
    }

}
