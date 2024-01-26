package com.cobblemon.mod.common.client.render.block

import com.cobblemon.mod.common.api.text.blue
import com.cobblemon.mod.common.api.text.green
import com.cobblemon.mod.common.api.text.red
import com.cobblemon.mod.common.api.tms.TechnicalMachines
import com.cobblemon.mod.common.api.types.ElementalTypes
import com.cobblemon.mod.common.block.entity.TMBlockEntity
import com.cobblemon.mod.common.client.CobblemonBakingOverrides
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import java.awt.Color

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
        matrices.push()
        val tm = entity.tmmInventory.filterTM?.let {
            TechnicalMachines.getTechnicalMachineFromStack(
                it
            )
        }
        val color = tm?.let { Color(ElementalTypes.get(it.type)!!.hue) } ?: Color.WHITE
        matrices.translate(0F, 0.3435F, 0F)
        MinecraftClient.getInstance().blockRenderManager.modelRenderer.render(
            matrices.peek(),
            vertexConsumers.getBuffer(RenderLayer.getCutoutMipped()),
            null,
            CobblemonBakingOverrides.TM_DISK.getModel(),
            color.red.toFloat(),
            color.green.toFloat(),
            color.blue.toFloat(),
            light,
            overlay
        )
        matrices.pop()
    }

}