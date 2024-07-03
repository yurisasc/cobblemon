package com.cobblemon.mod.common.client.render.block

import com.cobblemon.mod.common.block.TMBlock
import com.cobblemon.mod.common.block.entity.TMBlockEntity
import com.cobblemon.mod.common.client.render.models.blockbench.repository.MiscModelRepository
import com.cobblemon.mod.common.item.components.TMMoveComponent
import com.cobblemon.mod.common.util.cobblemonResource
import java.awt.Color
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Direction
import net.minecraft.util.math.RotationAxis

class TMBlockRenderer(context: BlockEntityRendererFactory.Context) : BlockEntityRenderer<TMBlockEntity> {
    //TODO Do the render optimizations that we do for berries and fossils
    override fun render(
        entity: TMBlockEntity,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {
        val diskModel = MiscModelRepository.modelOf(MODEL_ID) ?: return
        matrices.push()
        val tm = entity.tmmInventory.filterTM ?: return
        entity.partialTicks += tickDelta
        val color = Color(tm.elementalType.hue)
        when (entity.cachedState.get(TMBlock.FACING)) {
            Direction.SOUTH -> matrices.translate(15F / 16F, 5.5F / 16F, 1F / 16F)
            Direction.WEST -> matrices.translate(14F / 16F, 5.5F / 16F, 0F)
            Direction.EAST -> matrices.translate(1F, 5.5F / 16F, 0F)
            else -> matrices.translate(15F / 16F, 5.5F / 16F, -1F / 16F)
        }

        matrices.translate(-7.0/16f, 0.0, 8.0/16f)
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees( entity.partialTicks / 0.5f))
        matrices.translate(7.0/16f, 0.0, -8.0/16f)

        val colour = (255 shl 24) or (color.red shl 16) or (color.green shl 8) or color.blue

        val renderLayer = RenderLayer.getEntityCutout(cobblemonResource("textures/block/tm_machine.png"))
        diskModel.render(
            matrices,
            vertexConsumers.getBuffer(renderLayer),
            light,
            overlay,
            colour
        )
        matrices.pop()
    }

    companion object {
        val MODEL_ID = cobblemonResource("tm_disk.geo")
    }

}