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
import com.cobblemon.mod.common.client.render.models.blockbench.setPosition
import com.cobblemon.mod.common.util.math.geometry.Axis
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack


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
            val layer = RenderLayer.getArmorCutoutNoCull(texture)
            val vertexConsumer = vertexConsumers.getBuffer(layer)
            //matrices.push()
            //model.scale(Vector3f(-1f, -1f, -1f))
            //model.translate(growthPoint.position.toVector3f().div(16f))
            //matrices.scale(-1F, -1F, -1F)
            //matrices.translate(-(growthPoint.position.x / 16.0), -(growthPoint.position.y / 16.0), -(growthPoint.position.z / 16.0))
            //matrices.push()
            //RenderSystem.setShaderLights(light1, light2)
            //Berries go zoom
            val degrees = entity.world?.time?.mod(45)?.times(8.0)
            model.setAngles(Math.toRadians(180.0 - growthPoint.rotation.x).toFloat(),
                Math.toRadians(180.0 + growthPoint.rotation.y).toFloat(),
                Math.toRadians(growthPoint.rotation.z).toFloat())
            model.setPosition(Axis.X_AXIS.ordinal, growthPoint.position.x.toFloat())
            model.setPosition(Axis.Y_AXIS.ordinal, growthPoint.position.y.toFloat())
            model.setPosition(Axis.Z_AXIS.ordinal, growthPoint.position.z.toFloat())
            //matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(degrees?.toFloat()!!))
            //matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(degrees?.toFloat()!!))
            //matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(degrees?.toFloat()!!))
            //matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(growthPoint.rotation.z.toFloat()))
            //model.rotate(growthPoint.rotation.toVector3f())
            //matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(growthPoint.rotation.y.toFloat()))
            //matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(growthPoint.rotation.x.toFloat()))
            matrices.push()
            //matrices.scale(-0.5f, -0.5f, -0.5f)
            //this.context.itemRenderer.renderItem(CobblemonItems.ADAMANT_MINT.defaultStack, ModelTransformationMode.GROUND, LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE, overlay, matrices, vertexConsumers, entity.world, 0);
            model.render(matrices, vertexConsumer, light, overlay)
            //RenderSystem.setShaderLights(previous.first, previous.second)
            //matrices.pop()
            //matrices.pop()
            matrices.pop()
        }
        matrices.pop()
    }

}
