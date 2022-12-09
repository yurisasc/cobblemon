/*
 * Copyright (C) 2022 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.cobblemon.mod.common.client.render.block

import com.cobblemon.mod.common.world.block.BerryBlock
import com.cobblemon.mod.common.world.block.entity.BerryBlockEntity
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Direction

class BerryBlockRenderer(private val context: BlockEntityRendererFactory.Context) : BlockEntityRenderer<BerryBlockEntity> {

    override fun render(entity: BerryBlockEntity, tickDelta: Float, matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, light: Int, overlay: Int) {
        val blockState = entity.cachedState
        val age = blockState.get(BerryBlock.AGE)
        if (age <= BerryBlock.MATURE_AGE) {
            return
        }
        val isFlower = age == BerryBlock.FLOWER_AGE
        entity.berryAndShape(isFlower).forEach { (berry, shape) ->
            val model = (if (isFlower) berry.flowerModel() else berry.fruitModel()) ?: return@forEach
            val texture = if (isFlower) berry.flowerTexture else berry.fruitTexture
            matrices.push()
            val layer = RenderLayer.getEntityCutout(texture)
            matrices.translate(shape.getMin(Direction.Axis.X), shape.getMin(Direction.Axis.Y), shape.getMin(Direction.Axis.Z))
            val vertexConsumer = vertexConsumers.getBuffer(layer)
            model.render(matrices, vertexConsumer, light, overlay)
            matrices.pop()
        }
    }

}