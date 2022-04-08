package com.cablemc.pokemoncobbled.common.client.render.block

import com.cablemc.pokemoncobbled.common.CobbledBlocks
import com.cablemc.pokemoncobbled.common.CobbledItems
import com.cablemc.pokemoncobbled.common.world.level.block.entity.HealingMachineBlockEntity
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Vector3f
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.block.model.ItemTransforms
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.Direction
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

class HealingMachineRenderer<T: BlockEntity>(ctx: BlockEntityRendererProvider.Context): BlockEntityRenderer<T> {
    companion object {
        private val offsets = listOf(
            0.0 to 0.0,
            -0.64 to 0.0,
            0.64 to -0.5,
            -0.64 to 0.0,
            0.64 to -0.5,
            -0.64 to 0.0
        )
    }

    override fun render(blockEntity: T, tickDelta: Float, poseStack: PoseStack, multiBufferSource: MultiBufferSource, light: Int, overlay: Int) {
        if(blockEntity !is HealingMachineBlockEntity) return

        poseStack.pushPose()

        val blockState =
            if (blockEntity.level != null) blockEntity.blockState else (CobbledBlocks.HEALING_MACHINE.get().defaultBlockState().setValue(
                HorizontalDirectionalBlock.FACING, Direction.SOUTH
            ) as BlockState)
        val yRot = blockState.getValue(HorizontalDirectionalBlock.FACING).toYRot()

        // Position balls
        poseStack.translate(0.5, 0.5, 0.5)
        poseStack.mulPose(Vector3f.YP.rotationDegrees(-yRot))
        poseStack.scale(0.5f, 0.5f, 0.5f)
        poseStack.translate(0.3, 0.4, 0.23)

        for((index, pokeBall) in blockEntity.pokeBalls.withIndex()) {
            val offset = offsets[index]
            poseStack.translate(offset.first, 0.0, offset.second)
            Minecraft.getInstance().itemRenderer.renderStatic(ItemStack(CobbledItems.ballMap[pokeBall]?.get() ?: Items.AIR), ItemTransforms.TransformType.GROUND, light, overlay, poseStack, multiBufferSource, 0)
        }
        poseStack.popPose()
    }
}