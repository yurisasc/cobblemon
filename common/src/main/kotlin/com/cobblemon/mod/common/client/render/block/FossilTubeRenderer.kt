package com.cobblemon.mod.common.client.render.block

import com.cobblemon.mod.common.block.entity.FossilTubeEntity
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.util.ModelIdentifier
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier

class FossilTubeRenderer : BlockEntityRenderer<FossilTubeEntity> {
    override fun render(
        entity: FossilTubeEntity?,
        tickDelta: Float,
        matrices: MatrixStack?,
        vertexConsumers: VertexConsumerProvider?,
        light: Int,
        overlay: Int
    ) {
        TODO("Not yet implemented")
    }

    companion object {
    }
}