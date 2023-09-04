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
import com.cobblemon.mod.common.client.render.layer.CobblemonRenderLayers
import com.cobblemon.mod.common.client.render.models.blockbench.repository.BerryModelRepository
import com.cobblemon.mod.common.client.render.models.blockbench.setPosition
import com.cobblemon.mod.common.util.math.geometry.Axis
import com.cobblemon.mod.common.util.toVec3d
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gl.ShaderProgram
import net.minecraft.client.gl.VertexBuffer
import net.minecraft.client.render.*
import net.minecraft.client.render.RenderPhase.ENTITY_CUTOUT_NONULL_PROGRAM
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import org.joml.Matrix4f
import java.nio.ByteBuffer


class BerryBlockRenderer(private val context: BlockEntityRendererFactory.Context) : BlockEntityRenderer<BerryBlockEntity> {
    val vertexBufferMap = mutableMapOf<BlockPos, VertexBuffer>()
    override fun isInRenderDistance(blockEntity: BerryBlockEntity, pos: Vec3d): Boolean {
        return super.isInRenderDistance(blockEntity, pos)
                && MinecraftClient.getInstance().worldRenderer.frustum.isVisible(Box.of(pos, 2.0, 1.5, 2.0))
    }

    override fun render(entity: BerryBlockEntity, tickDelta: Float, matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, light: Int, overlay: Int) {
        if (!isInRenderDistance(entity, entity.pos.toVec3d())) return
        val blockState = entity.cachedState
        val age = blockState.get(BerryBlock.AGE)
        if (age <= BerryBlock.MATURE_AGE) {
            return
        }
        if (vertexBufferMap.contains(entity.pos)) {
            matrices.push()
            val vertexBuffer = vertexBufferMap[entity.pos]
            CobblemonRenderLayers.BERRY_LAYER.startDrawing()
            vertexBuffer?.bind()
            val posMatrix = matrices.peek().positionMatrix
            vertexBuffer?.draw(
                posMatrix.mul(RenderSystem.getModelViewMatrix()),
                RenderSystem.getProjectionMatrix(),
                GameRenderer.getRenderTypeEntityCutoutNoNullProgram()
            )
            VertexBuffer.unbind()
            CobblemonRenderLayers.BERRY_LAYER.endDrawing()
            matrices.pop()
        }
        else {
            constructBuffer(entity, age, light, overlay)
        }
    }

    fun constructBuffer(entity: BerryBlockEntity, age:Int, light: Int, overlay: Int) {
        val tessellator = Tessellator()
        tessellator.buffer.begin(
            CobblemonRenderLayers.BERRY_LAYER.drawMode,
            CobblemonRenderLayers.BERRY_LAYER.vertexFormat
        )
        val isFlower = age == BerryBlock.FLOWER_AGE
        for ((berry, growthPoint) in entity.berryAndGrowthPoint()) {
            val model =
                (if (isFlower) BerryModelRepository.modelOf(berry.flowerModelIdentifier) else BerryModelRepository.modelOf(
                    berry.fruitModelIdentifier
                )) ?: continue
            model.setAngles(
                Math.toRadians(180.0 - growthPoint.rotation.x).toFloat(),
                Math.toRadians(180.0 + growthPoint.rotation.y).toFloat(),
                Math.toRadians(growthPoint.rotation.z).toFloat()
            )
            model.setPosition(Axis.X_AXIS.ordinal, growthPoint.position.x.toFloat())
            model.setPosition(Axis.Y_AXIS.ordinal, growthPoint.position.y.toFloat())
            model.setPosition(Axis.Z_AXIS.ordinal, growthPoint.position.z.toFloat())
            model.render(MatrixStack(), tessellator.buffer, light, overlay)
        }
        val builtBuffer = tessellator.buffer.end()
        val vertexBuffer = VertexBuffer(VertexBuffer.Usage.STATIC)
        vertexBuffer.bind()
        vertexBuffer.upload(builtBuffer)
        VertexBuffer.unbind()
        vertexBufferMap[entity.pos] = vertexBuffer
    }
}
