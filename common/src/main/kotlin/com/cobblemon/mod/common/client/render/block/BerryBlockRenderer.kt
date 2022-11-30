package com.cobblemon.mod.common.client.render.block

import com.cobblemon.mod.common.world.block.entity.BerryBlockEntity
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack

class BerryBlockRenderer(private val context: BlockEntityRendererFactory.Context) : BlockEntityRenderer<BerryBlockEntity> {

    override fun render(entity: BerryBlockEntity, tickDelta: Float, matrices: MatrixStack, vertexConsumers: VertexConsumerProvider?, light: Int, overlay: Int) {
        val blockState = entity.cachedState
        val berryBlock = entity.berryBlock()
        val berry = entity.berry()
    }

}